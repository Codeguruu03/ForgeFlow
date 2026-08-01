package com.forgeflow.backend.controller;

import com.forgeflow.backend.model.Workflow;
import com.forgeflow.backend.repository.WorkflowRepository;
import com.forgeflow.backend.workflow.engine.WorkflowEngineService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import com.forgeflow.backend.workflow.model.WorkflowValidationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowRepository workflowRepository;
    private final WorkflowEngineService workflowEngineService;

    public WorkflowController(WorkflowRepository workflowRepository, WorkflowEngineService workflowEngineService) {
        this.workflowRepository = workflowRepository;
        this.workflowEngineService = workflowEngineService;
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        return ResponseEntity.ok(workflowRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@RequestBody Workflow workflow) {
        Workflow saved = workflowRepository.save(workflow);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflowById(@PathVariable Long id) {
        return workflowRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @RequestBody Workflow updated) {
        return workflowRepository.findById(id)
                .map(wf -> {
                    wf.setName(updated.getName());
                    wf.setDescription(updated.getDescription());
                    wf.setJsonDefinition(updated.getJsonDefinition());
                    wf.setStatus(updated.getStatus());
                    wf.setVersion(wf.getVersion() + 1);
                    return ResponseEntity.ok(workflowRepository.save(wf));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/validate")
    public ResponseEntity<WorkflowValidationResult> validateWorkflow(@PathVariable Long id) {
        return workflowRepository.findById(id)
                .map(wf -> ResponseEntity.ok(workflowEngineService.validateWorkflow(wf)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<WorkflowExecutionResult> executeWorkflow(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> initialContext) {
        WorkflowExecutionResult result = workflowEngineService.executeWorkflow(id, initialContext);
        return ResponseEntity.ok(result);
    }
}
