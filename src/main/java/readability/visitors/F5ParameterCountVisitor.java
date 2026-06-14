package readability.visitors;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

/**
 * Feature 5 — Parameter Count
 *
 * Opportunities: every method or constructor declaration
 * Violations: method or constructor with more than 3 parameters
 *
 * Score = 100 × (1 − violations / opportunities)
 * If opportunities == 0, score = 100.
 */
public class F5ParameterCountVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations    = 0;
    private int opportunities = 0;

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        opportunities++;
        if (n.getParameters().size() > 3) {
            violations++;
        }
        super.visit(n, arg);
    }

    @Override
    public void visit(ConstructorDeclaration n, Void arg) {
        opportunities++;
        if (n.getParameters().size() > 3) {
            violations++;
        }
        super.visit(n, arg);
    }

    public FeatureResult getResult() {
        double score = (opportunities == 0)
                ? 100.0
                : Math.max(0.0, 100.0 * (1.0 - violations / (double) opportunities));
        return new FeatureResult(violations, opportunities, score, getFeatureId());
    }

    @Override
    public FeatureResult analyze(com.github.javaparser.ast.CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }
        this.violations = 0;
        this.opportunities = 0;
        this.visit(ast, null);
        return getResult();
    }

    @Override
    public int getFeatureId() {
        return 5;
    }

    @Override
    public String getFeatureName() {
        return "Parameter Count (F5)";
    }
}
