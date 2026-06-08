package legibilidade.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import legibilidade.model.FeatureResult;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class F4Test {

    private F4NestingDepthVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F4NestingDepthVisitor visitor = new F4NestingDepthVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    private F4NestingDepthVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F4NestingDepthVisitor visitor = new F4NestingDepthVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    @Test
    void f4Good_shouldHaveZeroViolations() throws Exception {
        F4NestingDepthVisitor v = runOn("F4Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F4Good should have no nesting violations");
        assertEquals(100.0, result.getScore(), 0.001, "F4Good score should be 100");
    }

    @Test
    void f4Bad_shouldHaveViolationsAndLowScore() throws Exception {
        F4NestingDepthVisitor v = runOn("F4Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F4Bad should have nesting violations");
        assertTrue(result.getScore() < 50.0, "F4Bad score should be below 50");
    }

    @Test
    void noMethods_shouldScoreHundred() {
        String source = """
                class Empty {
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getOpportunities(), "No methods means 0 opportunities");
        assertEquals(100.0, result.getScore(), 0.001, "Score should be 100 when opportunities == 0");
    }

    @Test
    void depthExactlyTwo_shouldNotViolate() {
        // method body (depth 1) → if block (depth 2): exactly at the limit
        String source = """
                class ShallowNesting {
                    void process(int x) {
                        if (x > 0) {
                            System.out.println("positive");
                        }
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations(), "Depth of 2 should not be a violation");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    @Test
    void depthThree_shouldViolate() {
        // method body (1) → if (2) → for (3): exceeds limit
        String source = """
                class DeepNesting {
                    void process(int[] data) {
                        if (data != null) {
                            for (int item : data) {
                                System.out.println(item);
                            }
                        }
                    }
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(1, result.getViolations(), "Depth of 3 should count as one violation");
    }

    @Test
    void twoMethods_onlyOneDeep_oneViolation() {
        String source = """
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
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(2, result.getOpportunities(), "Two methods = two opportunities");
        assertEquals(1, result.getViolations(), "Only the deep method should be a violation");
    }
}
