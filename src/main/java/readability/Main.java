package readability;

import readability.analyzer.FileAnalyzer;
import readability.model.FileReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main entry point for the code readability metric analyzer.
 * * Usage: java -jar legibilidade.jar <path>
 * <path> - a Java file or directory to analyze
 * * Flow:
 * 1. Discovers all .java files in the target path
 * 2. Analyzes each file with FileAnalyzer
 * 3. Prints summary to console
 * * @see readability.analyzer.FileAnalyzer
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar legibilidade.jar <path>");
            System.out.println("  <path> - a Java file or directory to scan");
            return;
        }

        Path targetPath = Paths.get(args[0]);
        List<FileReport> reports = new ArrayList<>();

        if (!Files.exists(targetPath)) {
            System.err.println("Path not found: " + targetPath);
            return;
        }

        try {
            List<Path> javaFiles;
            if (Files.isDirectory(targetPath)) {
                try (Stream<Path> walk = Files.walk(targetPath)) {
                    javaFiles = walk
                        .filter(p -> p.getFileName().toString().endsWith(".java"))
                        .collect(Collectors.toList());
                }
            } else {
                javaFiles = List.of(targetPath);
            }

            int total = javaFiles.size();
            System.out.printf("Found %d Java files to analyze%n%n", total);
            
            for (int i = 0; i < total; i++) {
                Path file = javaFiles.get(i);
                System.out.printf("[%d/%d] Analyzing: %s%n", (i + 1), total, file.getFileName());
                reports.add(FileAnalyzer.analyze(file));
            }

            // Print summary
            printSummary(reports);

        } catch (IOException e) {
            System.err.println("Error processing path: " + e.getMessage());
        }
    }
    
    /**
     * Prints a summary of analyzed files.
     */
    private static void printSummary(List<FileReport> reports) {
        System.out.println("\n=== Analysis Summary ===");
        
        List<FileReport> parseableReports = reports.stream()
            .filter(FileReport::isParseable)
            .collect(Collectors.toList());
        
        if (parseableReports.isEmpty()) {
            System.out.println("No parseable files found.");
            return;
        }
        
        // Exibe o Final Score e todos os scores individuais das features (F1, F2...)
        System.out.println("\nFile Scores:");
        parseableReports.forEach(r -> {
            System.out.printf("  %s%n", r.getFilePath());
            System.out.printf("    Final Score: %6.2f%%%n", r.getFinalScore());
            
            for (readability.model.FeatureResult feature : r.getFeatures()) {
                System.out.printf("    - F%d: %6.2f%%%n", feature.getFeatureId(), feature.getScore());
            }
            System.out.println();
        });
        
        // Calcula a média final de todos os arquivos processados
        double avgFinal = parseableReports.stream()
            .mapToDouble(FileReport::getFinalScore)
            .average()
            .orElse(0.0);
        
        System.out.printf("Average Final Score: %.2f%%%n", avgFinal);
    }
}