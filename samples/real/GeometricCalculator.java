public class GeometricCalculator {
    public double compute(double a, double b, double c, double d, double e, double f, double g) {
        if (a > 0 && b > 0) {
            return (a * b) / c + (d * e) - (f / g) + 10.5 * a;
        }
        return 0;
    }
}
