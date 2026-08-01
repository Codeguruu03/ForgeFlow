package com.forgeflow.backend.controller;

import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.repository.*;
import com.forgeflow.shared.enums.JobStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final WorkflowRepository workflowRepository;
    private final JobRepository jobRepository;
    private final WorkerNodeRepository workerNodeRepository;
    private final AuditLogRepository auditLogRepository;

    public DashboardController(WorkflowRepository workflowRepository,
                               JobRepository jobRepository,
                               WorkerNodeRepository workerNodeRepository,
                               AuditLogRepository auditLogRepository) {
        this.workflowRepository = workflowRepository;
        this.jobRepository = jobRepository;
        this.workerNodeRepository = workerNodeRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalWorkflows = workflowRepository.count();
        long totalJobs = jobRepository.count();
        long runningJobs = jobRepository.findByStatus(JobStatus.RUNNING).size();
        long failedJobs = jobRepository.findByStatus(JobStatus.DEAD_LETTER).size();
        long totalWorkers = workerNodeRepository.count();
        List<AuditLog> recentLogs = auditLogRepository.findTop50ByOrderByTimestampDesc();

        stats.put("totalWorkflows", totalWorkflows);
        stats.put("totalJobs", totalJobs);
        stats.put("runningJobs", runningJobs);
        stats.put("failedJobs", failedJobs);
        stats.put("totalWorkers", totalWorkers);
        stats.put("recentLogs", recentLogs);

        // System telemetry
        Runtime runtime = Runtime.getRuntime();
        double usedMemoryMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0);
        double maxMemoryMb = runtime.maxMemory() / (1024.0 * 1024.0);

        stats.put("usedMemoryMb", Math.round(usedMemoryMb * 10.0) / 10.0);
        stats.put("maxMemoryMb", Math.round(maxMemoryMb * 10.0) / 10.0);
        stats.put("availableProcessors", runtime.availableProcessors());

        return ResponseEntity.ok(stats);
    }
}
