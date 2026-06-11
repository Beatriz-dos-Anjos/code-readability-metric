package readability.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import readability.model.FileReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Report writer for analysis output.
 * Produces a JSON file and a console summary.
 */
public class ReportWriter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Writes reports to the given path and prints a summary to the console.
     *
     * @param reports list of FileReport
     * @param outputPath target file path for JSON report
     * @throws IOException when write fails
     */
    public static void writeReports(List<FileReport> reports, Path outputPath) throws IOException {
        if (reports == null) throw new IllegalArgumentException("reports cannot be null");
        if (outputPath == null) throw new IllegalArgumentException("outputPath cannot be null");

        // Produto 1 - JSON completo
        String json = GSON.toJson(reports);
        if (outputPath.getParent() != null) {
            Files.createDirectories(outputPath.getParent());
        }
        Files.writeString(outputPath, json);

        // Produto 2 - Resumo no console
        printConsoleSummary(reports);
    }

    private static void printConsoleSummary(List<FileReport> reports) {
        List<FileReport> parseableReports = reports.stream()
                .filter(FileReport::isParseable)
                .toList();

        long unparseableCount = reports.size() - parseableReports.size();

        System.out.println("\n=== RESUMO DA ANÁLISE ===");
        
        // 10 piores arquivos
        System.out.println("\nOs 10 piores arquivos por score final:");
        System.out.printf("%-60s | %-10s%n", "Arquivo", "Score");
        System.out.println("-".repeat(73));
        
        parseableReports.stream()
                .sorted(Comparator.comparingDouble(FileReport::getFinalScore))
                .limit(10)
                .forEach(r -> System.out.printf("%-60s | %-10.2f%n", r.getFilePath(), r.getFinalScore()));

        // Score médio
        double averageScore = parseableReports.stream()
                .mapToDouble(FileReport::getFinalScore)
                .average()
                .orElse(0.0);
        
        System.out.printf("\nScore médio do repositório: %.2f%n", averageScore);

        // Contagem por faixa
        Map<String, Long> distribution = parseableReports.stream()
                .collect(Collectors.groupingBy(r -> {
                    double score = r.getFinalScore();
                    if (score >= 90) return "90-100";
                    if (score >= 70) return "70-89";
                    if (score >= 50) return "50-69";
                    return "Abaixo de 50";
                }, Collectors.counting()));

        System.out.println("\nDistribuição de scores:");
        System.out.printf("90-100:       %d%n", distribution.getOrDefault("90-100", 0L));
        System.out.printf("70-89:        %d%n", distribution.getOrDefault("70-89", 0L));
        System.out.printf("50-69:        %d%n", distribution.getOrDefault("50-69", 0L));
        System.out.printf("Abaixo de 50: %d%n", distribution.getOrDefault("Abaixo de 50", 0L));

        if (unparseableCount > 0) {
            System.out.printf("\nArquivos não processados (erro de parse): %d%n", unparseableCount);
        }
        System.out.println("=".repeat(25) + "\n");
    }
}
