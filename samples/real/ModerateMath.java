public class ModerateMath {
    public double calculateArea(double radius, double height) {
        if (radius > 0) {
            if (height > 0) {
                // One line with high operator density to trigger F3 slightly
                return Math.PI * radius * radius + 2 * Math.PI * radius * height;
            }
        }
        return 0;
    }
}
