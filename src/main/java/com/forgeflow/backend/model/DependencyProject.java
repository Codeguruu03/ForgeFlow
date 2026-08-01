package com.forgeflow.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dependency_projects")
public class DependencyProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    private String rootPath;

    private String buildTool;

    private Integer totalFiles = 0;

    private Integer totalLoc = 0;

    private Double cyclomaticComplexity = 0.0;

    private String status = "ANALYZED";

    private LocalDateTime analyzedAt;

    public DependencyProject() {}

    public DependencyProject(String projectName, String rootPath, String buildTool) {
        this.projectName = projectName;
        this.rootPath = rootPath;
        this.buildTool = buildTool;
        this.status = "ANALYZED";
        this.analyzedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getRootPath() { return rootPath; }
    public void setRootPath(String rootPath) { this.rootPath = rootPath; }

    public String getBuildTool() { return buildTool; }
    public void setBuildTool(String buildTool) { this.buildTool = buildTool; }

    public Integer getTotalFiles() { return totalFiles; }
    public void setTotalFiles(Integer totalFiles) { this.totalFiles = totalFiles; }

    public Integer getTotalLoc() { return totalLoc; }
    public void setTotalLoc(Integer totalLoc) { this.totalLoc = totalLoc; }

    public Double getCyclomaticComplexity() { return cyclomaticComplexity; }
    public void setCyclomaticComplexity(Double cyclomaticComplexity) { this.cyclomaticComplexity = cyclomaticComplexity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
