package com.forgeflow.backend.controller;

import com.forgeflow.backend.dependency.DependencyAnalyzerService;
import com.forgeflow.backend.report.ReportExporterService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportExporterService reportExporterService;

    public ReportController(ReportExporterService reportExporterService) {
        this.reportExporterService = reportExporterService;
    }

    @PostMapping("/code-analysis")
    public ResponseEntity<String> exportCodeReport(@RequestBody DependencyAnalyzerService.AnalysisResult analysis) {
        String html = reportExporterService.generateStaticCodeReportHtml(analysis);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"forgeflow-code-analysis-report.html\"")
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @PostMapping("/workflow-execution")
    public ResponseEntity<String> exportWorkflowReport(@RequestBody WorkflowExecutionResult execution) {
        String html = reportExporterService.generateWorkflowAuditReportHtml(execution);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"forgeflow-workflow-execution-report.html\"")
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}
