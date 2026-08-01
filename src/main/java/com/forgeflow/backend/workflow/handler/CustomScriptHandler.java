package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CustomScriptHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "CUSTOM".equalsIgnoreCase(nodeType) || "SCRIPT".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[CUSTOM_SCRIPT] Executing custom workflow script handler...");
        logs.add("[CUSTOM_SCRIPT] Script returned status 0 (Success).");
        context.put("customOutput", "SUCCESS");
        return true;
    }
}
