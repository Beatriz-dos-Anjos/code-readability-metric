package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

/**
 * Feature 1: Hidden Braces
 * 
 * Detects the absence of curly braces {} in control flow statements
 * (if, for, while, etc).
 * 
 * Metrics:
 * - Opportunities: Each if/else/for/while that could have braces
 * - Violations: Opportunities without braces
 * - Score: 100% * (1 - violations/opportunities)
 * 
 * Correctly handles:
 * - Single-line statements without braces
 * - Chained else-if (counts as 1 if, not double)
 * - Nested control structures
 * - Empty code (score = 100%)
 */
public class F1HiddenBracesVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {
    
    private int violations = 0;
    private int opportunities = 0;

    @Override
    public void visit(IfStmt n, Void arg) {
        opportunities++;
        if (!(n.getThenStmt() instanceof BlockStmt)) {
            violations++;
        }

        if (n.getElseStmt().isPresent()) {
            Statement elseStmt = n.getElseStmt().get();
            // Do NOT count else-if as a separate opportunity
            // (it will be counted when we visit the nested IfStmt)
            if (!(elseStmt instanceof IfStmt)) {
                opportunities++;
                if (!(elseStmt instanceof BlockStmt)) {
                    violations++;
                }
            }
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        opportunities++;
        if (!(n.getBody() instanceof BlockStmt)) {
            violations++;
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        opportunities++;
        if (!(n.getBody() instanceof BlockStmt)) {
            violations++;
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        opportunities++;
        if (!(n.getBody() instanceof BlockStmt)) {
            violations++;
        }
        super.visit(n, arg);
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
        double score = (opportunities == 0) ? 100.0 : 100.0 * (1.0 - ((double) violations / opportunities));
        return new FeatureResult(violations, opportunities, score, getFeatureId());
    }

    @Override
    public int getFeatureId() {
        return 1;
    }

    @Override
    public String getFeatureName() {
        return "Hidden Braces (F1)";
    }
}