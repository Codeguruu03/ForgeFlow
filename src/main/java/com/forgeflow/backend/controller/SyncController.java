package com.forgeflow.backend.controller;

import com.forgeflow.backend.model.FileSync;
import com.forgeflow.backend.repository.FileSyncRepository;
import com.forgeflow.backend.sync.LocalSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sync")
public class SyncController {

    private final LocalSyncService syncService;
    private final FileSyncRepository fileSyncRepository;

    public SyncController(LocalSyncService syncService, FileSyncRepository fileSyncRepository) {
        this.syncService = syncService;
        this.fileSyncRepository = fileSyncRepository;
    }

    @PostMapping("/trigger")
    public ResponseEntity<LocalSyncService.SyncReport> triggerSync(@RequestBody Map<String, String> request) {
        String sourcePath = request.get("sourcePath");
        String targetPath = request.get("targetPath");

        if (sourcePath == null || targetPath == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            LocalSyncService.SyncReport report = syncService.synchronizeFolders(sourcePath, targetPath);
            return ResponseEntity.ok(report);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileSync>> getSyncedFiles() {
        return ResponseEntity.ok(fileSyncRepository.findAll());
    }
}
