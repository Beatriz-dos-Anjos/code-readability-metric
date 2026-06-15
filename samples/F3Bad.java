// F3 - bad case: high operator density
// All methods have at most 2 parameters so they do not violate F5.
public class F3Bad {

    public int compute(int a, int b) {
        return (a + b) * a - b / 2;
    }

    public int sumAll(int a, int b) {
        return a + b + a + b + 1;
    }

    public boolean validate(int x, int y) {
        return x > 0 && y < 100 && x != y;
    }

    public int calculate(int n, int m) {
        return n % m * 2 + n - 1;
    }

    public double formula(double a, double b) {
        return a / b + b * a - b;
    }

    public int complex(int a, int b) {
        return a + b * a - b % a / 2;
    }
}