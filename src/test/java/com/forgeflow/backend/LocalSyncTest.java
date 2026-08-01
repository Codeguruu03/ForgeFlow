package com.forgeflow.backend;

import com.forgeflow.backend.sync.LocalSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LocalSyncTest {

    @Autowired
    private LocalSyncService localSyncService;

    @Test
    void testFileSyncAndEncryption() throws Exception {
        File sourceDir = new File("target/sync_test_source");
        File targetDir = new File("target/sync_test_target");
        sourceDir.mkdirs();
        targetDir.mkdirs();

        File sampleFile = new File(sourceDir, "test_doc.txt");
        Files.writeString(sampleFile.toPath(), "ForgeFlow LocalSync Engine Verification Content");

        LocalSyncService.SyncReport report = localSyncService.synchronizeFolders(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());
        assertNotNull(report);
        assertTrue(report.getFilesCopied() > 0);

        File syncedFile = new File(targetDir, "test_doc.txt");
        assertTrue(syncedFile.exists());
        assertEquals("ForgeFlow LocalSync Engine Verification Content", Files.readString(syncedFile.toPath()));

        // Encryption test
        byte[] original = "Confidential Payload".getBytes();
        byte[] encrypted = localSyncService.encryptData(original);
        byte[] decrypted = localSyncService.decryptData(encrypted);
        assertArrayEquals(original, decrypted);
    }
}
