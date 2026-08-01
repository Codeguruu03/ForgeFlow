package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.Workflow;
import com.forgeflow.shared.enums.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByStatus(WorkflowStatus status);
    List<Workflow> findByAuthor(String author);
}
