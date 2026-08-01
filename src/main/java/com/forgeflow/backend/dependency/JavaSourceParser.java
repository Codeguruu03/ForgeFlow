package com.forgeflow.backend.dependency;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JavaSourceParser {

    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;");
    private static final Pattern IMPORT_PATTERN = Pattern.compile("^\\s*import\\s+([a-zA-Z0-9_.]+)\\s*;");
    private static final Pattern CLASS_PATTERN = Pattern.compile("\\b(class|interface|enum|record)\\s+([a-zA-Z0-9_]+)");

    public ParsedJavaClass parseFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());

        String packageName = "default";
        String className = file.getName().replace(".java", "");
        Set<String> imports = new HashSet<>();
        int loc = 0;
        int cyclomaticComplexity = 1; // Base complexity

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("//") || trimmed.startsWith("/*") || trimmed.startsWith("*")) {
                continue;
            }
            loc++;

            Matcher pkgMatcher = PACKAGE_PATTERN.matcher(trimmed);
            if (pkgMatcher.find()) {
                packageName = pkgMatcher.group(1);
            }

            Matcher impMatcher = IMPORT_PATTERN.matcher(trimmed);
            if (impMatcher.find()) {
                imports.add(impMatcher.group(1));
            }

            Matcher clsMatcher = CLASS_PATTERN.matcher(trimmed);
            if (clsMatcher.find()) {
                className = clsMatcher.group(2);
            }

            // Cyclomatic complexity heuristics (if, else, for, while, case, &&, ||, ?)
            if (trimmed.contains("if ") || trimmed.contains("if(") ||
                trimmed.contains("else if") || trimmed.contains("for ") ||
                trimmed.contains("while ") || trimmed.contains("case ") ||
                trimmed.contains("&&") || trimmed.contains("||") || trimmed.contains(" ? ")) {
                cyclomaticComplexity++;
            }
        }

        String fqcn = packageName.equals("default") ? className : packageName + "." + className;
        return new ParsedJavaClass(fqcn, className, packageName, file.getAbsolutePath(), loc, cyclomaticComplexity, imports);
    }

    public static class ParsedJavaClass {
        private final String fqcn;
        private final String className;
        private final String packageName;
        private final String filePath;
        private final int loc;
        private final int cyclomaticComplexity;
        private final Set<String> imports;

        public ParsedJavaClass(String fqcn, String className, String packageName, String filePath, int loc, int cyclomaticComplexity, Set<String> imports) {
            this.fqcn = fqcn;
            this.className = className;
            this.packageName = packageName;
            this.filePath = filePath;
            this.loc = loc;
            this.cyclomaticComplexity = cyclomaticComplexity;
            this.imports = imports;
        }

        public String getFqcn() { return fqcn; }
        public String getClassName() { return className; }
        public String getPackageName() { return packageName; }
        public String getFilePath() { return filePath; }
        public int getLoc() { return loc; }
        public int getCyclomaticComplexity() { return cyclomaticComplexity; }
        public Set<String> getImports() { return imports; }
    }
}
