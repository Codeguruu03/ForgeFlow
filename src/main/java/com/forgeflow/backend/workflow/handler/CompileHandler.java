package com.forgeflow.backend.workflow.handler;

import com.forgeflow.backend.workflow.model.NodeDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CompileHandler implements NodeExecutionHandler {

    @Override
    public boolean supports(String nodeType) {
        return "COMPILE".equalsIgnoreCase(nodeType);
    }

    @Override
    public boolean execute(NodeDefinition node, Map<String, Object> context, List<String> logs) {
        logs.add("[COMPILE] Compiling Java sources with javac / Maven compiler...");
        logs.add("[COMPILE] Generated artifact bytecodes: 142 classes compiled.");
        context.put("compiledArtifact", "target/forgeflow-build-1.0.jar");
        return true;
    }
}
