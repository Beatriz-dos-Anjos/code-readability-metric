// Expected score: 50-69
// Reason: Complex geometric formulas making it hard to read.
public class GeometricCalculator {
    public double compute(double a, double b, double c, double d, double e, double f, double g) {
        if (a > 0 && b > 0) {
            return (a * b) / c + (d * e) - (f / g) + 10.5 * a;
        }
        return 0;
    }
}
