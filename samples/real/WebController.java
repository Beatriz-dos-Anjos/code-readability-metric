// Expected score: 90-100
// Reason: Clean web controller with well-defined endpoints.
public class WebController {
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.findAll();
    }
    
    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        if (user.isValid()) {
            userService.save(user);
        }
    }
}
