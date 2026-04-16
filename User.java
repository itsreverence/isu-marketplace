import java.util.UUID;

/**
 * User class to represent a user in the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 */
public class User {
    private UUID id; // ID of the user
    private String username; // Username of the user
    private String passwordHash; // Password hash of the user
    private Role role; // Role of the user

    /**
     * Constructor for the User class
     * 
     * @param id           The ID of the user
     * @param username     The username of the user
     * @param passwordHash The password hash of the user
     * @param role         The role of the user
     */
    public User(UUID id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
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
     * Getter for the password hash of the user
     * 
     * @return The password hash of the user
     */
    public String getPasswordHash() {
        return this.passwordHash;
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
     * Setter for the password hash of the user
     * 
     * @param passwordHash The new password hash of the user
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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
