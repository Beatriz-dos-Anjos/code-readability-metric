package readability.model;

import java.util.ArrayList;
import java.util.List;

public class FileReport {
    private final String filePath;
    private final List<FeatureResult> features;
    private final boolean parseable;
    private double finalScore;

    public FileReport(String filePath, List<FeatureResult> features, boolean parseable) {
        this.filePath = filePath;
        this.features = new ArrayList<>(features);
        this.parseable = parseable;
        this.finalScore = 0.0;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<FeatureResult> getFeatures() {
        return features;
    }

    public boolean isParseable() {
        return parseable;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }

    @Override
    public String toString() {
        return "FileReport{" +
               "filePath='" + filePath + '\'' +
               ", features=" + features +
               ", parseable=" + parseable +
               ", finalScore=" + finalScore +
               '}';
    }
}
