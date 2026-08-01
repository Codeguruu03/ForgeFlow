package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.Job;
import com.forgeflow.shared.enums.JobStatus;
import com.forgeflow.shared.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);
    List<Job> findByJobType(JobType jobType);
    List<Job> findByStatusOrderByPriorityDesc(JobStatus status);
}
