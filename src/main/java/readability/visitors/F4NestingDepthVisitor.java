package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

/**
 * Feature 4: Nesting Depth
 *
 * Detects methods whose maximum block-nesting depth exceeds 2.
 *
 * Depth convention (corpo do método = nível 0, não conta):
 *   for { }              → depth 1  (ok)
 *   for { if { } }       → depth 2  (ok)
 *   for { if { for{} } } → depth 3  (VIOLAÇÃO — > 2)
 *
 * Isso é alcançado iniciando currentDepth = -1 em cada método,
 * de modo que a primeira BlockStmt (corpo do método) vire depth 0.
 *
 * Metrics:
 *   - Opportunities: total de métodos + construtores
 *   - Violations:    métodos/construtores com maxDepth > 2
 *   - Score:         100 * (1 − violations / opportunities)
 */

public class F4NestingDepthVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;

    // Per-method state — saved/restored to handle inner classes and lambdas
    private int currentDepth = 0;
    private int maxDepth = 0;

    // -------------------------------------------------------------------------
    // MethodDeclaration: reset counters, visit body, record result
    // -------------------------------------------------------------------------

    @Override
    public void visit(MethodDeclaration n, Void arg) {
        int savedCurrent = currentDepth;
        int savedMax = maxDepth;

        currentDepth = -1;
        maxDepth = -1;

        super.visit(n, arg); // triggers visit(BlockStmt) for the method body

        opportunities++;
        if (maxDepth > 2) {
            violations++;
        }

        // Restore so that outer-scope methods (inner classes) are unaffected
        currentDepth = savedCurrent;
        maxDepth = savedMax;
    }

    // -------------------------------------------------------------------------
    // ConstructorDeclaration: same rule as methods
    // -------------------------------------------------------------------------

    @Override
    public void visit(ConstructorDeclaration n, Void arg) {
        int savedCurrent = currentDepth;
        int savedMax = maxDepth;

        currentDepth = -1;
        maxDepth = -1;

        super.visit(n, arg);

        opportunities++;
        if (maxDepth > 2) {
            violations++;
        }

        currentDepth = savedCurrent;
        maxDepth = savedMax;
    }

    // -------------------------------------------------------------------------
    // BlockStmt: manual depth tracking (VoidVisitorAdapter has no exit event)
    // -------------------------------------------------------------------------

    @Override
    public void visit(BlockStmt n, Void arg) {
        currentDepth++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
        super.visit(n, arg); // recurse into children
        currentDepth--;
    }

    // -------------------------------------------------------------------------
    // FeatureVisitor contract
    // -------------------------------------------------------------------------

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }
        this.violations = 0;
        this.opportunities = 0;
        this.currentDepth = 0;
        this.maxDepth = 0;
        this.visit(ast, null);
        return getResult();
    }

    public FeatureResult getResult() {
        double score;
        if (opportunities == 0) {
            score = 100.0;
        } else {
            score = Math.max(0.0, 100.0 * (1.0 - (double) violations / opportunities));
        }
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