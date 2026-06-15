package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import readability.model.FeatureResult;
import readability.model.FileReport;
import readability.score.ScoreCalculator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for F5ParameterCountVisitor and ScoreCalculator.
 * Validates that the visitor correctly detects methods with excessive parameter
 * counts,
 * and that the score calculator properly computes violation scores and final
 * readability metrics.
 */
class F5Test {

    /**
     * Helper method: parses a Java file from the samples directory
     * and executes the F5ParameterCountVisitor on its AST.
     */
    private F5ParameterCountVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F5ParameterCountVisitor visitor = new F5ParameterCountVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    /**
     * Helper method: parses a Java source code string
     * and executes the F5ParameterCountVisitor on its AST.
     */
    private F5ParameterCountVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F5ParameterCountVisitor visitor = new F5ParameterCountVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    /**
     * ASSERTS: F5Good.java sample must have zero violations and a perfect score.
     * This baseline demonstrates code with acceptable parameter counts.
     */
    @Test
    void f5Good_shouldHaveZeroViolations() throws Exception {
        F5ParameterCountVisitor v = runOn("F5Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F5Good should have no parameter violations");
        assertEquals(100.0, result.getScore(), 0.001, "F5Good score should be 100");
    }

    /**
     * ASSERTS: F5Bad.java sample must have at least one violation and a score below
     * 50.
     * This baseline demonstrates code with methods having excessive parameter
     * counts.
     */
    @Test
    void f5Bad_mostMethodsShouldViolate() throws Exception {
        F5ParameterCountVisitor v = runOn("F5Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F5Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F5Bad score should be below 50");
    }

    /**
     * ASSERTS: A class with no methods must have zero opportunities and score
     * 100.0,
     * since there are no methods to validate parameter counts in.
     */
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

    /**
     * ASSERTS: A method with exactly 3 parameters must not violate and score 100.0.
     */
    @Test
    void exactlyThreeParams_shouldNotViolate() {
        String source = """
                class Boundary {
                    void method(int a, int b, int c) {}
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(0, result.getViolations(), "Exactly 3 parameters should not be a violation");
        assertEquals(100.0, result.getScore(), 0.001);
    }

    /**
     * ASSERTS: A method with 4 parameters must count as one violation.
     */
    @Test
    void fourParams_shouldViolate() {
        String source = """
                class TooMany {
                    void method(int a, int b, int c, int d) {}
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(1, result.getViolations(), "4 parameters should be 1 violation");
    }

    /**
     * ASSERTS: A constructor with 4 parameters must count as one violation,
     * applying the same rules as regular methods.
     */
    @Test
    void constructorWithFourParams_shouldViolate() {
        String source = """
                class TooMany {
                    TooMany(int a, int b, int c, int d) {}
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(1, result.getViolations(), "Constructor with 4 parameters should be a violation");
    }

    /**
     * ASSERTS: Among multiple methods with varying parameter counts,
     * only those exceeding the threshold should be counted as violations.
     */
    @Test
    void mixedMethods_partialViolations() {
        String source = """
                class Mixed {
                    void ok(int a, int b) {}
                    void bad(int a, int b, int c, int d) {}
                    void alsoOk(String s) {}
                }
                """;
        FeatureResult result = runOnSource(source).getResult();

        assertEquals(3, result.getOpportunities());
        assertEquals(1, result.getViolations());
    }

    /**
     * ASSERTS: When a feature has zero opportunities (no methods to evaluate),
     * the calculated score must be 100.0 for all features and the final score.
     */
    @Test
    void scoreCalculator_zeroOpportunities_shouldReturnHundred() {
        FeatureResult r = new FeatureResult(0, 0, 0.0); // score not yet set
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r), true);

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(100.0, fr.getScore(), 0.001,
                    "Zero opportunities should yield score 100");
        }
        assertEquals(100.0, report.getFinalScore(), 0.001);
    }

    /**
     * ASSERTS: When violations equal opportunities (all methods violate),
     * the calculated score must be 0.0 for all features and the final score.
     */
    @Test
    void scoreCalculator_allViolations_shouldReturnZero() {
        FeatureResult r = new FeatureResult(5, 5, 0.0);
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r), true);

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(0.0, fr.getScore(), 0.001,
                    "violations == opportunities should yield score 0");
        }
        assertEquals(0.0, report.getFinalScore(), 0.001);
    }

    /**
     * ASSERTS: When violations exceed opportunities (more violations than methods),
     * the calculated score must be clamped to 0.0 and never go negative.
     */
    @Test
    void scoreCalculator_violationsExceedOpportunities_shouldClampToZero() {
        FeatureResult r = new FeatureResult(10, 5, 0.0);
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r), true);

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(0.0, fr.getScore(), 0.001,
                    "Score must never go below 0");
        }
    }

    /**
     * ASSERTS: The final score must be the arithmetic mean of all five feature
     * scores,
     * properly aggregating individual feature evaluations into a single readability
     * metric.
     */
    @Test
    void scoreCalculator_finalScore_isArithmeticMeanOfFive() {
        FeatureResult r100 = new FeatureResult(0, 10, 0.0);
        FeatureResult r50 = new FeatureResult(5, 10, 0.0);
        FileReport report = new FileReport("dummy.java",
                List.of(r100, r100, r50, r100, r100), true);

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        assertEquals(90.0, report.getFinalScore(), 0.001,
                "Final score should be the arithmetic mean of all five feature scores");
    }
}
