package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

/**
 * Feature 2: Embedded Assignments
 *
 * Detects assignments (=) and increment/decrement operators (++/--) embedded
 * inside conditional expressions of if, while, and for statements.
 *
 * Metrics:
 * - Opportunities: each condition inspected (one per if/while/for that has a condition)
 * - Violations: AssignExpr or increment/decrement UnaryExpr found inside the condition
 */
public class F2EmbeddedAssignVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;

    @Override
    public void visit(IfStmt n, Void arg) {
        opportunities++;
        violations += countViolationsInCondition(n.getCondition());
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        opportunities++;
        violations += countViolationsInCondition(n.getCondition());
        super.visit(n, arg);
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        if (n.getCompare().isPresent()) {
            opportunities++;
            violations += countViolationsInCondition(n.getCompare().get());
        }
        super.visit(n, arg);
    }

    private int countViolationsInCondition(Expression condition) {
        int count = 0;
        count += condition.findAll(AssignExpr.class).size();
        count += condition.findAll(UnaryExpr.class).stream()
                .filter(u -> u.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT
                          || u.getOperator() == UnaryExpr.Operator.PREFIX_DECREMENT
                          || u.getOperator() == UnaryExpr.Operator.POSTFIX_INCREMENT
                          || u.getOperator() == UnaryExpr.Operator.POSTFIX_DECREMENT)
                .count();
        return count;
    }

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }
        this.violations = 0;
        this.opportunities = 0;
        this.visit(ast, null);
        return getResult();
    }

    public FeatureResult getResult() {
        double score = (opportunities == 0) ? 100.0
                : Math.max(0.0, 100.0 * (1.0 - (double) violations / opportunities));
        return new FeatureResult(violations, opportunities, score, getFeatureId());
    }

    @Override
    public int getFeatureId() {
        return 2;
    }

    @Override
    public String getFeatureName() {
        return "Embedded Assignment (F2)";
    }
}
