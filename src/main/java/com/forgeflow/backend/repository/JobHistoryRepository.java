package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {
    List<JobHistory> findByJobIdOrderByExecutedAtDesc(Long jobId);
    List<JobHistory> findTop20ByOrderByExecutedAtDesc();
}
