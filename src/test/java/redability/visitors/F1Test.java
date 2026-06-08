package legibilidade.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import legibilidade.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F1Test {

    private F1HiddenBracesVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F1HiddenBracesVisitor visitor = new F1HiddenBracesVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    private F1HiddenBracesVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F1HiddenBracesVisitor visitor = new F1HiddenBracesVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    @Test
    void f1Good_shouldHaveZeroViolations() throws Exception {
        F1HiddenBracesVisitor v = runOn("F1Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F1Good should have no violations");
        assertEquals(100.0, result.getScore(), 0.001, "F1Good score should be 100");
    }

    @Test
    void f1Bad_shouldHaveViolationsAndLowScore() throws Exception {
        F1HiddenBracesVisitor v = runOn("F1Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F1Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F1Bad score should be below 50");
    }

    @Test
    void noControlFlow_shouldScoreHundred() {
        String source = """
                class Empty {
                    void doNothing() {}
                    int getValue() { return 42; }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getOpportunities(), "No control-flow means 0 opportunities");
        assertEquals(100.0, result.getScore(), 0.001, "Score should be 100 when opportunities == 0");
    }

    @Test
    void allBracesPresent_shouldScoreHundred() {
        String source = """
                class Good {
                    void check(int x) {
                        if (x > 0) {
                            System.out.println("positive");
                        } else {
                            System.out.println("non-positive");
                        }
                        for (int i = 0; i < x; i++) {
                            System.out.println(i);
                        }
                        while (x > 0) {
                            x--;
                        }
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations());
        assertEquals(100.0, result.getScore(), 0.001);
    }

    @Test
    void missingBraces_shouldCountViolations() {
        String source = """
                class Bad {
                    void check(int x) {
                        if (x > 0)
                            System.out.println("positive");
                        for (int i = 0; i < x; i++)
                            System.out.println(i);
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(2, result.getViolations(), "Two missing-brace violations expected");
        assertTrue(result.getScore() < 100.0);
    }
}
