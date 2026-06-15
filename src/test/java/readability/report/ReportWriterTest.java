package readability.report;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import readability.model.FeatureResult;
import readability.model.FileReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportWriterTest {

    @TempDir
    Path tempDir;

    @Test
    void testWriteReports() throws IOException {
        Path outputPath = tempDir.resolve("report.json");
        List<FileReport> reports = new ArrayList<>();

        List<FeatureResult> features1 = List.of(
                new FeatureResult(0, 10, 100.0, 1),
                new FeatureResult(2, 10, 80.0, 3));
        FileReport report1 = new FileReport("File1.java", features1, true);
        report1.setFinalScore(90.0);
        reports.add(report1);

        List<FeatureResult> features2 = List.of(
                new FeatureResult(5, 10, 50.0, 1),
                new FeatureResult(8, 10, 20.0, 3));
        FileReport report2 = new FileReport("File2.java", features2, true);
        report2.setFinalScore(35.0);
        reports.add(report2);

        // Suppress console output during the test by capturing System.out.
        java.io.PrintStream originalOut = System.out;
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        try {
            ReportWriter.writeReports(reports, outputPath);
        } finally {
            // Restore the original System.out.
            System.setOut(originalOut);
        }

        assertTrue(Files.exists(outputPath));
        String content = Files.readString(outputPath);
        assertTrue(content.contains("File1.java"));
        assertTrue(content.contains("File2.java"));
        assertTrue(content.contains("90.0"));
        assertTrue(content.contains("35.0"));

        // Verify that the report was also generated correctly in the console.
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("=== RESUMO DA ANÁLISE ==="));
        assertTrue(consoleOutput.contains("File1.java"));
        assertTrue(consoleOutput.contains("File2.java"));
    }

    @Test
    void testWriteReportsNullArgs() {
        assertThrows(IllegalArgumentException.class, () -> ReportWriter.writeReports(null, Path.of("test.json")));
        assertThrows(IllegalArgumentException.class, () -> ReportWriter.writeReports(new ArrayList<>(), null));
    }
}
