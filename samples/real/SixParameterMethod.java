// Expected score: 70-89
// Reason: Penalized for having a method with too many parameters.
public class SixParameterMethod {
    public void registerUser(String username, String password, String email, String phone, String address, String role) {
        if (username != null) {
            System.out.println("Registering...");
        }
    }
}
