package com.forgeflow.backend;

import com.forgeflow.backend.dependency.DependencyAnalyzerService;
import com.forgeflow.backend.security.JwtTokenProvider;
import com.forgeflow.backend.sync.LocalSyncService;
import com.forgeflow.backend.workflow.model.EdgeDefinition;
import com.forgeflow.backend.workflow.model.NodeDefinition;
import com.forgeflow.backend.workflow.model.WorkflowValidationResult;
import com.forgeflow.backend.workflow.validator.WorkflowDagValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EdgeCasesTest {

    @Autowired
    private WorkflowDagValidator dagValidator;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private LocalSyncService localSyncService;

    @Autowired
    private DependencyAnalyzerService dependencyAnalyzerService;

    @Test
    @DisplayName("Edge Case 1: Detect Cycle in Workflow DAG (NodeA -> NodeB -> NodeC -> NodeA)")
    void testCyclicDagDetectionEdgeCase() {
        System.out.println("--------------------------------------------------");
        System.out.println("[TEST CONSOLE] Running Edge Case 1: Cyclic DAG Detection");
        List<NodeDefinition> nodes = List.of(
                new NodeDefinition("NodeA", "RECEIVE_FILE", "Receive Node"),
                new NodeDefinition("NodeB", "VALIDATE", "Validate Node"),
                new NodeDefinition("NodeC", "DEPLOY", "Deploy Node")
        );
        List<EdgeDefinition> edges = List.of(
                new EdgeDefinition("NodeA", "NodeB"),
                new EdgeDefinition("NodeB", "NodeC"),
                new EdgeDefinition("NodeC", "NodeA") // Cycle!
        );

        WorkflowValidationResult result = dagValidator.validate(nodes, edges);
        System.out.println("[TEST CONSOLE] Cyclic Validation Output: IsValid=" + result.isValid() + " | Errors=" + result.getErrors());

        assertFalse(result.isValid(), "DAG with circular reference must fail validation");
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("cycle")), "Error message must report cycle detection");
        System.out.println("[TEST CONSOLE] ✔ Edge Case 1 Passed Successfully!");
        System.out.println("--------------------------------------------------");
    }

    @Test
    @DisplayName("Edge Case 2: Reject Invalid/Non-Existent Path in Code Explorer")
    void testNonExistentPathInCodeExplorer() {
        System.out.println("--------------------------------------------------");
        System.out.println("[TEST CONSOLE] Running Edge Case 2: Non-Existent Directory Path Validation");
        String badPath = "./non_existent_directory_for_testing_123";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dependencyAnalyzerService.analyzeProject(badPath);
        });

        System.out.println("[TEST CONSOLE] Code Explorer Exception Intercepted: " + exception.getMessage());
        assertTrue(exception.getMessage().contains("Invalid project directory path"));
        System.out.println("[TEST CONSOLE] ✔ Edge Case 2 Passed Successfully!");
        System.out.println("--------------------------------------------------");
    }

    @Test
    @DisplayName("Edge Case 3: Invalid JWT Token Rejection")
    void testInvalidJwtTokenRejection() {
        System.out.println("--------------------------------------------------");
        System.out.println("[TEST CONSOLE] Running Edge Case 3: Invalid JWT Authentication");
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.invalidPayloadSignature";

        boolean isValid = jwtTokenProvider.validateToken(malformedToken);
        System.out.println("[TEST CONSOLE] JWT Token Validation Result: IsValid=" + isValid);

        assertFalse(isValid, "Malformed or tampered JWT token must be rejected");
        System.out.println("[TEST CONSOLE] ✔ Edge Case 3 Passed Successfully!");
        System.out.println("--------------------------------------------------");
    }

    @Test
    @DisplayName("Edge Case 4: AES-256 Encryption & Decryption Roundtrip Integrity")
    void testAesEncryptionRoundtrip() throws Exception {
        System.out.println("--------------------------------------------------");
        System.out.println("[TEST CONSOLE] Running Edge Case 4: Cryptographic Payload Encryption");
        byte[] originalSecret = "TopSecretPayloadData_12345".getBytes();

        byte[] encrypted = localSyncService.encryptData(originalSecret);
        byte[] decrypted = localSyncService.decryptData(encrypted);

        System.out.println("[TEST CONSOLE] Crypto Bytes: Original=" + originalSecret.length + " | Encrypted=" + encrypted.length + " | Decrypted=" + decrypted.length);

        assertArrayEquals(originalSecret, decrypted, "Decrypted data must match original payload exactly");
        System.out.println("[TEST CONSOLE] ✔ Edge Case 4 Passed Successfully!");
        System.out.println("--------------------------------------------------");
    }
}
