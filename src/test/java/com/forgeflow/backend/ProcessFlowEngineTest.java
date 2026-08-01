package com.forgeflow.backend;

import com.forgeflow.backend.model.Workflow;
import com.forgeflow.backend.repository.WorkflowRepository;
import com.forgeflow.backend.workflow.engine.WorkflowEngineService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import com.forgeflow.backend.workflow.model.WorkflowValidationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProcessFlowEngineTest {

    @Autowired
    private WorkflowEngineService workflowEngineService;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Test
    void testWorkflowValidationAndExecution() {
        Workflow workflow = new Workflow("Test Pipeline", "Test description", "admin");
        workflow.setJsonDefinition("{\"nodes\":[{\"id\":\"1\",\"type\":\"RECEIVE_FILE\",\"label\":\"Receive File\"},{\"id\":\"2\",\"type\":\"VALIDATE\",\"label\":\"Validate Format\"}],\"edges\":[{\"from\":\"1\",\"to\":\"2\"}]}");
        Workflow saved = workflowRepository.save(workflow);

        WorkflowValidationResult validation = workflowEngineService.validateWorkflow(saved);
        assertTrue(validation.isValid());
        assertTrue(validation.getErrors().isEmpty());

        WorkflowExecutionResult execution = workflowEngineService.executeWorkflow(saved.getId(), null);
        assertTrue(execution.isSuccess());
        assertNotNull(execution.getStatusMessage());
        assertFalse(execution.getLogs().isEmpty());
    }
}
