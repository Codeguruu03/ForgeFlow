package com.forgeflow.backend.model;

import com.forgeflow.shared.enums.JobStatus;
import com.forgeflow.shared.enums.JobType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String cronExpression;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobType jobType;

    private String target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.SCHEDULED;

    private Integer priority = 5;

    private Integer maxRetries = 3;

    private Integer retryCount = 0;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    private LocalDateTime lastRunTime;

    private LocalDateTime nextRunTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Job() {}

    public Job(String name, String cronExpression, JobType jobType, String target, String payloadJson) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.jobType = jobType;
        this.target = target;
        this.payloadJson = payloadJson;
        this.status = JobStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public LocalDateTime getLastRunTime() { return lastRunTime; }
    public void setLastRunTime(LocalDateTime lastRunTime) { this.lastRunTime = lastRunTime; }

    public LocalDateTime getNextRunTime() { return nextRunTime; }
    public void setNextRunTime(LocalDateTime nextRunTime) { this.nextRunTime = nextRunTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
