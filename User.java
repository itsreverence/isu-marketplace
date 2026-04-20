import java.util.UUID;

/**
 * User class to represent a user in the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 * CWE-767: Access to Critical Private Variable via Public Method, passwordHash been removed + get/set as not needed
 * Password verification is handled internally by UserHandler using BCrypt directly.
 */
public class User {
    private UUID id; // ID of the user
    private String username; // Username of the user
    private Role role; // Role of the user

    /**
     * Constructor for the User class
     *
     * @param id       The ID of the user
     * @param username The username of the user
     * @param role     The role of the user
     */
    public User(UUID id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    /**
     * Getter for the ID of the user
     * 
     * @return The ID of the user
     */
    public UUID getId() {
        return this.id;
    }

    /**
     * Getter for the username of the user
     * 
     * @return The username of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter for the role of the user
     * 
     * @return The role of the user
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Setter for the username of the user
     * 
     * @param username The new username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter for the role of the user
     * 
     * @param role The new role of the user
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
