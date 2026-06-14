// F5 — caso bom: métodos com poucos parâmetros. Score esperado: 100.
// Sem linhas densas de operadores (max 2 por linha), sem aninhamento profundo.
public class F5Good {

    public void register(User user) {
        validateUser(user);
        saveUser(user);
    }

    public User findById(int id) {
        return null;
    }

    // Split condition into separate variables to avoid dense operator line
    public boolean authenticate(String username, String password) {
        boolean hasUsername = username != null;
        boolean hasPassword = password != null;
        return hasUsername && hasPassword;
    }

    public void updateEmail(int userId, String newEmail) {
        // two parameters — fine
    }

    public boolean hasPermission(int userId, String role) {
        return false;
    }

    public void deactivate(int userId) {
        // one parameter — fine
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
    }

    private void saveUser(User user) {
        // persistence logic here
    }

    static class User {
        int id;
        String name;
        String email;
    }
}