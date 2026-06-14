package readability.score;

import readability.model.FeatureResult;
import readability.model.FileReport;

import java.util.List;

/**
 * Calculates readability scores for a FileReport using the Buse & Weimer formula:
 *   score = 100 * (1 - violations / opportunities)
 *
 * Rules:
 *   - opportunities == 0  → score = 100.0
 *   - score < 0           → clamped to 0.0
 *   - finalScore          → arithmetic mean of all feature scores
 */
public class ScoreCalculator {

    public FileReport calculate(FileReport report) {
        List<FeatureResult> features = report.getFeatures();
            System.out.println("ScoreCalculator executed");


        double total = 0.0;
        for (int i = 0; i < features.size(); i++) {
            FeatureResult original = features.get(i);
            double score = computeScore(original.getViolations(), original.getOpportunities());
            features.set(i, new FeatureResult(
                    original.getViolations(),
                    original.getOpportunities(),
                    score,
                    original.getFeatureId()));
            total += score;
        }

        double finalScore = features.isEmpty() ? 0.0 : total / features.size();
        report.setFinalScore(finalScore);
        return report;
    }

    private double computeScore(int violations, int opportunities) {
        if (opportunities == 0) {
            return 100.0;
        }
        double score = 100.0 * (1.0 - (double) violations / opportunities);
        return Math.max(0.0, score);
    }
}
