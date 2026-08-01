package com.forgeflow.backend.scheduler;

import com.forgeflow.backend.model.WorkerNode;
import com.forgeflow.backend.repository.WorkerNodeRepository;
import com.forgeflow.shared.enums.WorkerStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class LeaderElectionManager {

    private final WorkerNodeRepository workerNodeRepository;
    private final String localWorkerId = "worker-node-1"; // Local instance default node

    public LeaderElectionManager(WorkerNodeRepository workerNodeRepository) {
        this.workerNodeRepository = workerNodeRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void performHeartbeatAndElection() {
        LocalDateTime now = LocalDateTime.now();

        // 1. Update local worker node heartbeat
        WorkerNode localNode = workerNodeRepository.findByWorkerId(localWorkerId)
                .orElseGet(() -> new WorkerNode(localWorkerId, "127.0.0.1", 8081));
        localNode.setLastHeartbeat(now);
        localNode.setCpuLoad(Math.round((10.0 + Math.random() * 20.0) * 10.0) / 10.0);
        localNode.setMemoryUsage(Math.round((30.0 + Math.random() * 15.0) * 10.0) / 10.0);
        workerNodeRepository.save(localNode);

        // 2. Mark dead workers (heartbeat older than 15 seconds)
        List<WorkerNode> allNodes = workerNodeRepository.findAll();
        boolean hasLeader = false;

        for (WorkerNode node : allNodes) {
            if (node.getLastHeartbeat() == null || node.getLastHeartbeat().isBefore(now.minusSeconds(15))) {
                if (node.getStatus() != WorkerStatus.OFFLINE) {
                    node.setStatus(WorkerStatus.OFFLINE);
                    workerNodeRepository.save(node);
                }
            } else if (node.getStatus() == WorkerStatus.LEADER) {
                hasLeader = true;
            }
        }

        // 3. If no active leader exists, elect local worker node as leader
        if (!hasLeader) {
            localNode.setStatus(WorkerStatus.LEADER);
            workerNodeRepository.save(localNode);
        }
    }

    public boolean isCurrentNodeLeader() {
        return workerNodeRepository.findByWorkerId(localWorkerId)
                .map(n -> n.getStatus() == WorkerStatus.LEADER)
                .orElse(true);
    }
}
