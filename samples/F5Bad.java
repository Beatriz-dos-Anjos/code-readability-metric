// F5 — bad case: polyadic methods
package samples;

public class F5Bad {

    // 4 parameters — violation
    public void register(String name, int age, String cpf, String email) {
        validate(name, age, cpf, email);
    }

    // 4 parameters — violation
    private void validate(String name, int age, String cpf, String email) {
        if (name == null || email == null || cpf == null) {
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
        return username != null && password != null;
    }

    // 2 parameters — OK
    public void deactivate(int userId, String reason) {
        // deactivation logic here
    }
}
