package com.forgeflow.backend.dependency;

import com.forgeflow.backend.model.AuditLog;
import com.forgeflow.backend.model.DependencyProject;
import com.forgeflow.backend.repository.AuditLogRepository;
import com.forgeflow.backend.repository.DependencyProjectRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Service
public class DependencyAnalyzerService {

    private final JavaSourceParser javaSourceParser;
    private final DependencyProjectRepository projectRepository;
    private final AuditLogRepository auditLogRepository;

    public DependencyAnalyzerService(JavaSourceParser javaSourceParser,
                                     DependencyProjectRepository projectRepository,
                                     AuditLogRepository auditLogRepository) {
        this.javaSourceParser = javaSourceParser;
        this.projectRepository = projectRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public AnalysisResult analyzeProject(String projectDirectoryPath) {
        File dir = new File(projectDirectoryPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Invalid project directory path: " + projectDirectoryPath);
        }

        AnalysisResult result = new AnalysisResult();
        result.setProjectName(dir.getName());
        result.setRootPath(dir.getAbsolutePath());

        List<JavaSourceParser.ParsedJavaClass> parsedClasses = new ArrayList<>();
        Map<String, JavaSourceParser.ParsedJavaClass> fqcnMap = new HashMap<>();

        try (Stream<Path> stream = Files.walk(Paths.get(projectDirectoryPath))) {
            List<File> javaFiles = stream.filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .toList();

            result.setTotalFiles(javaFiles.size());

            int grandLoc = 0;
            int totalComplexity = 0;

            for (File file : javaFiles) {
                try {
                    JavaSourceParser.ParsedJavaClass parsed = javaSourceParser.parseFile(file);
                    parsedClasses.add(parsed);
                    fqcnMap.put(parsed.getFqcn(), parsed);
                    grandLoc += parsed.getLoc();
                    totalComplexity += parsed.getCyclomaticComplexity();
                } catch (Exception e) {
                    result.getErrors().add("Failed to parse: " + file.getName() + " - " + e.getMessage());
                }
            }

            result.setTotalLoc(grandLoc);
            result.setAvgCyclomaticComplexity(parsedClasses.isEmpty() ? 0 : (double) totalComplexity / parsedClasses.size());

            // Build Dependency Graph & Calculate Fan-In / Fan-Out
            Map<String, Set<String>> graph = new HashMap<>();
            Map<String, Integer> fanInMap = new HashMap<>();
            Map<String, Integer> fanOutMap = new HashMap<>();

            for (JavaSourceParser.ParsedJavaClass cls : parsedClasses) {
                graph.put(cls.getFqcn(), new HashSet<>());
                fanInMap.put(cls.getFqcn(), 0);
                fanOutMap.put(cls.getFqcn(), 0);
            }

            for (JavaSourceParser.ParsedJavaClass cls : parsedClasses) {
                int fanOut = 0;
                for (String imp : cls.getImports()) {
                    if (fqcnMap.containsKey(imp)) {
                        graph.get(cls.getFqcn()).add(imp);
                        fanOut++;
                        fanInMap.put(imp, fanInMap.get(imp) + 1);
                    }
                }
                fanOutMap.put(cls.getFqcn(), fanOut);
            }

            result.setDependenciesGraph(graph);

            // Detect Circular Dependencies
            List<List<String>> cycles = findCycles(graph);
            result.setCircularDependencies(cycles);

            // Detect Dead Code (Fan-In == 0, excluding App entry classes)
            List<String> deadCodeCandidates = new ArrayList<>();
            for (JavaSourceParser.ParsedJavaClass cls : parsedClasses) {
                if (fanInMap.get(cls.getFqcn()) == 0 && !cls.getClassName().contains("Application") && !cls.getClassName().contains("Main")) {
                    deadCodeCandidates.add(cls.getFqcn());
                }
            }
            result.setDeadCodeCandidates(deadCodeCandidates);

            // Save to DB
            DependencyProject project = new DependencyProject(result.getProjectName(), result.getRootPath(), "Maven/Gradle");
            project.setTotalFiles(result.getTotalFiles());
            project.setTotalLoc(result.getTotalLoc());
            project.setCyclomaticComplexity(result.getAvgCyclomaticComplexity());
            projectRepository.save(project);

            auditLogRepository.save(new AuditLog("system", "ANALYZE_PROJECT", "CODE_EXPLORER", "Analyzed Java project: " + result.getProjectName() + " (" + result.getTotalFiles() + " files)"));

        } catch (Exception e) {
            result.getErrors().add("Analysis exception: " + e.getMessage());
        }

        return result;
    }

    private List<List<String>> findCycles(Map<String, Set<String>> graph) {
        List<List<String>> cycles = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> path = new ArrayList<>();

        for (String node : graph.keySet()) {
            if (!visited.contains(node)) {
                dfsCycle(node, graph, visited, recStack, path, cycles);
            }
        }
        return cycles;
    }

    private void dfsCycle(String node, Map<String, Set<String>> graph, Set<String> visited, Set<String> recStack, List<String> path, List<List<String>> cycles) {
        visited.add(node);
        recStack.add(node);
        path.add(node);

        for (String neighbor : graph.getOrDefault(node, Collections.emptySet())) {
            if (!visited.contains(neighbor)) {
                dfsCycle(neighbor, graph, visited, recStack, path, cycles);
            } else if (recStack.contains(neighbor)) {
                int cycleStart = path.indexOf(neighbor);
                if (cycleStart != -1) {
                    cycles.add(new ArrayList<>(path.subList(cycleStart, path.size())));
                }
            }
        }

        recStack.remove(node);
        path.remove(path.size() - 1);
    }

    public static class AnalysisResult {
        private String projectName;
        private String rootPath;
        private int totalFiles;
        private int totalLoc;
        private double avgCyclomaticComplexity;
        private Map<String, Set<String>> dependenciesGraph = new HashMap<>();
        private List<List<String>> circularDependencies = new ArrayList<>();
        private List<String> deadCodeCandidates = new ArrayList<>();
        private List<String> errors = new ArrayList<>();

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getRootPath() { return rootPath; }
        public void setRootPath(String rootPath) { this.rootPath = rootPath; }

        public int getTotalFiles() { return totalFiles; }
        public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }

        public int getTotalLoc() { return totalLoc; }
        public void setTotalLoc(int totalLoc) { this.totalLoc = totalLoc; }

        public double getAvgCyclomaticComplexity() { return avgCyclomaticComplexity; }
        public void setAvgCyclomaticComplexity(double avgCyclomaticComplexity) { this.avgCyclomaticComplexity = avgCyclomaticComplexity; }

        public Map<String, Set<String>> getDependenciesGraph() { return dependenciesGraph; }
        public void setDependenciesGraph(Map<String, Set<String>> dependenciesGraph) { this.dependenciesGraph = dependenciesGraph; }

        public List<List<String>> getCircularDependencies() { return circularDependencies; }
        public void setCircularDependencies(List<List<String>> circularDependencies) { this.circularDependencies = circularDependencies; }

        public List<String> getDeadCodeCandidates() { return deadCodeCandidates; }
        public void setDeadCodeCandidates(List<String> deadCodeCandidates) { this.deadCodeCandidates = deadCodeCandidates; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }
}
