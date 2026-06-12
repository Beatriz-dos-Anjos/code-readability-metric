public class F3Bad {

    // Line with 4 operators: +, *, -, /
    public int compute(int a, int b, int c, int d) {
        return (a + b) * c - d / 2;
    }

    // Line with 4 operators: +, +, +, +
    public int sumAll(int a, int b, int c, int d) {
        return a + b + c + d + 1;
    }

    // Line with 5 operators: >, &&, <, &&, !=
    public boolean validate(int x, int y, Object z) {
        return x > 0 && y < 100 && z != null;
    }

    // Line with 4 operators: %, *, +, -
    public int calculate(int n, int m) {
        return n % m * 2 + n - 1;
    }

    // Line with 4 operators: /, +, *, -
    public double formula(double a, double b, double c) {
        return a / b + b * c - a;
    }

    // Line with 5 operators: +, *, -, %, /
    public int complex(int a, int b, int c) {
        return a + b * c - b % a / 2;
    }
}