// Expected score: 90-100
// Reason: Simple repository interface.
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByLastName(String lastName);
    Optional<User> findByEmail(String email);
}
