package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.FileSync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileSyncRepository extends JpaRepository<FileSync, Long> {
    Optional<FileSync> findByPath(String path);
    Optional<FileSync> findByHashSha256(String hashSha256);
}
