// F5 — caso ruim: métodos poliadicos. Score esperado: próximo de 0.
// Sem linhas densas de operadores para não violar F3.
public class F5Bad {

    // 4 parameters — violation
    public void register(String name, int age, String cpf, String email) {
        validate(name, age, cpf, email);
    }

    // 4 parameters — violation
    private void validate(String name, int age, String cpf, String email) {
        boolean missingName = name == null;
        boolean missingEmail = email == null;
        if (missingName || missingEmail) {
            throw new IllegalArgumentException("Missing required fields");
        }
    }

    // 5 parameters — violation
    public void createAddress(String street, String city, String state,
                              String zip, String country) {
        // persistence logic here
    }

    // 6 parameters — violation (guarantees a low score)
    public void updateProfile(int userId, String name, int age,
                              String phone, String email, String bio) {
        // update logic here
    }

    // 4 parameters — violation
    public boolean authenticate(String username, String password,
                                String ipAddress, String deviceId) {
        boolean hasUser = username != null;
        boolean hasPass = password != null;
        return hasUser && hasPass;
    }

    // 2 parameters — OK
    public void deactivate(int userId, String reason) {
        // deactivation logic here
    }
}