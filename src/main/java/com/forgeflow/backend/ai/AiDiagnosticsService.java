package com.forgeflow.backend.ai;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiDiagnosticsService {

    public Map<String, Object> diagnoseError(String errorContext, String stackTrace) {
        Map<String, Object> analysis = new HashMap<>();

        analysis.put("errorContext", errorContext);
        analysis.put("severity", determineSeverity(stackTrace));
        analysis.put("confidenceScore", 0.94);
        analysis.put("rootCauseCategory", categorizeError(stackTrace));

        String rootCauseExplanation;
        String recommendedFix;
        List<String> actionSteps;

        if (stackTrace.contains("ConnectException") || stackTrace.contains("Connection refused")) {
            rootCauseExplanation = "Target service host or port is unreachable. Network connection refused.";
            recommendedFix = "Verify target service health and firewall policies. Ensure host endpoint URL is correctly configured.";
            actionSteps = List.of(
                    "Check target host IP and port availability using telnet/ping",
                    "Verify security group rules and proxy settings",
                    "Configure automatic retry delay in ForgeFlow Scheduler"
            );
        } else if (stackTrace.contains("NullPointerException") || stackTrace.contains("NPE")) {
            rootCauseExplanation = "Attempted to access object reference evaluated to null in workflow execution context.";
            recommendedFix = "Add null-check guards or specify default configuration fallback values in node payload.";
            actionSteps = List.of(
                    "Validate node payload JSON schema before DAG execution",
                    "Check node execution handler context initialization"
            );
        } else {
            rootCauseExplanation = "Unspecified runtime anomaly encountered during execution pipeline.";
            recommendedFix = "Inspect node execution logs and enable DEBUG level logging in application.properties.";
            actionSteps = List.of(
                    "Review full stack trace details in H2 console / AUDIT_LOGS table",
                    "Re-trigger failed DAG node with sample payload"
            );
        }

        analysis.put("rootCauseExplanation", rootCauseExplanation);
        analysis.put("recommendedFix", recommendedFix);
        analysis.put("actionSteps", actionSteps);

        return analysis;
    }

    private String determineSeverity(String stackTrace) {
        if (stackTrace.contains("OutOfMemory") || stackTrace.contains("ConnectException") || stackTrace.contains("AccessDenied")) {
            return "HIGH";
        }
        return "MEDIUM";
    }

    private String categorizeError(String stackTrace) {
        if (stackTrace.contains("ConnectException")) return "NETWORK_INFRASTRUCTURE";
        if (stackTrace.contains("NullPointerException")) return "NULL_POINTER_EXCEPTION";
        if (stackTrace.contains("SecurityException")) return "SECURITY_AUTHORIZATION";
        return "GENERAL_RUNTIME_ERROR";
    }
}
