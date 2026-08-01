package com.forgeflow.backend.workflow.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowExecutionResult {
    private Long workflowId;
    private String workflowName;
    private boolean success;
    private String statusMessage;
    private long executionTimeMs;
    private List<String> logs = new ArrayList<>();
    private Map<String, Object> outputContext = new HashMap<>();
    private LocalDateTime timestamp = LocalDateTime.now();

    public WorkflowExecutionResult() {}

    public WorkflowExecutionResult(Long workflowId, String workflowName) {
        this.workflowId = workflowId;
        this.workflowName = workflowName;
    }

    public Long getWorkflowId() { return workflowId; }
    public void setWorkflowId(Long workflowId) { this.workflowId = workflowId; }

    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public List<String> getLogs() { return logs; }
    public void setLogs(List<String> logs) { this.logs = logs; }

    public Map<String, Object> getOutputContext() { return outputContext; }
    public void setOutputContext(Map<String, Object> outputContext) { this.outputContext = outputContext; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public void addLog(String log) { this.logs.add(log); }
}
