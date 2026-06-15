package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for F3OperatorsPerLineVisitor.
 * Validates that the visitor correctly detects lines with excessive operator
 * density
 * and calculates appropriate violation counts and readability scores.
 */
class F3Test {

    /**
     * Helper method: parses a Java file from the samples directory
     * and executes the F3OperatorsPerLineVisitor on its AST.
     */
    private FeatureResult runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        return new F3OperatorsPerLineVisitor().analyze(cu);
    }

    /**
     * Helper method: parses a Java source code string
     * and executes the F3OperatorsPerLineVisitor on its AST.
     */
    private FeatureResult runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        return new F3OperatorsPerLineVisitor().analyze(cu);
    }

    /**
     * ASSERTS: F3Good.java sample must have zero violations and a perfect score.
     * This baseline demonstrates code with acceptable operator density per line.
     */
    @Test
    void f3Good_shouldHaveZeroViolations() throws Exception {
        FeatureResult result = runOn("F3Good.java");

        assertEquals(0, result.getViolations(), "F3Good should have no operator-density violations");
        assertEquals(100.0, result.getScore(), 0.001, "F3Good score should be 100");
    }

    /**
     * ASSERTS: F3Bad.java sample must have at least one violation and a score below
     * 50.
     * This baseline demonstrates code with dense lines exceeding the operator
     * threshold.
     */
    @Test
    void f3Bad_shouldHaveViolationsAndLowScore() throws Exception {
        FeatureResult result = runOn("F3Bad.java");

        assertTrue(result.getViolations() > 0, "F3Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F3Bad score should be below 50");
    }

    /**
     * ASSERTS: Code with no operators must have zero violations and score 100.0,
     * since there are no dense lines to detect.
     */
    @Test
    void noOperators_shouldScoreHundred() {
        FeatureResult result = runOnSource("""
                class NoOps {
                    void sayHello() {
                        System.out.println("hello");
                    }
                }
                """);

        assertEquals(0, result.getViolations(), "No operators means no violations");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    /**
     * ASSERTS: A single line with 3 or more operators must count as exactly one
     * violation.
     */
    @Test
    void denseLineShouldCountAsOneViolation() {
        FeatureResult result = runOnSource("""
                class Dense {
                    int compute(int a, int b, int c, int d) {
                        return (a + b) * c - d / 2;
                    }
                }
                """);

        assertEquals(1, result.getViolations(),
                "A line with >= 3 operators counts as exactly 1 violation");
    }

    /**
     * ASSERTS: Lines with 2 or fewer operators must not violate and score 100.0.
     */
    @Test
    void twoOperatorsPerLine_shouldNotViolate() {
        FeatureResult result = runOnSource("""
                class Sparse {
                    int compute(int a, int b) {
                        int sum = a + b;
                        int diff = a - b;
                        return sum * diff;
                    }
                }
                """);

        assertEquals(0, result.getViolations(), "Lines with <= 2 operators should not violate");
        assertEquals(100.0, result.getScore(), 0.001);
    }
}