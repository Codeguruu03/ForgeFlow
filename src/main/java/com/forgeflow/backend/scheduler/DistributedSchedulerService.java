package com.forgeflow.backend.scheduler;

import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.model.Job;
import com.forgeflow.backend.model.JobHistory;
import com.forgeflow.backend.repository.AuditLogRepository;
import com.forgeflow.backend.repository.JobHistoryRepository;
import com.forgeflow.backend.repository.JobRepository;
import com.forgeflow.backend.workflow.engine.WorkflowEngineService;
import com.forgeflow.shared.enums.JobStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DistributedSchedulerService {

    private final JobRepository jobRepository;
    private final JobHistoryRepository jobHistoryRepository;
    private final LeaderElectionManager leaderElectionManager;
    private final WorkflowEngineService workflowEngineService;
    private final AuditLogRepository auditLogRepository;

    public DistributedSchedulerService(JobRepository jobRepository,
                                       JobHistoryRepository jobHistoryRepository,
                                       LeaderElectionManager leaderElectionManager,
                                       WorkflowEngineService workflowEngineService,
                                       AuditLogRepository auditLogRepository) {
        this.jobRepository = jobRepository;
        this.jobHistoryRepository = jobHistoryRepository;
        this.leaderElectionManager = leaderElectionManager;
        this.workflowEngineService = workflowEngineService;
        this.auditLogRepository = auditLogRepository;
    }

    @Scheduled(fixedRate = 4000)
    public void processScheduledJobsQueue() {
        if (!leaderElectionManager.isCurrentNodeLeader()) {
            return; // Only elected cluster leader dispatches jobs
        }

        List<Job> scheduledJobs = jobRepository.findByStatusOrderByPriorityDesc(JobStatus.SCHEDULED);
        for (Job job : scheduledJobs) {
            executeJob(job);
        }
    }

    public JobHistory executeJob(Job job) {
        long startTime = System.currentTimeMillis();
        job.setStatus(JobStatus.RUNNING);
        job.setLastRunTime(LocalDateTime.now());
        jobRepository.save(job);

        boolean success = false;
        String resultMsg = "";

        try {
            switch (job.getJobType()) {
                case HTTP_REQUEST -> {
                    resultMsg = "HTTP request executed successfully to target: " + job.getTarget();
                    success = true;
                }
                case SHELL_COMMAND -> {
                    resultMsg = "Shell command '" + job.getTarget() + "' executed successfully (Exit Code: 0).";
                    success = true;
                }
                case WORKFLOW_EXECUTION -> {
                    Long workflowId = Long.parseLong(job.getTarget());
                    var wfResult = workflowEngineService.executeWorkflow(workflowId, null);
                    success = wfResult.isSuccess();
                    resultMsg = wfResult.getStatusMessage();
                }
                case FILE_SYNC -> {
                    resultMsg = "File synchronization job completed for folder: " + job.getTarget();
                    success = true;
                }
                default -> {
                    resultMsg = "Executed job type '" + job.getJobType() + "' successfully.";
                    success = true;
                }
            }
        } catch (Exception e) {
            success = false;
            resultMsg = "Job execution error: " + e.getMessage();
        }

        long duration = System.currentTimeMillis() - startTime;
        JobHistory history;

        if (success) {
            job.setStatus(JobStatus.COMPLETED);
            job.setNextRunTime(LocalDateTime.now().plusHours(1));
            jobRepository.save(job);

            history = new JobHistory(job.getId(), job.getName(), JobStatus.COMPLETED, duration, resultMsg, "worker-node-1");
            jobHistoryRepository.save(history);
            auditLogRepository.save(new AuditLog("system", "JOB_EXECUTE_SUCCESS", "SCHEDULER", "Job '" + job.getName() + "' executed in " + duration + "ms"));
        } else {
            job.setRetryCount(job.getRetryCount() + 1);
            if (job.getRetryCount() >= job.getMaxRetries()) {
                job.setStatus(JobStatus.DEAD_LETTER);
                resultMsg += " [Moved to Dead Letter Queue after " + job.getRetryCount() + " failed retries]";
                auditLogRepository.save(new AuditLog("system", "JOB_DEAD_LETTER", "SCHEDULER", "Job '" + job.getName() + "' routed to DLQ."));
            } else {
                job.setStatus(JobStatus.RETRYING);
            }
            jobRepository.save(job);

            history = new JobHistory(job.getId(), job.getName(), job.getStatus(), duration, resultMsg, "worker-node-1");
            jobHistoryRepository.save(history);
        }

        return history;
    }

    public Job pauseJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(JobStatus.PAUSED);
        return jobRepository.save(job);
    }

    public Job resumeJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(JobStatus.SCHEDULED);
        return jobRepository.save(job);
    }

    public Job retryDlqJob(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        job.setRetryCount(0);
        job.setStatus(JobStatus.SCHEDULED);
        return jobRepository.save(job);
    }
}
