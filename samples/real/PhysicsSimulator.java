// Expected score: Below 50
// Reason: Massive complexity, huge methods, and terrible naming conventions.
public class PhysicsSimulator {
    public double calculateTrajectory(double v, double theta, double g, double h, double w, double f, double m, double t) {
        double x;
        double y;
        if ((x = v * Math.cos(theta) * t - 0.5 * f * w * t * t / m) > 0)
            if ((y = h + v * Math.sin(theta) * t - 0.5 * g * t * t + f * w / m) > 0)
                return Math.sqrt(x * x + y * y) * 2.0 / 3.0 + 4.5 * v - g / 2.0;
        return 0.0;
    }
}
