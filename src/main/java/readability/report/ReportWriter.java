package readability.report;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import readability.model.FileReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Simple JSON report writer for analysis output.
 *
 * Writes a list of FileReport objects to the specified file as JSON.
 */
public class ReportWriter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Writes reports to the given path (creates/overwrites file).
     *
     * @param reports list of FileReport
     * @param outputPath target file path
     * @throws IOException when write fails
     */
    public static void writeReports(List<FileReport> reports, Path outputPath) throws IOException {
        if (reports == null) throw new IllegalArgumentException("reports cannot be null");
        if (outputPath == null) throw new IllegalArgumentException("outputPath cannot be null");

        String json = GSON.toJson(reports);
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, json);
    }
}
