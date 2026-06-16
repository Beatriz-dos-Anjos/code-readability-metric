// Expected score: 90-100
// Reason: Simple Data Transfer Object.
public record UserDTO(String id, String username, String email) {
    public boolean isValid() {
        return email != null && email.contains("@");
    }
}
