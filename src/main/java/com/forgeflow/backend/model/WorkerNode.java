package com.forgeflow.backend.model;

import com.forgeflow.shared.enums.WorkerStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "worker_nodes")
public class WorkerNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String workerId;

    private String host;

    private Integer port;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkerStatus status = WorkerStatus.IDLE;

    private Double cpuLoad = 0.0;

    private Double memoryUsage = 0.0;

    private Integer activeTasks = 0;

    private LocalDateTime lastHeartbeat;

    public WorkerNode() {}

    public WorkerNode(String workerId, String host, Integer port) {
        this.workerId = workerId;
        this.host = host;
        this.port = port;
        this.status = WorkerStatus.IDLE;
        this.lastHeartbeat = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Integer getPort() { return port; }
    public void setPort(Integer port) { this.port = port; }

    public WorkerStatus getStatus() { return status; }
    public void setStatus(WorkerStatus status) { this.status = status; }

    public Double getCpuLoad() { return cpuLoad; }
    public void setCpuLoad(Double cpuLoad) { this.cpuLoad = cpuLoad; }

    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }

    public Integer getActiveTasks() { return activeTasks; }
    public void setActiveTasks(Integer activeTasks) { this.activeTasks = activeTasks; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
}
