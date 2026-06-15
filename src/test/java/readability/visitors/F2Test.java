package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for F2EmbeddedAssignVisitor.
 * Validates that the visitor correctly detects assignment expressions embedded
 * in conditional statements and calculates appropriate violation counts and
 * scores.
 */
class F2Test {

    /**
     * Helper method: parses a Java file from the samples directory
     * and executes the F2EmbeddedAssignVisitor on its AST.
     */
    private F2EmbeddedAssignVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F2EmbeddedAssignVisitor visitor = new F2EmbeddedAssignVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    /**
     * Helper method: parses a Java source code string
     * and executes the F2EmbeddedAssignVisitor on its AST.
     */
    private F2EmbeddedAssignVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F2EmbeddedAssignVisitor visitor = new F2EmbeddedAssignVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    /**
     * ASSERTS: F2Good.java sample must have zero violations and a perfect score.
     * This baseline demonstrates code with no embedded assignments in conditionals.
     */
    @Test
    void f2Good_shouldHaveZeroViolations() throws Exception {
        F2EmbeddedAssignVisitor v = runOn("F2Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F2Good should have no embedded-assign violations");
        assertEquals(100.0, result.getScore(), 0.001, "F2Good score should be 100");
    }

    /**
     * ASSERTS: F2Bad.java sample must have at least one violation and a score below
     * 50.
     * This baseline demonstrates code with embedded assignments in conditional
     * statements.
     */
    @Test
    void f2Bad_shouldHaveViolationsAndLowScore() throws Exception {
        F2EmbeddedAssignVisitor v = runOn("F2Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F2Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F2Bad score should be below 50");
    }

    /**
     * ASSERTS: Code with no conditional statements must have zero opportunities
     * and score 100.0, since there are no conditionals to validate.
     */
    @Test
    void noConditionals_shouldScoreHundred() {
        String source = """
                class NoConditions {
                    void run() {
                        int x = 5;
                        x = x + 1;
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getOpportunities(), "No conditionals means 0 opportunities");
        assertEquals(100.0, result.getScore(), 0.001, "Score should be 100 when opportunities == 0");
    }

    /**
     * ASSERTS: Code with clean conditionals (no embedded assignments) must have
     * zero violations and score 100.0, regardless of conditional complexity.
     */
    @Test
    void cleanConditionals_shouldScoreHundred() {
        String source = """
                class Clean {
                    void process(int x) {
                        if (x > 0) {
                            System.out.println("pos");
                        }
                        while (x > 0) {
                            x--;
                        }
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations(), "No embedded assignments expected");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    /**
     * ASSERTS: Code with an assignment expression embedded in a while condition
     * must count as at least one violation.
     */
    @Test
    void embeddedAssignInWhile_shouldCountAsViolation() {
        String source = """
                import java.io.*;
                class Reader {
                    void read(BufferedReader reader) throws IOException {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertTrue(result.getViolations() >= 1, "Embedded assign in while condition should be a violation");
    }
}
