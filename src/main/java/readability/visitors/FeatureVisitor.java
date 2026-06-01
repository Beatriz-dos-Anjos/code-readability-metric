package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;

/**
 * Common interface for all Feature Visitors (F1 through F5).
 * 
 * All visitors must implement this to ensure consistent integration 
 * with the FileAnalyzer and to enable dynamic visitor registration.
 * 
 * Each visitor analyzes one specific code readability metric and returns
 * a FeatureResult with violations, opportunities, and a calculated score.
 * 
 * @see VisitorRegistry
 * @see readability.analyzer.FileAnalyzer
 */
public interface FeatureVisitor {
    
    /**
     * Analyzes the given CompilationUnit for this feature's metric.
     * 
     * @param ast the Abstract Syntax Tree (AST) root node to analyze
     * @return a FeatureResult containing violations, opportunities, and score
     * @throws IllegalArgumentException if ast is null
     */
    FeatureResult analyze(CompilationUnit ast);
    
    /**
     * Returns the feature index (1-5) this visitor implements.
     * 
     * This is used to maintain consistent ordering across all features
     * and to provide meaningful error messages.
     * 
     * @return feature index (1 for F1, 2 for F2, ..., 5 for F5)
     */
    int getFeatureId();
    
    /**
     * Returns a human-readable feature name for logging and reporting.
     * 
     * Examples: "Hidden Braces", "Embedded Assignment", etc.
     * 
     * @return feature name
     */
    String getFeatureName();
}
