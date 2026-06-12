package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Feature 3: Operators Per Line
 *
 * Counts operator occurrences per source line. Any line with >= 3 operators
 * is considered a violation (counts as 1 violation per offending line).
 */
public class F3OperatorsPerLineVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null)
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        this.violations = 0;
        this.opportunities = 0;

        Map<Integer, Integer> opsPerLine = new HashMap<>();

        // Count binary operators by their begin line
        for (BinaryExpr be : ast.findAll(BinaryExpr.class)) {
            be.getRange().ifPresent(r -> opsPerLine.merge(r.begin.line, 1, Integer::sum));
        }

        // Also count unary operator occurrences (++, --, +, - when used as unary)
        for (UnaryExpr ue : ast.findAll(UnaryExpr.class)) {
            ue.getRange().ifPresent(r -> opsPerLine.merge(r.begin.line, 1, Integer::sum));
        }

        for (Map.Entry<Integer, Integer> e : opsPerLine.entrySet()) {
            opportunities++;
            if (e.getValue() >= 3)
                violations++;
        }

        return getResult();
    }
    public FeatureResult getResult() {
        double score = (opportunities == 0) ? 100.0
                : Math.max(0.0, 100.0 * (1.0 - ((double) violations / opportunities)));
        return new FeatureResult(violations, opportunities, score, getFeatureId());
    }

    @Override
    public int getFeatureId() {
        return 3;
    }

    @Override
    public String getFeatureName() {
        return "Operators Per Line (F3)";
    }
}
