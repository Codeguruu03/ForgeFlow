package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.DependencyProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DependencyProjectRepository extends JpaRepository<DependencyProject, Long> {
    Optional<DependencyProject> findByProjectName(String projectName);
}
