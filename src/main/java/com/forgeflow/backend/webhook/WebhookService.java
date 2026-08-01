package com.forgeflow.backend.webhook;

import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.model.Workflow;
import com.forgeflow.backend.repository.AuditLogRepository;
import com.forgeflow.backend.repository.WorkflowRepository;
import com.forgeflow.backend.workflow.engine.WorkflowEngineService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebhookService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowEngineService workflowEngineService;
    private final AuditLogRepository auditLogRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public WebhookService(WorkflowRepository workflowRepository,
                          WorkflowEngineService workflowEngineService,
                          AuditLogRepository auditLogRepository) {
        this.workflowRepository = workflowRepository;
        this.workflowEngineService = workflowEngineService;
        this.auditLogRepository = auditLogRepository;
    }

    public Map<String, Object> handleGithubWebhook(String eventType, Map<String, Object> payload) {
        Map<String, Object> response = new HashMap<>();
        response.put("event", eventType);

        String ref = payload.containsKey("ref") ? payload.get("ref").toString() : "refs/heads/main";
        auditLogRepository.save(new AuditLog("github-webhook", "GITHUB_EVENT_" + eventType.toUpperCase(), "WEBHOOK", "Received GitHub " + eventType + " event on " + ref));

        // Find active workflow to execute on push / release
        List<Workflow> workflows = workflowRepository.findAll();
        if (!workflows.isEmpty()) {
            Workflow targetWf = workflows.get(0);
            Map<String, Object> context = Map.of(
                    "triggerSource", "GitHub Webhook (" + eventType + ")",
                    "gitRef", ref
            );
            WorkflowExecutionResult wfResult = workflowEngineService.executeWorkflow(targetWf.getId(), context);
            response.put("triggeredWorkflowId", targetWf.getId());
            response.put("executionResult", wfResult);
        } else {
            response.put("message", "No active workflow registered to trigger.");
        }

        return response;
    }

    public boolean sendSlackNotification(String webhookUrl, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("text", "🚨 *[ForgeFlow Alert]* " + message);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            auditLogRepository.save(new AuditLog("system", "SLACK_NOTIFY", "WEBHOOK", "Slack alert dispatched: " + message));
            return true;
        } catch (Exception e) {
            auditLogRepository.save(new AuditLog("system", "SLACK_NOTIFY_ERROR", "WEBHOOK", "Failed to dispatch Slack alert: " + e.getMessage()));
            return false;
        }
    }

    public boolean sendDiscordNotification(String webhookUrl, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> body = Map.of(
                    "content", "⚡ **[ForgeFlow Notification]** " + message
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(webhookUrl, request, String.class);
            auditLogRepository.save(new AuditLog("system", "DISCORD_NOTIFY", "WEBHOOK", "Discord notification dispatched: " + message));
            return true;
        } catch (Exception e) {
            auditLogRepository.save(new AuditLog("system", "DISCORD_NOTIFY_ERROR", "WEBHOOK", "Failed to dispatch Discord notification: " + e.getMessage()));
            return false;
        }
    }
}
