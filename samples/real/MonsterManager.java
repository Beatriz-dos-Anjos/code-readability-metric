public class MonsterManager {
    public int executeChaos(int a, int b, int c, int d, int e, int f, int g, int h, int i, int j, int k, int l) {
        int result = 0;
        int temp;
        if ((temp = a + b - c * d / e) > 0)
            for (int z = 0; z < f; z++)
                if ((result = temp + g - h * i / j + k - l) != 0)
                    while ((temp = result % 2) == 0)
                        if (a > b)
                            if (c < d)
                                if (e == f)
                                    result = result * a / b + c - d * e / f + g - h * i;
        return result;
    }
}
