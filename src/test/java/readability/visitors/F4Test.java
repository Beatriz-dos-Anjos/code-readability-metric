package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F4Test {

    private FeatureResult runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        return new F4NestingDepthVisitor().analyze(cu);
    }

    private FeatureResult runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        return new F4NestingDepthVisitor().analyze(cu);
    }

    @Test
    void f4Good_shouldHaveZeroViolations() throws Exception {
        FeatureResult result = runOn("F4Good.java");

        assertEquals(0, result.getViolations(), "F4Good should have no nesting violations");
        assertEquals(100.0, result.getScore(), 0.001, "F4Good score should be 100");
    }

    @Test
    void f4Bad_shouldHaveViolationsAndLowScore() throws Exception {
        FeatureResult result = runOn("F4Bad.java");

        assertTrue(result.getViolations() > 0, "F4Bad should have nesting violations");
        assertTrue(result.getScore() < 50.0, "F4Bad score should be below 50");
    }

    @Test
    void noMethods_shouldScoreHundred() {
        FeatureResult result = runOnSource("""
                class Empty {
                }
                """);

        assertEquals(0, result.getOpportunities(), "No methods means 0 opportunities");
        assertEquals(100.0, result.getScore(), 0.001, "Score should be 100 when opportunities == 0");
    }

    @Test
    void depthExactlyTwo_shouldNotViolate() {
        // method body (depth 1) → if block (depth 2): exactly at the limit
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

    @Test
    void depthThree_shouldViolate() {
        // method body (1) → if (2) → for (3): exceeds limit
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