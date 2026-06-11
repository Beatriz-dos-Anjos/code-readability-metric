package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Feature 3: Operators Per Line
 * 
 * Heuristic:
 * - Counts operators (Binary and Unary expressions) per line.
 * - An "opportunity" is any non-empty source line (LOC).
 * - A "violation" is any line that contains 3 or more operators.
 * - Score: 100% * (1 - violations/opportunities)
 */
public class F3OperatorsPerLineVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;
    private final Map<Integer, Integer> lineOperators = new HashMap<>();

    @Override
    public void visit(BinaryExpr n, Void arg) {
        n.getBegin().ifPresent(pos -> incrementLine(pos.line));
        super.visit(n, arg);
    }

    @Override
    public void visit(UnaryExpr n, Void arg) {
        n.getBegin().ifPresent(pos -> incrementLine(pos.line));
        super.visit(n, arg);
    }

    private void incrementLine(int line) {
        lineOperators.put(line, lineOperators.getOrDefault(line, 0) + 1);
    }

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }

        this.lineOperators.clear();
        this.violations = 0;
        this.opportunities = 0;

        // Visit the AST to count operators per line
        ast.accept(this, null);

        // Each line with >= 3 operators counts as 1 violation
        for (int count : lineOperators.values()) {
            if (count >= 3) {
                violations++;
            }
        }

        // Opportunities = total non-empty lines (LOC)
        this.opportunities = countNonEmptyLines(ast);

        return getResult();
    }

    private int countNonEmptyLines(CompilationUnit ast) {
        String source = "";
        if (ast.getStorage().isPresent()) {
            try {
                source = Files.readString(ast.getStorage().get().getPath());
            } catch (IOException e) {
                // Fallback if file cannot be read
                source = ast.toString();
            }
        } else {
            // Fallback if source is not available via storage
            source = ast.toString();
        }

        String[] lines = source.split("\r?\n");
        int count = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public FeatureResult getResult() {
        double score = (opportunities == 0) ? 100.0 : 100.0 * (1.0 - ((double) violations / opportunities));
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
