package com.forgeflow.backend.controller;

import com.forgeflow.backend.ai.AiDiagnosticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiDiagnosticsController {

    private final AiDiagnosticsService aiDiagnosticsService;

    public AiDiagnosticsController(AiDiagnosticsService aiDiagnosticsService) {
        this.aiDiagnosticsService = aiDiagnosticsService;
    }

    @PostMapping("/diagnose")
    public ResponseEntity<Map<String, Object>> diagnoseError(@RequestBody Map<String, String> request) {
        String context = request.getOrDefault("context", "Execution Failure");
        String stackTrace = request.getOrDefault("stackTrace", "java.lang.NullPointerException: Object evaluated to null");
        Map<String, Object> analysis = aiDiagnosticsService.diagnoseError(context, stackTrace);
        return ResponseEntity.ok(analysis);
    }
}
