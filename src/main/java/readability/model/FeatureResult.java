package readability.model;

/**
 * Represents the result of analyzing a single code readability feature.
 * 
 * Each feature (F1 through F5) produces one FeatureResult containing:
 * - violations: number of code instances that violate the metric
 * - opportunities: number of code instances where the metric applies
 * - score: calculated percentage score (100% if no violations, lower otherwise)
 * - featureId: which feature this result belongs to (1-5)
 * 
 * This class is immutable and thread-safe.
 */
public class FeatureResult {
    private final int violations;
    private final int opportunities;
    private final double score;
    private final int featureId;

    /**
     * Creates a new FeatureResult.
     * 
     * @param violations number of violations found
     * @param opportunities total opportunities where violation could occur
     * @param score calculated percentage score [0, 100]
     * @param featureId feature identifier (1-5)
     */
    public FeatureResult(int violations, int opportunities, double score, int featureId) {
        this.violations = violations;
        this.opportunities = opportunities;
        this.score = Math.max(0.0, Math.min(100.0, score));
        this.featureId = featureId;
    }

    /**
     * Legacy constructor for backwards compatibility (F1 only).
     * Use the 4-argument constructor for new code.
     */
    public FeatureResult(int violations, int opportunities, double score) {
        this(violations, opportunities, score, 1);
    }

    public int getViolations() { 
        return violations; 
    }

    public int getOpportunities() { 
        return opportunities; 
    }

    public double getScore() { 
        return score; 
    }

    public int getFeatureId() {
        return featureId;
    }

    @Override
    public String toString() {
        return "FeatureResult{" + 
               "featureId=" + featureId +
               ", violations=" + violations + 
               ", opportunities=" + opportunities + 
               ", score=" + String.format("%.2f%%", score) + 
               '}';
    }
}