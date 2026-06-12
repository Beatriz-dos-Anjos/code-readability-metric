public class F3Good {

    public int sum(int a, int b) {
        return a + b;
    }

    public int difference(int a, int b) {
        return a - b;
    }

    public int product(int a, int b) {
        return a * b;
    }

    public boolean isPositive(int x) {
        return x > 0;
    }

    public boolean isInRange(int x, int min, int max) {
        boolean aboveMin = x >= min;
        boolean belowMax = x <= max;
        return aboveMin && belowMax;
    }

    public double average(double a, double b) {
        double total = a + b;
        return total / 2;
    }
}