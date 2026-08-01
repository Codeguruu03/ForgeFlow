package com.forgeflow.backend.report;

import com.forgeflow.backend.dependency.DependencyAnalyzerService;
import com.forgeflow.backend.workflow.model.WorkflowExecutionResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ReportExporterService {

    public String generateStaticCodeReportHtml(DependencyAnalyzerService.AnalysisResult analysis) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>ForgeFlow Static Code Dependency Report</title>")
            .append("<style>")
            .append("body { font-family: 'Segoe UI', Arial, sans-serif; background: #0f141c; color: #e2e8f0; padding: 30px; }")
            .append(".card { background: #1e293b; border-radius: 8px; padding: 20px; margin-bottom: 20px; border: 1px solid #334155; }")
            .append("h1 { color: #38bdf8; } h2 { color: #818cf8; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 10px; }")
            .append("th, td { border: 1px solid #334155; padding: 10px; text-align: left; }")
            .append("th { background: #171e2e; color: #94a3b8; }")
            .append(".badge-success { background: #064e3b; color: #34d399; padding: 4px 8px; border-radius: 4px; }")
            .append(".badge-warning { background: #78350f; color: #fbbf24; padding: 4px 8px; border-radius: 4px; }")
            .append("</style></head><body>");

        html.append("<h1>⚡ ForgeFlow Static Code Dependency Audit Report</h1>");
        html.append("<p>Generated at: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("</p>");

        html.append("<div class='card'><h2>Project Summary</h2>")
            .append("<p><strong>Project Name:</strong> ").append(analysis.getProjectName()).append("</p>")
            .append("<p><strong>Root Path:</strong> ").append(analysis.getRootPath()).append("</p>")
            .append("<p><strong>Total Files:</strong> ").append(analysis.getTotalFiles()).append("</p>")
            .append("<p><strong>Total LOC:</strong> ").append(analysis.getTotalLoc()).append("</p>")
            .append("<p><strong>Avg Cyclomatic Complexity:</strong> ").append(String.format("%.2f", analysis.getAvgCyclomaticComplexity())).append("</p>")
            .append("</div>");

        html.append("<div class='card'><h2>Circular Dependencies</h2>");
        if (analysis.getCircularDependencies().isEmpty()) {
            html.append("<p class='badge-success'>✔ No circular dependencies detected in class graph.</p>");
        } else {
            html.append("<p class='badge-warning'>⚠️ ").append(analysis.getCircularDependencies().size()).append(" cycles detected!</p>");
            html.append("<ul>");
            for (var cycle : analysis.getCircularDependencies()) {
                html.append("<li>").append(String.join(" ➔ ", cycle)).append("</li>");
            }
            html.append("</ul>");
        }
        html.append("</div>");

        html.append("<div class='card'><h2>Dead Code Candidates (Unreferenced)</h2>");
        if (analysis.getDeadCodeCandidates().isEmpty()) {
            html.append("<p class='badge-success'>✔ No unreferenced classes detected.</p>");
        } else {
            html.append("<ul>");
            for (String dead : analysis.getDeadCodeCandidates()) {
                html.append("<li>").append(dead).append("</li>");
            }
            html.append("</ul>");
        }
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }

    public String generateWorkflowAuditReportHtml(WorkflowExecutionResult execution) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><title>ForgeFlow Workflow Audit Report</title>")
            .append("<style>")
            .append("body { font-family: 'Consolas', 'Segoe UI', sans-serif; background: #030712; color: #4ade80; padding: 25px; }")
            .append(".header { border-bottom: 2px solid #1e293b; padding-bottom: 15px; margin-bottom: 20px; }")
            .append(".log-entry { margin: 6px 0; }")
            .append(".success { color: #34d399; font-weight: bold; }")
            .append(".fail { color: #f87171; font-weight: bold; }")
            .append("</style></head><body>");

        html.append("<div class='header'>")
            .append("<h2>⚡ ForgeFlow Workflow Execution Audit Log</h2>")
            .append("<p>Workflow ID: ").append(execution.getWorkflowId()).append(" | Name: ").append(execution.getWorkflowName()).append("</p>")
            .append("<p>Status: <span class='").append(execution.isSuccess() ? "success" : "fail").append("'>")
            .append(execution.getStatusMessage()).append("</span> | Execution Time: ").append(execution.getExecutionTimeMs()).append(" ms</p>")
            .append("</div>");

        html.append("<h3>Execution Steps Log Stream</h3><div>");
        for (String log : execution.getLogs()) {
            html.append("<div class='log-entry'>").append(log).append("</div>");
        }
        html.append("</div></body></html>");
        return html.toString();
    }
}
