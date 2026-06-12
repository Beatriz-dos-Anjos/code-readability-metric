package readability.visitors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import readability.analyzer.FileAnalyzer;
import readability.model.FeatureResult;
import readability.model.FileReport;
import readability.report.ReportWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class F3IntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testF3WorkflowWithSamples() throws IOException {
        List<FileReport> reports = new ArrayList<>();

        // Process F3Good.java using FileAnalyzer
        Path goodPath = Paths.get("samples", "F3Good.java");
        FileReport goodReport = FileAnalyzer.analyze(goodPath);
        // Note: Final score calculation might need to be done if not set by analyzer
        // In current implementation, we might need to sum up or average features
        double goodF3Score = goodReport.getFeatures().stream()
                .filter(f -> f.getFeatureId() == 3)
                .findFirst()
                .map(FeatureResult::getScore)
                .orElse(0.0);
        goodReport.setFinalScore(goodF3Score);
        reports.add(goodReport);

        // Process F3Bad.java using FileAnalyzer
        Path badPath = Paths.get("samples", "F3Bad.java");
        FileReport badReport = FileAnalyzer.analyze(badPath);
        double badF3Score = badReport.getFeatures().stream()
                .filter(f -> f.getFeatureId() == 3)
                .findFirst()
                .map(FeatureResult::getScore)
                .orElse(0.0);
        badReport.setFinalScore(badF3Score);
        reports.add(badReport);

        // Write report
        Path outputPath = tempDir.resolve("f3_integration_report.json");
        ReportWriter.writeReports(reports, outputPath);

        // Validations
        assertTrue(Files.exists(outputPath), "Report file should be created");
        
        String jsonContent = Files.readString(outputPath);
        
        // Check F3Good in JSON
        assertTrue(jsonContent.contains("F3Good.java"));
        assertTrue(jsonContent.contains("100.0")); // Expected score for F3Good
        
        // Check F3Bad in JSON
        assertTrue(jsonContent.contains("F3Bad.java"));
        // F3Bad score is calculated as 100 * (1 - 5/16) = 68.75
        assertTrue(jsonContent.contains("68.75")); 

        System.out.println("Integration test finished successfully. Report generated at: " + outputPath);
    }
}
