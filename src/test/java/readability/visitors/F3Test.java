package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F3Test {

    private FeatureResult runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        return new F3OperatorsPerLineVisitor().analyze(cu);
    }

    private FeatureResult runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        return new F3OperatorsPerLineVisitor().analyze(cu);
    }

    @Test
    void f3Good_shouldHaveZeroViolations() throws Exception {
        FeatureResult result = runOn("F3Good.java");

        assertEquals(0, result.getViolations(), "F3Good should have no operator-density violations");
        assertEquals(100.0, result.getScore(), 0.001, "F3Good score should be 100");
    }

    @Test
    void f3Bad_shouldHaveViolationsAndLowScore() throws Exception {
        FeatureResult result = runOn("F3Bad.java");

        assertTrue(result.getViolations() > 0, "F3Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F3Bad score should be below 50");
    }

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

    @Test
    void denseLineShouldCountAsOneViolation() {
        // One line with 4 operators — should be exactly 1 violation regardless of count
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