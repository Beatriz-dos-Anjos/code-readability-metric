package legibilidade.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import legibilidade.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F3Test {

    private F3OperatorsPerLineVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F3OperatorsPerLineVisitor visitor = new F3OperatorsPerLineVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    private F3OperatorsPerLineVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F3OperatorsPerLineVisitor visitor = new F3OperatorsPerLineVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    @Test
    void f3Good_shouldHaveZeroViolations() throws Exception {
        F3OperatorsPerLineVisitor v = runOn("F3Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F3Good should have no operator-density violations");
        assertEquals(100.0, result.getScore(), 0.001, "F3Good score should be 100");
    }

    @Test
    void f3Bad_shouldHaveViolationsAndLowScore() throws Exception {
        F3OperatorsPerLineVisitor v = runOn("F3Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F3Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F3Bad score should be below 50");
    }

    @Test
    void noOperators_shouldScoreHundred() {
        String source = """
                class NoOps {
                    void sayHello() {
                        System.out.println("hello");
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations(), "No operators means no violations");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    @Test
    void denseLineShouldCountAsOneViolation() {
        // One line with 4 operators — should be exactly 1 violation regardless of count
        String source = """
                class Dense {
                    int compute(int a, int b, int c, int d) {
                        return (a + b) * c - d / 2;
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(1, result.getViolations(),
                "A line with >= 3 operators counts as exactly 1 violation");
    }

    @Test
    void twoOperatorsPerLine_shouldNotViolate() {
        String source = """
                class Sparse {
                    int compute(int a, int b) {
                        int sum = a + b;
                        int diff = a - b;
                        return sum * diff;
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations(), "Lines with <= 2 operators should not violate");
        assertEquals(100.0, result.getScore(), 0.001);
    }
}
