class F3Bad {
    int compute(int a, int b, int c, int d) {
        return (a + b) * c - d / 2; // dense operators on one line
    }

    int mixed() {
        int x = 1 + 2 + 3 + 4; // multiple operators but counts per-line
        return x;
    }
}
