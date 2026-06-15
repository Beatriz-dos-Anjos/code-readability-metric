// Expected score: Below 50
// Reason: Terrible formatting, obscure naming, and high complexity.
public class NightmareCode {
    public void run(int a, int b, int c, int d, int e, int f) {
        int k = 0;
        if ((k = a + b) > 5)
            if ((a = c + d) > 2)
                for (int i=0; i<e; i++)
                    while (k > 0)
                        k = k - 1 + a * b / c - d + e * f;
    }
}
