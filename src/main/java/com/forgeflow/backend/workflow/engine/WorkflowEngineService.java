package com.forgeflow.backend.workflow.engine;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.model.Workflow;
import com.forgeflow.backend.repository.AuditLogRepository;
import com.forgeflow.backend.repository.WorkflowRepository;
import com.forgeflow.backend.workflow.handler.NodeExecutionHandler;
import com.forgeflow.backend.workflow.model.*;
import com.forgeflow.backend.workflow.validator.WorkflowDagValidator;
import com.forgeflow.shared.enums.WorkflowStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WorkflowEngineService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowDagValidator dagValidator;
    private final List<NodeExecutionHandler> nodeHandlers;
    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkflowEngineService(WorkflowRepository workflowRepository,
                                 WorkflowDagValidator dagValidator,
                                 List<NodeExecutionHandler> nodeHandlers,
                                 AuditLogRepository auditLogRepository) {
        this.workflowRepository = workflowRepository;
        this.dagValidator = dagValidator;
        this.nodeHandlers = nodeHandlers;
        this.auditLogRepository = auditLogRepository;
    }

    public WorkflowValidationResult validateWorkflow(Workflow workflow) {
        try {
            if (workflow.getJsonDefinition() == null || workflow.getJsonDefinition().isBlank()) {
                WorkflowValidationResult emptyRes = new WorkflowValidationResult();
                emptyRes.addError("Workflow definition is empty.");
                return emptyRes;
            }

            Map<String, Object> map = objectMapper.readValue(workflow.getJsonDefinition(), new TypeReference<>() {});
            List<NodeDefinition> nodes = objectMapper.convertValue(map.get("nodes"), new TypeReference<>() {});
            List<EdgeDefinition> edges = objectMapper.convertValue(map.get("edges"), new TypeReference<>() {});

            return dagValidator.validate(nodes, edges);
        } catch (Exception e) {
            WorkflowValidationResult errRes = new WorkflowValidationResult();
            errRes.addError("Invalid JSON structure: " + e.getMessage());
            return errRes;
        }
    }

    public WorkflowExecutionResult executeWorkflow(Long workflowId, Map<String, Object> initialContext) {
        long startTime = System.currentTimeMillis();
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found with ID: " + workflowId));

        WorkflowExecutionResult result = new WorkflowExecutionResult(workflow.getId(), workflow.getName());
        Map<String, Object> context = initialContext != null ? new HashMap<>(initialContext) : new HashMap<>();

        try {
            workflow.setStatus(WorkflowStatus.EXECUTING);
            workflowRepository.save(workflow);

            result.addLog("[ENGINE] Initiating workflow: " + workflow.getName() + " (v" + workflow.getVersion() + ")");

            WorkflowValidationResult validation = validateWorkflow(workflow);
            if (!validation.isValid()) {
                result.setSuccess(false);
                result.setStatusMessage("Validation failed: " + String.join("; ", validation.getErrors()));
                result.getLogs().addAll(validation.getErrors());
                workflow.setStatus(WorkflowStatus.ACTIVE);
                workflowRepository.save(workflow);
                return result;
            }

            Map<String, Object> map = objectMapper.readValue(workflow.getJsonDefinition(), new TypeReference<>() {});
            List<NodeDefinition> nodes = objectMapper.convertValue(map.get("nodes"), new TypeReference<>() {});

            for (NodeDefinition node : nodes) {
                result.addLog("[ENGINE] Executing Node ID: " + node.getId() + " [" + node.getType() + " - " + node.getLabel() + "]");
                NodeExecutionHandler handler = nodeHandlers.stream()
                        .filter(h -> h.supports(node.getType()))
                        .findFirst()
                        .orElse(null);

                if (handler == null) {
                    result.addLog("[ENGINE] WARNING: No specialized handler found for type '" + node.getType() + "'. Using default execution handler.");
                    result.addLog("[" + node.getType() + "] Default pass-through executed for node '" + node.getLabel() + "'.");
                    continue;
                }

                boolean stepSuccess = handler.execute(node, context, result.getLogs());
                if (!stepSuccess) {
                    result.setSuccess(false);
                    result.setStatusMessage("Execution failed at node: " + node.getLabel());
                    workflow.setStatus(WorkflowStatus.ACTIVE);
                    workflowRepository.save(workflow);
                    return result;
                }
            }

            result.setSuccess(true);
            result.setStatusMessage("Workflow executed successfully.");
            result.setOutputContext(context);
            workflow.setStatus(WorkflowStatus.ACTIVE);
            workflowRepository.save(workflow);

            auditLogRepository.save(new AuditLog("system", "WORKFLOW_EXECUTE", "PROCESSFLOW", "Successfully executed workflow '" + workflow.getName() + "'"));

        } catch (Exception e) {
            result.setSuccess(false);
            result.setStatusMessage("Execution error: " + e.getMessage());
            result.addLog("[ENGINE] Exception encountered: " + e.getMessage());
            workflow.setStatus(WorkflowStatus.ACTIVE);
            workflowRepository.save(workflow);
        } finally {
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        }

        return result;
    }
}
