// F5 - good case: methods with few parameters
// No dense operator lines, no deep nesting
public class F5Good {

    public void register(User user) {
        validateUser(user);
        saveUser(user);
    }

    public User findById(int id) {
        return null;
    }

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