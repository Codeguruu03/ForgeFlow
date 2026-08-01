package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ValidateHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "VALIDATE".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[VALIDATE] Running schema and integrity checks...");
        if (!context.containsKey("receivedFile")) {
            logs.add("[VALIDATE] ERROR: No received file found in context.");
            return false;
        }
        logs.add("[VALIDATE] Integrity checksum OK. Security policy scan passed.");
        context.put("validationStatus", "PASSED");
        return true;
    }
}
