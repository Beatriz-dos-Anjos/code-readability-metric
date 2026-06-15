package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for F4NestingDepthVisitor.
 * Validates that the visitor correctly detects excessive nesting depth in
 * methods
 * and calculates appropriate violation counts and readability scores.
 */
class F4Test {

    /**
     * Helper method: parses a Java file from the samples directory
     * and executes the F4NestingDepthVisitor on its AST.
     */
    private FeatureResult runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        return new F4NestingDepthVisitor().analyze(cu);
    }

    /**
     * Helper method: parses a Java source code string
     * and executes the F4NestingDepthVisitor on its AST.
     */
    private FeatureResult runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        return new F4NestingDepthVisitor().analyze(cu);
    }

    /**
     * ASSERTS: F4Good.java sample must have zero violations and a perfect score.
     * This baseline demonstrates code with acceptable nesting depth.
     */
    @Test
    void f4Good_shouldHaveZeroViolations() throws Exception {
        FeatureResult result = runOn("F4Good.java");

        assertEquals(0, result.getViolations(), "F4Good should have no nesting violations");
        assertEquals(100.0, result.getScore(), 0.001, "F4Good score should be 100");
    }

    /**
     * ASSERTS: F4Bad.java sample must have at least one violation and a score below
     * 50.
     * This baseline demonstrates code with excessive nesting depth.
     */
    @Test
    void f4Bad_shouldHaveViolationsAndLowScore() throws Exception {
        FeatureResult result = runOn("F4Bad.java");

        assertTrue(result.getViolations() > 0, "F4Bad should have nesting violations");
        assertTrue(result.getScore() < 50.0, "F4Bad score should be below 50");
    }

    /**
     * ASSERTS: A class with no methods must have zero opportunities and score
     * 100.0,
     * since there are no methods to validate nesting in.
     */
    @Test
    void noMethods_shouldScoreHundred() {
        FeatureResult result = runOnSource("""
                class Empty {
                }
                """);

        assertEquals(0, result.getOpportunities(), "No methods means 0 opportunities");
        assertEquals(100.0, result.getScore(), 0.001, "Score should be 100 when opportunities == 0");
    }

    /**
     * ASSERTS: A method with nesting depth of exactly 2 must not violate and score
     * 100.0.
     */
    @Test
    void depthExactlyTwo_shouldNotViolate() {
        FeatureResult result = runOnSource("""
                class ShallowNesting {
                    void process(int x) {
                        if (x > 0) {
                            System.out.println("positive");
                        }
                    }
                }
                """);

        assertEquals(0, result.getViolations(), "Depth of 2 should not be a violation");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    /**
     * ASSERTS: A method with nesting depth of 3 must count as one violation.
     */
    @Test
    void depthThree_shouldViolate() {
        FeatureResult result = runOnSource("""
                class DeepNesting {
                    void process(int[] data) {
                        if (data != null) {
                            for (int item : data) {
                                System.out.println(item);
                            }
                        }
                    }
                }
                """);

        assertEquals(1, result.getViolations(), "Depth of 3 should count as one violation");
    }

    /**
     * ASSERTS: Among two methods, only the one with excessive nesting depth
     * should count as a violation.
     */
    @Test
    void twoMethods_onlyOneDeep_oneViolation() {
        FeatureResult result = runOnSource("""
                class Mixed {
                    void shallow(int x) {
                        if (x > 0) {
                            System.out.println(x);
                        }
                    }
                    void deep(int[] data) {
                        if (data != null) {
                            for (int item : data) {
                                System.out.println(item);
                            }
                        }
                    }
                }
                """);

        assertEquals(2, result.getOpportunities(), "Two methods = two opportunities");
        assertEquals(1, result.getViolations(), "Only the deep method should be a violation");
    }
}