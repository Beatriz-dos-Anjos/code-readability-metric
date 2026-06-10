// F3 - caso ruim: densidade de operadores alta. Score esperado: próximo de 0
public class F3Bad {
    public double calculate(double a, double b, double c, double d, double e, double f) {
        double val = (a + b * c) / (d - e % f); 
        double tax = a * 0.15 + b * 0.10 + c * 0.05; 
        double discount = (d + e) / (f - a) * 0.1; 
        double net = val + tax - discount; 
        return (net * 1.1) / (1 - 0.05) + val; 
    }

    public boolean check(int x, int y, Integer z, int w, int min) {
        if (x > 0 && y < 10 && z != null && w >= min) { 
             return true;
        }
        return false;
    }
}
