package com.forgeflow.backend.controller;

import com.forgeflow.backend.model.WorkerNode;
import com.forgeflow.backend.repository.WorkerNodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    private final WorkerNodeRepository workerNodeRepository;

    public WorkerController(WorkerNodeRepository workerNodeRepository) {
        this.workerNodeRepository = workerNodeRepository;
    }

    @GetMapping
    public ResponseEntity<List<WorkerNode>> getWorkers() {
        return ResponseEntity.ok(workerNodeRepository.findAll());
    }

    @GetMapping("/{workerId}")
    public ResponseEntity<WorkerNode> getWorkerById(@PathVariable String workerId) {
        return workerNodeRepository.findByWorkerId(workerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
