package com.forgeflow.backend.model;

import com.forgeflow.shared.enums.JobStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_history")
public class JobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobId;

    private String jobName;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private Long executionTimeMs;

    @Column(length = 2000)
    private String resultMessage;

    private String executedByWorker;

    private LocalDateTime executedAt;

    public JobHistory() {}

    public JobHistory(Long jobId, String jobName, JobStatus status, Long executionTimeMs, String resultMessage, String executedByWorker) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.status = status;
        this.executionTimeMs = executionTimeMs;
        this.resultMessage = resultMessage;
        this.executedByWorker = executedByWorker;
        this.executedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }

    public String getExecutedByWorker() { return executedByWorker; }
    public void setExecutedByWorker(String executedByWorker) { this.executedByWorker = executedByWorker; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }
}
