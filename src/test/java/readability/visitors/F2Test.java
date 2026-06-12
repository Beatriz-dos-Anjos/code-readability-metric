package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F2Test {

    private F2EmbeddedAssignVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F2EmbeddedAssignVisitor visitor = new F2EmbeddedAssignVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    private F2EmbeddedAssignVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F2EmbeddedAssignVisitor visitor = new F2EmbeddedAssignVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    @Test
    void f2Good_shouldHaveZeroViolations() throws Exception {
        F2EmbeddedAssignVisitor v = runOn("F2Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F2Good should have no embedded-assign violations");
        assertEquals(100.0, result.getScore(), 0.001, "F2Good score should be 100");
    }

    @Test
    void f2Bad_shouldHaveViolationsAndLowScore() throws Exception {
        F2EmbeddedAssignVisitor v = runOn("F2Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F2Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F2Bad score should be below 50");
    }

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

    @Test
    void embeddedAssignInWhile_shouldCountAsViolation() {
        // while ((line = reader.readLine()) != null) pattern
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
