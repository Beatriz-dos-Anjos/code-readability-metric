// F3 — caso ruim: densidade de operadores alta. Score esperado: próximo de 0.
// Todos os métodos têm no máximo 2 parâmetros para não violar F5.
public class F3Bad {

    // 4 operators on one line: +, *, -, /
    public int compute(int a, int b) {
        return (a + b) * a - b / 2;
    }

    // 4 operators on one line: +, +, +, +
    public int sumAll(int a, int b) {
        return a + b + a + b + 1;
    }

    // 3 operators on one line: >, &&, <
    public boolean validate(int x, int y) {
        return x > 0 && y < 100 && x != y;
    }

    // 4 operators on one line: %, *, +, -
    public int calculate(int n, int m) {
        return n % m * 2 + n - 1;
    }

    // 4 operators on one line: /, +, *, -
    public double formula(double a, double b) {
        return a / b + b * a - b;
    }

    // 5 operators on one line: +, *, -, %, /
    public int complex(int a, int b) {
        return a + b * a - b % a / 2;
    }
}