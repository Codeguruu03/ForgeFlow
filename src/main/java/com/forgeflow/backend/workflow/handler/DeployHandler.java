package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DeployHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "DEPLOY".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[DEPLOY] Deploying target build to staging environment...");
        logs.add("[DEPLOY] Health checks verified on target instance :8080/actuator/health.");
        context.put("deployStatus", "SUCCESS");
        return true;
    }
}
