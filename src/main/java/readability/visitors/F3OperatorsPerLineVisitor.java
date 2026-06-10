package readability.visitors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import readability.model.FeatureResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Feature 3: Operators Per Line
 *
 * Heuristic:
 * - For each source line, count occurrences of common Java operators.
 * - An "opportunity" is any line that contains at least one operator.
 * - A "violation" is a line that contains more than one operator (threshold = 1).
 *
 * Score formula: 100 * (1 - violations/opportunities) (100% if opportunities == 0)
 */
public class F3OperatorsPerLineVisitor extends VoidVisitorAdapter<Void> implements FeatureVisitor {

    private int violations = 0;
    private int opportunities = 0;

    // match longer operators first (==, !=, >=, <=, &&, ||, +=, -=, etc.)
    private static final Pattern OPERATOR_PATTERN = Pattern.compile(
        "(==|!=|>=|<=|&&|\\|\\||\\+=|-=|\\*=|/=|%=?|<<=?|>>=?|>>>?=?|&|\\||\\^|~|\\+|-|\\*|/|%|>|<|=)"
    );

    @Override
    public FeatureResult analyze(CompilationUnit ast) {
        if (ast == null) {
            throw new IllegalArgumentException("CompilationUnit cannot be null");
        }

        this.violations = 0;
        this.opportunities = 0;

        String src = ast.toString();
        String[] lines = src.split("\r?\n");

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            Matcher m = OPERATOR_PATTERN.matcher(trimmed);
            int count = 0;
            while (m.find()) {
                count++;
            }

            if (count > 0) {
                opportunities++;
                if (count > 1) {
                    violations++;
                }
            }
        }

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
