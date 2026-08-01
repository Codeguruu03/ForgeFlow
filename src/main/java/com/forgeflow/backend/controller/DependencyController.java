package com.forgeflow.backend.controller;

import com.forgeflow.backend.dependency.DependencyAnalyzerService;
import com.forgeflow.backend.model.DependencyProject;
import com.forgeflow.backend.repository.DependencyProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dependency")
public class DependencyController {

    private final DependencyAnalyzerService analyzerService;
    private final DependencyProjectRepository projectRepository;

    public DependencyController(DependencyAnalyzerService analyzerService, DependencyProjectRepository projectRepository) {
        this.analyzerService = analyzerService;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<DependencyAnalyzerService.AnalysisResult> analyzeProject(@RequestBody Map<String, String> request) {
        String path = request.get("path");
        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        DependencyAnalyzerService.AnalysisResult result = analyzerService.analyzeProject(path);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<DependencyProject>> getAnalyzedProjects() {
        return ResponseEntity.ok(projectRepository.findAll());
    }
}
