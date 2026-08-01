package com.forgeflow.backend;

import com.forgeflow.backend.dependency.DependencyAnalyzerService;
import com.forgeflow.backend.report.ReportExporterService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReportExporterTest {

    @Autowired
    private ReportExporterService reportExporterService;

    @Test
    void testCodeReportHtmlGeneration() {
        DependencyAnalyzerService.AnalysisResult result = new DependencyAnalyzerService.AnalysisResult();
        result.setProjectName("ForgeFlow");
        result.setRootPath("/test/path");
        result.setTotalFiles(10);
        result.setTotalLoc(1000);
        result.setAvgCyclomaticComplexity(2.5);

        String html = reportExporterService.generateStaticCodeReportHtml(result);
        assertNotNull(html);
        assertTrue(html.contains("ForgeFlow Static Code Dependency Audit Report"));
        assertTrue(html.contains("ForgeFlow"));
    }

    @Test
    void testWorkflowReportHtmlGeneration() {
        WorkflowExecutionResult result = new WorkflowExecutionResult(1L, "CI/CD Pipeline");
        result.setSuccess(true);
        result.setStatusMessage("Execution succeeded");
        result.addLog("[ENGINE] Step 1 complete");

        String html = reportExporterService.generateWorkflowAuditReportHtml(result);
        assertNotNull(html);
        assertTrue(html.contains("ForgeFlow Workflow Execution Audit Log"));
        assertTrue(html.contains("CI/CD Pipeline"));
    }
}
