package legibilidade.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import legibilidade.model.FeatureResult;
import legibilidade.model.FileReport;
import legibilidade.score.ScoreCalculator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class F5Test {

    private F5ParameterCountVisitor runOn(String filename) throws Exception {
        CompilationUnit cu = StaticJavaParser.parse(new File("samples/" + filename));
        F5ParameterCountVisitor visitor = new F5ParameterCountVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    private F5ParameterCountVisitor runOnSource(String source) {
        CompilationUnit cu = StaticJavaParser.parse(source);
        F5ParameterCountVisitor visitor = new F5ParameterCountVisitor();
        visitor.visit(cu, null);
        return visitor;
    }

    @Test
    void f5Good_shouldHaveZeroViolations() throws Exception {
        F5ParameterCountVisitor v = runOn("F5Good.java");
        FeatureResult result = v.getResult();

        assertEquals(0, result.getViolations(), "F5Good should have no parameter violations");
        assertEquals(100.0, result.getScore(), 0.001, "F5Good score should be 100");
    }

    @Test
    void f5Bad_mostMethodsShouldViolate() throws Exception {
        F5ParameterCountVisitor v = runOn("F5Bad.java");
        FeatureResult result = v.getResult();

        assertTrue(result.getViolations() > 0, "F5Bad should have violations");
        assertTrue(result.getScore() < 50.0, "F5Bad score should be below 50");
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

    @Test
    void scoreCalculator_zeroOpportunities_shouldReturnHundred() {
        FeatureResult r = new FeatureResult(0, 0, 0.0); // score not yet set
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r));

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(100.0, fr.getScore(), 0.001,
                    "Zero opportunities should yield score 100");
        }
        assertEquals(100.0, report.getFinalScore(), 0.001);
    }

    @Test
    void scoreCalculator_allViolations_shouldReturnZero() {
        // violations == opportunities → score should be 0
        FeatureResult r = new FeatureResult(5, 5, 0.0);
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r));

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(0.0, fr.getScore(), 0.001,
                    "violations == opportunities should yield score 0");
        }
        assertEquals(0.0, report.getFinalScore(), 0.001);
    }

    @Test
    void scoreCalculator_violationsExceedOpportunities_shouldClampToZero() {
        // Bug scenario: more violations than opportunities — must clamp to 0, not go negative
        FeatureResult r = new FeatureResult(10, 5, 0.0);
        FileReport report = new FileReport("dummy.java",
                List.of(r, r, r, r, r));

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        for (FeatureResult fr : report.getFeatures()) {
            assertEquals(0.0, fr.getScore(), 0.001,
                    "Score must never go below 0");
        }
    }

    @Test
    void scoreCalculator_finalScore_isArithmeticMeanOfFive() {
        // Mix of known scores to verify the average calculation
        FeatureResult r100 = new FeatureResult(0, 10, 0.0);  // will become 100
        FeatureResult r50  = new FeatureResult(5, 10, 0.0);  // will become 50
        FileReport report  = new FileReport("dummy.java",
                List.of(r100, r100, r50, r100, r100));

        ScoreCalculator calc = new ScoreCalculator();
        calc.calculate(report);

        // Expected: (100 + 100 + 50 + 100 + 100) / 5 = 90
        assertEquals(90.0, report.getFinalScore(), 0.001,
                "Final score should be the arithmetic mean of all five feature scores");
    }
}
