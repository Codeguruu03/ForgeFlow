package com.forgeflow.backend.sync;

import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.model.FileSync;
import com.forgeflow.backend.repository.AuditLogRepository;
import com.forgeflow.backend.repository.FileSyncRepository;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Service
public class LocalSyncService {

    private final FileSyncRepository fileSyncRepository;
    private final AuditLogRepository auditLogRepository;
    private static final String AES_KEY = "ForgeFlowSync123"; // 16 bytes key

    public LocalSyncService(FileSyncRepository fileSyncRepository, AuditLogRepository auditLogRepository) {
        this.fileSyncRepository = fileSyncRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public SyncReport synchronizeFolders(String sourceDirPath, String targetDirPath) throws IOException {
        Path sourcePath = Paths.get(sourceDirPath);
        Path targetPath = Paths.get(targetDirPath);

        if (!Files.exists(sourcePath)) {
            Files.createDirectories(sourcePath);
        }
        if (!Files.exists(targetPath)) {
            Files.createDirectories(targetPath);
        }

        SyncReport report = new SyncReport();
        report.setSourcePath(sourceDirPath);
        report.setTargetPath(targetDirPath);

        Map<String, String> sourceHashes = new HashMap<>();
        Map<String, String> targetHashes = new HashMap<>();

        // 1. Scan source directory files
        try (var stream = Files.walk(sourcePath)) {
            stream.filter(Files::isRegularFile).forEach(p -> {
                String rel = sourcePath.relativize(p).toString();
                try {
                    String hash = calculateSha256(p.toFile());
                    sourceHashes.put(rel, hash);
                } catch (Exception e) {
                    report.getErrors().add("Hash calculation failed for: " + rel);
                }
            });
        }

        // 2. Scan target directory files
        try (var stream = Files.walk(targetPath)) {
            stream.filter(Files::isRegularFile).forEach(p -> {
                String rel = targetPath.relativize(p).toString();
                try {
                    String hash = calculateSha256(p.toFile());
                    targetHashes.put(rel, hash);
                } catch (Exception e) {
                    report.getErrors().add("Hash calculation failed for target: " + rel);
                }
            });
        }

        // 3. Perform Delta Sync & Conflict Detection
        int copied = 0;
        int conflicts = 0;

        for (Map.Entry<String, String> entry : sourceHashes.entrySet()) {
            String relPath = entry.getKey();
            String srcHash = entry.getValue();

            Path srcFile = sourcePath.resolve(relPath);
            Path destFile = targetPath.resolve(relPath);

            if (!targetHashes.containsKey(relPath)) {
                // New file to sync
                Files.createDirectories(destFile.getParent());
                Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                copied++;
                report.getSyncedFiles().add(relPath);
            } else {
                String tgtHash = targetHashes.get(relPath);
                if (!srcHash.equals(tgtHash)) {
                    // Conflict or updated file -> Sync update
                    Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                    copied++;
                    conflicts++;
                    report.getSyncedFiles().add(relPath + " (Updated)");
                }
            }

            // Save or update file sync metadata in DB
            FileSync syncRecord = fileSyncRepository.findByPath(relPath).orElseGet(() -> new FileSync(relPath, srcHash, srcFile.toFile().length()));
            syncRecord.setHashSha256(srcHash);
            syncRecord.setSizeBytes(srcFile.toFile().length());
            syncRecord.setLastModified(LocalDateTime.now());
            fileSyncRepository.save(syncRecord);
        }

        report.setFilesCopied(copied);
        report.setConflictsDetected(conflicts);

        auditLogRepository.save(new AuditLog("system", "FOLDER_SYNC", "LOCAL_SYNC", "Synchronized " + copied + " files between '" + sourceDirPath + "' and '" + targetDirPath + "'"));

        return report;
    }

    public String calculateSha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public byte[] encryptData(byte[] input) throws Exception {
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public byte[] decryptData(byte[] input) throws Exception {
        SecretKeySpec key = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(input);
    }

    public byte[] compressData(byte[] input) throws IOException {
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(obj)) {
            gzip.write(input);
        }
        return obj.toByteArray();
    }

    public byte[] decompressData(byte[] input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(input))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzip.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
        return out.toByteArray();
    }

    public static class SyncReport {
        private String sourcePath;
        private String targetPath;
        private int filesCopied;
        private int conflictsDetected;
        private List<String> syncedFiles = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public String getSourcePath() { return sourcePath; }
        public void setSourcePath(String sourcePath) { this.sourcePath = sourcePath; }

        public String getTargetPath() { return targetPath; }
        public void setTargetPath(String targetPath) { this.targetPath = targetPath; }

        public int getFilesCopied() { return filesCopied; }
        public void setFilesCopied(int filesCopied) { this.filesCopied = filesCopied; }

        public int getConflictsDetected() { return conflictsDetected; }
        public void setConflictsDetected(int conflictsDetected) { this.conflictsDetected = conflictsDetected; }

        public List<String> getSyncedFiles() { return syncedFiles; }
        public void setSyncedFiles(List<String> syncedFiles) { this.syncedFiles = syncedFiles; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
