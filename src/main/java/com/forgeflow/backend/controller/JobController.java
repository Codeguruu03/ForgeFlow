package com.forgeflow.backend.controller;

import com.forgeflow.backend.model.Job;
import com.forgeflow.backend.model.JobHistory;
import com.forgeflow.backend.repository.JobHistoryRepository;
import com.forgeflow.backend.repository.JobRepository;
import com.forgeflow.backend.scheduler.DistributedSchedulerService;
import com.forgeflow.shared.enums.JobStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final JobHistoryRepository jobHistoryRepository;
    private final DistributedSchedulerService schedulerService;

    public JobController(JobRepository jobRepository,
                         JobHistoryRepository jobHistoryRepository,
                         DistributedSchedulerService schedulerService) {
        this.jobRepository = jobRepository;
        this.jobHistoryRepository = jobHistoryRepository;
        this.schedulerService = schedulerService;
    }

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        Job saved = jobRepository.save(job);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/pause")
    public ResponseEntity<Job> pauseJob(@PathVariable Long id) {
        return ResponseEntity.ok(schedulerService.pauseJob(id));
    }

    @PostMapping("/{id}/resume")
    public ResponseEntity<Job> resumeJob(@PathVariable Long id) {
        return ResponseEntity.ok(schedulerService.resumeJob(id));
    }

    @PostMapping("/{id}/trigger")
    public ResponseEntity<JobHistory> triggerJob(@PathVariable Long id) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
        JobHistory history = schedulerService.executeJob(job);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/dlq")
    public ResponseEntity<List<Job>> getDlqJobs() {
        return ResponseEntity.ok(jobRepository.findByStatus(JobStatus.DEAD_LETTER));
    }

    @PostMapping("/dlq/{id}/retry")
    public ResponseEntity<Job> retryDlqJob(@PathVariable Long id) {
        return ResponseEntity.ok(schedulerService.retryDlqJob(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<JobHistory>> getJobHistory() {
        return ResponseEntity.ok(jobHistoryRepository.findTop20ByOrderByExecutedAtDesc());
    }
}
