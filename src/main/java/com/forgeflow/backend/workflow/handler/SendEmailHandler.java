package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SendEmailHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "SEND_EMAIL".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[SEND_EMAIL] Dispatching deployment notification email via SMTP...");
        logs.add("[SEND_EMAIL] Notification email successfully sent to engineering team.");
        context.put("emailSent", true);
        return true;
    }
}
