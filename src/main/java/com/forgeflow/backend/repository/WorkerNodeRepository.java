package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.WorkerNode;
import com.forgeflow.shared.enums.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerNodeRepository extends JpaRepository<WorkerNode, Long> {
    Optional<WorkerNode> findByWorkerId(String workerId);
    List<WorkerNode> findByStatus(WorkerStatus status);
}
