// F5 - bad case: polyadic methods
// No dense operator lines to avoid violating F3
public class F5Bad {

    public void register(String name, int age, String cpf, String email) {
        validate(name, age, cpf, email);
    }

    private void validate(String name, int age, String cpf, String email) {
        boolean missingName = name == null;
        boolean missingEmail = email == null;
        if (missingName || missingEmail) {
            throw new IllegalArgumentException("Missing required fields");
        }
    }

    public void createAddress(String street, String city, String state,
                              String zip, String country) {
        // persistence logic here
    }

    public void updateProfile(int userId, String name, int age,
                              String phone, String email, String bio) {
        // update logic here
    }

    public boolean authenticate(String username, String password,
                                String ipAddress, String deviceId) {
        boolean hasUser = username != null;
        boolean hasPass = password != null;
        return hasUser && hasPass;
    }

    public void deactivate(int userId, String reason) {
        // deactivation logic here
    }
}