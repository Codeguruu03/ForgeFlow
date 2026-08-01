package com.forgeflow.backend;

import com.forgeflow.backend.ai.AiDiagnosticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AiDiagnosticsTest {

    @Autowired
    private AiDiagnosticsService aiDiagnosticsService;

    @Test
    void testConnectionRefusedDiagnosis() {
        String context = "FTP Backup Job Failure";
        String stackTrace = "java.net.ConnectException: Connection refused: connect";

        Map<String, Object> result = aiDiagnosticsService.diagnoseError(context, stackTrace);
        assertNotNull(result);
        assertEquals("HIGH", result.get("severity"));
        assertEquals("NETWORK_INFRASTRUCTURE", result.get("rootCauseCategory"));
        assertTrue(result.get("recommendedFix").toString().contains("target service health"));
    }

    @Test
    void testNullPointerDiagnosis() {
        String context = "DAG Node #3 Execution";
        String stackTrace = "java.lang.NullPointerException: Cannot read field 'id' because object is null";

        Map<String, Object> result = aiDiagnosticsService.diagnoseError(context, stackTrace);
        assertNotNull(result);
        assertEquals("NULL_POINTER_EXCEPTION", result.get("rootCauseCategory"));
        assertTrue(result.get("recommendedFix").toString().contains("null-check guards"));
    }
}
