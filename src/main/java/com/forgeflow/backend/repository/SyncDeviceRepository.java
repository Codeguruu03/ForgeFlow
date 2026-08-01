package com.forgeflow.backend.repository;

import com.forgeflow.backend.model.SyncDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncDeviceRepository extends JpaRepository<SyncDevice, Long> {
    Optional<SyncDevice> findByDeviceId(String deviceId);
}
