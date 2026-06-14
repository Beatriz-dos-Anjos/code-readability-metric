public class MathUtils {
    public static double calculatePhysics(double v0, double t, double a, double d) {
        return v0 * t + (0.5 * a * t * t) - d / 2 + 10 * a;
    }
}
