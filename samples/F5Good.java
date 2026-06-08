// F5 — good case: methods with few parameters
package samples;

public class F5Good {

    // Groups related data into an object instead of listing every field
    public void register(User user) {
        validateUser(user);
        saveUser(user);
    }

    public User findById(int id) {
        // single parameter — perfectly fine
        return null;
    }

    public boolean authenticate(String username, String password) {
        // two parameters — fine
        return username != null && password != null;
    }

    public void updateEmail(int userId, String newEmail) {
        // two parameters — fine
    }

    public boolean hasPermission(int userId, String role) {
        // two parameters — fine
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

    // Inner stub so the file compiles standalone
    static class User {
        int id;
        String name;
        String email;
    }
}
