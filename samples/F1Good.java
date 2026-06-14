// F1 — caso bom: nenhuma chave omitida. Score esperado: 100.
// Sem aninhamento profundo (max depth 2), sem linhas densas, sem params excessivos.
public class F1Good {

    // depth: body=1, if=2 → max=2 ✅
    public boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        if (email.isEmpty()) {
            return false;
        }
        if (!email.contains("@")) {
            return false;
        }
        return true;
    }

    // depth: body=1, if=2 → max=2 ✅ (for sem if dentro)
    public int countActiveUsers(User[] users) {
        if (users == null) {
            return 0;
        }
        int count = 0;
        for (User user : users) {
            count += (user != null && user.isActive()) ? 1 : 0;
        }
        return count;
    }

    // depth: body=1, if=2 → max=2 ✅
    public String getStatus(int code) {
        if (code == 200) {
            return "OK";
        }
        if (code == 404) {
            return "Not Found";
        }
        if (code == 500) {
            return "Server Error";
        }
        return "Unknown";
    }

    // depth: body=1, while=2 → max=2 
    public void printPositive(int[] values) {
        if (values == null) {
            return;
        }
        int i = 0;
        while (i < values.length) {
            System.out.println(values[i]);
            i++;
        }
    }

    // depth: body=1, if=2 → max=2 ✅
    public boolean isInRange(int value, int min, int max) {
        if (value < min) {
            return false;
        }
        if (value > max) {
            return false;
        }
        return true;
    }
}

class User {
    private boolean active;

    public boolean isActive() {
        return active;
    }
}