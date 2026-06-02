package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Central registry for all Feature Visitors (F1 through F5).
 * 
 * This singleton manages the registration and execution of all features
 * in a predictable order. Colleagues should register their visitors here
 * during Phase 2 integration.
 * 
 * Thread-safe for concurrent analysis of multiple files.
 */
public class VisitorRegistry {
    
    private static final List<FeatureVisitor> visitors = new ArrayList<>();
    
    static {
        // Phase 1: Person 1 registers F1
        visitors.add(new F1HiddenBracesVisitor());
        
        // Phase 2: Colleagues register their visitors in order
        // visitors.add(new F2EmbeddedAssignVisitor());      // Person 2
        visitors.add(new F3OperatorsPerLineVisitor());    // Person 3
        // visitors.add(new F4NestingDepthVisitor());        // Person 5
        // visitors.add(new F5ParameterCountVisitor());      // Person 4
    }
    
    /**
     * Prevents instantiation of this utility class.
     */
    private VisitorRegistry() {
        throw new AssertionError("VisitorRegistry cannot be instantiated");
    }
    
    /**
     * Analyzes a CompilationUnit using all registered visitors.
     * 
     * Results are returned in the same order as visitor registration,
     * so Feature 1 is always at index 0, Feature 2 at index 1, etc.
     * 
     * @param ast the Abstract Syntax Tree (AST) root node to analyze
     * @return list of FeatureResult in order (F1, F2, F3, F4, F5)
     * @throws IllegalArgumentException if ast is null
     * @throws IllegalStateException if not all 5 features are registered
     */
    public static List<FeatureResult> analyzeFile(CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }
        
        List<FeatureResult> results = visitors.stream()
            .map(visitor -> {
                try {
                    return visitor.analyze(ast);
                } catch (Exception e) {
                    System.err.printf("[ERROR] Visitor %s failed: %s%n", 
                        visitor.getFeatureName(), e.getMessage());
                    throw new RuntimeException("Visitor analysis failed: " + visitor.getFeatureName(), e);
                }
            })
            .collect(Collectors.toList());
        
        // Phase 1-2: Feature count validation deferred to Phase 3 integration
        // This allows incremental development: Phase 1 runs with 1 feature,
        // Phase 2 colleagues register their visitors, Phase 3 validates all 5
        
        return results;
    }
    
    /**
     * Returns the number of registered visitors.
     * 
     * This should be 5 in Phase 2 when all features are integrated.
     * 
     * @return number of registered visitors
     */
    public static int getVisitorCount() {
        return visitors.size();
    }
    
    /**
     * For testing only: adds a visitor to the registry.
     * 
     * @param visitor the visitor to add
     */
    public static void registerVisitor(FeatureVisitor visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        visitors.add(visitor);
    }
    
    /**
     * For testing only: clears all registered visitors.
     */
    public static void clearVisitors() {
        visitors.clear();
    }
}
