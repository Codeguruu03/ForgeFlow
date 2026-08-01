package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import java.util.List;
import java.util.Map;

public interface NodeExecutionHandler {
    boolean supports(String nodeType);
    boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs);
}
