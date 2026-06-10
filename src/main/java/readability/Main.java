package readability;

import readability.analyzer.FileAnalyzer;
import readability.model.FileReport;
import readability.report.ReportWriter;

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
 * 
 * Usage: java -jar legibilidade.jar <path>
 *   <path> - a Java file or directory to analyze
 * 
 * Flow:
 * 1. Discovers all .java files in the target path
 * 2. Analyzes each file with FileAnalyzer
 * 3. Calculates final scores
 * 4. Generates JSON report and prints summary via ReportWriter
 * 
 * @see readability.analyzer.FileAnalyzer
 * @see readability.report.ReportWriter
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

            // Calculate final scores (placeholder until ScoreCalculator is ready)
            reports.forEach(report -> {
                if (report.isParseable()) {
                    double avg = report.getFeatures().stream()
                        .mapToDouble(f -> f.getScore())
                        .average()
                        .orElse(0.0);
                    report.setFinalScore(avg);
                }
            });

            // Write report and print summary
            ReportWriter.writeReports(reports, Paths.get("report.json"));

            // Phase 2 (Person 3): Write JSON report
            try {
                Path reportPath = Paths.get("readability-report.json");
                readability.report.ReportWriter.writeReports(reports, reportPath);
                System.out.printf("%nJSON report saved to: %s%n", reportPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("Failed to write JSON report: " + e.getMessage());
            }

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
            
        List<FileReport> unparseableReports = reports.stream()
            .filter(r -> !r.isParseable())
            .collect(Collectors.toList());
        
        System.out.printf("Total files found:      %d%n", reports.size());
        System.out.printf("Parseable files:        %d%n", parseableReports.size());
        System.out.printf("Unparseable (skipped):  %d%n", unparseableReports.size());
        
        if (!unparseableReports.isEmpty()) {
            System.out.println("\nUnparseable Files:");
            unparseableReports.forEach(r -> System.out.printf("  [SKIP] %s%n", r.getFilePath()));
        }

        if (parseableReports.isEmpty()) {
            System.out.println("\nNo parseable files found to calculate scores.");
            return;
        }
        
        // Print all files with F1 score
        System.out.println("\nFile Scores:");
        parseableReports.forEach(r -> {
            if (!r.getFeatures().isEmpty()) {
                double f1Score = r.getFeatures().get(0).getScore();
                System.out.printf("  %-50s F1: %6.2f%%%n", 
                    r.getFilePath(), f1Score);
            }
        });
        
        // Print average F1 score
        double avgF1 = parseableReports.stream()
            .filter(r -> !r.getFeatures().isEmpty())
            .mapToDouble(r -> r.getFeatures().get(0).getScore())
            .average()
            .orElse(0.0);
        
        System.out.printf("\nAverage F1 Score: %.2f%%%n", avgF1);
    }
}