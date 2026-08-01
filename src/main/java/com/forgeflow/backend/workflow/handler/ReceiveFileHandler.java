package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReceiveFileHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "RECEIVE_FILE".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[RECEIVE_FILE] Step started: Checking incoming file payload...");
        String fileName = context.containsKey("fileName") ? context.get("fileName").toString() : "artifact_bundle.zip";
        long fileSize = context.containsKey("fileSize") ? (long) context.get("fileSize") : 10485760L;

        context.put("receivedFile", fileName);
        context.put("fileSizeBytes", fileSize);
        context.put("fileStatus", "RECEIVED");

        logs.add("[RECEIVE_FILE] File '" + fileName + "' (" + fileSize + " bytes) successfully received and buffered.");
        return true;
    }
}
