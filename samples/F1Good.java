// F1 - good case: no missing braces
// No deep nesting, no dense lines, no excessive parameters
public class F1Good {

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