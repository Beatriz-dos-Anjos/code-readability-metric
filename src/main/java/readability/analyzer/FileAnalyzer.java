package readability.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FileReport;
import readability.score.ScoreCalculator;
import readability.visitors.VisitorRegistry;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Orchestrates the analysis of a single Java source file.
 *
 * FileAnalyzer:
 * 1. Parses the file into an AST (Abstract Syntax Tree)
 * 2. Executes all registered Feature Visitors via VisitorRegistry
 * 3. Calculates feature and final scores
 * 4. Returns a complete FileReport
 *
 * Error handling:
 * - Parse errors are caught and reported with parseable=false
 *
 * Thread-safety: Stateless (can be called concurrently)
 *
 * @see readability.visitors.VisitorRegistry
 * @see readability.model.FileReport
 */
public class FileAnalyzer {

    /**
     * Analyzes a Java source file for all registered code readability metrics.
     *
     * @param path absolute path to a .java file
     * @return FileReport with metrics for all features, or empty report if parse failed
     * @throws IllegalArgumentException if path is null
     */
    public static FileReport analyze(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        try {
            CompilationUnit ast = StaticJavaParser.parse(path);

            // Execute all registered Feature Visitors
            var features = VisitorRegistry.analyzeFile(ast);

            // Create report with all feature results
            FileReport report = new FileReport(
                path.toString(),
                features,
                true
            );

            // Calculate feature scores and final score
            report = new ScoreCalculator().calculate(report);

            return report;

        } catch (Exception e) {
            System.err.printf(
                "[SKIP] %s - Parse failed: %s%n",
                path.getFileName(),
                e.getMessage()
            );

            return new FileReport(
                path.toString(),
                new ArrayList<>(),
                false
            );
        }
    }
}