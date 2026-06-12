package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

/**
 * Feature 4: Nesting Depth
 *
 * For each method, compute the maximum nesting depth. Method body counts as
 * depth 1.
 * Any method with max depth > 2 is considered a violation.
 */
public class F4NestingDepthVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null)
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        this.violations = 0;
        this.opportunities = 0;

        this.visit(ast, null);
        return getResult();
    }

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        opportunities++;
        int maxDepth = 1;
        if (n.getBody().isPresent()) {
            BlockStmt body = n.getBody().get();
            maxDepth = Math.max(maxDepth, computeMaxDepth(body, 1));
        }
        if (maxDepth > 2)
            violations++;
        super.visit(n, arg);
    }

    private int computeMaxDepth(Node node, int current) {
        int max = current;
        for (Node child : node.getChildNodes()) {
            if (child instanceof IfStmt
                    || child instanceof ForStmt
                    || child instanceof ForEachStmt
                    || child instanceof WhileStmt
                    || child instanceof DoStmt
                    || child instanceof SwitchStmt) {
                int inner = computeMaxDepth(child, current + 1);
                if (inner > max)
                    max = inner;
            } else if (child instanceof BlockStmt) {
                int inner = computeMaxDepth(child, current);
                if (inner > max)
                    max = inner;
            } else {
                int inner = computeMaxDepth(child, current);
                if (inner > max)
                    max = inner;
            }
        }
        return max;
    }

    public FeatureResult getResult() {
        double score = (opportunities == 0) ? 100.0
                : Math.max(0.0, 100.0 * (1.0 - ((double) violations / opportunities)));
        return new FeatureResult(violations, opportunities, score, getFeatureId());
    }

    @Override
    public int getFeatureId() {
        return 4;
    }

    @Override
    public String getFeatureName() {
        return "Nesting Depth (F4)";
    }
}
