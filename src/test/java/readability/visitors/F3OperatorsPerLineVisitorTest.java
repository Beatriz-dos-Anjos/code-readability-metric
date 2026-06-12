package readability.visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import readability.model.FeatureResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class F3OperatorsPerLineVisitorTest {

    private F3OperatorsPerLineVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new F3OperatorsPerLineVisitor();
    }

    @Test
    void testF3Good() throws IOException {
        Path path = Paths.get("samples", "F3Good.java");
        CompilationUnit ast = StaticJavaParser.parse(path);
        FeatureResult result = visitor.analyze(ast);

        assertEquals(3, result.getFeatureId());
        assertEquals(0, result.getViolations());
        assertTrue(result.getOpportunities() > 0);
        assertEquals(100.0, result.getScore(), 0.01);
    }

    @Test
    void testF3Bad() throws IOException {
        Path path = Paths.get("samples", "F3Bad.java");
        CompilationUnit ast = StaticJavaParser.parse(path);
        FeatureResult result = visitor.analyze(ast);

        assertEquals(3, result.getFeatureId());
        // Based on analysis:
        // L4: (a + b * c) / (d - e % f) -> 5 ops (binary)
        // L5: a * 0.15 + b * 0.10 + c * 0.05 -> 5 ops (binary)
        // L6: (d + e) / (f - a) * 0.1 -> 4 ops (binary)
        // L8: (net * 1.1) / (1 - 0.05) + val -> 4 ops (binary)
        // L12: x > 0 && y < 10 && z != null && w >= min -> 7 ops (binary)
        // Total violations: 5
        assertEquals(5, result.getViolations());
        assertEquals(16, result.getOpportunities());
        assertEquals(100.0 * (1.0 - 5.0/16.0), result.getScore(), 0.01);
    }

    @Test
    void testAnalyzeNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> visitor.analyze(null));
    }
}
