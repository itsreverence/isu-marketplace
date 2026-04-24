import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * User handler to manage the users in the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
*  CWE-366: All methods that access the shared database connection are synchronized to prevent
 * concurrent threads from reading/modifying data in an inconsistent state.
 */
public class UserHandler extends DatabaseHandler {


    private static final int MAX_FAILED_ATTEMPTS = 5; //CWE-307
    private static final int NO_LOG_REGISTER_ATTEMPTS = 3; // Number of failed register attempts before logging
    private int loginAttempts = 0; //CWE 307
    private int registerAttempts = 0; // Counter for failed register attempts
    

    /**
     * Constructor for the UserHandler class
     * 
     * @param databaseName The name of the database
     */
    public UserHandler(String databaseName) {
        super(databaseName);
    }

    /**
     * Create the table if it doesn't exist. Additionally, if it didn't exist, adds admin accounts
     */
    @Override
    public void createTable() {
        // CWE-459: Incomplete Cleanup
        try (Statement statement = this.connection.createStatement()) {
            statement.setQueryTimeout(30);
            statement.executeUpdate(
                    "create table if not exists user (id string, username string, passwordHash string, role string)");

            // check if the table is empty before adding admins
            if (getUsers().isEmpty()) {
                // Adds admins for testing and demonstrations only.
                register("admin1", "admin", true);
                register("admin2", "admin", true);
            }
            // CWE-778: Insufficient Logging
            logger.info("User table created");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Initialize the logger for the user handler
     * 
     * @return the logger for the user handler
     */
    @Override
    public Logger initLogger() {
        try {
            Logger logger = Logger.getLogger("UserLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            // CWE-779: Logging of Excessive Data
            FileHandler fileHandler = new FileHandler("./logs/UserLogger.log", 1000000, 1, true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            // CWE-778: Insufficient Logging
            logger.info("User logger initialized");
            return logger;
        } catch (IOException e) {
            // CWE-778: Insufficient Logging
            System.err.println("Error initializing logger: " + e.getMessage());
        }
        return null;
    }

    //CWE-117
    private String sanitizeForLog(String input) {
        if (input == null) {
            return "(null)";
        }
        return input.replaceAll("[\r\n\t]", "_");
    }

    /**
     * Register a new user
     * 
     * @param username The username of the user
     * @param password The password of the user
     * @return The new user
     */
    public synchronized User register(String username, String password, boolean isAdmin) {
        // CWE-779: Logging of Excessive Data
        if (registerAttempts >= NO_LOG_REGISTER_ATTEMPTS) {
            logger.warning("User " + sanitizeForLog(username) + " has failed to register " + registerAttempts + " times");
        }
        User user = null;
        String checkUsernameQuery = "SELECT * FROM user WHERE username = ?";
        String insertUserQuery = "INSERT INTO user (id, username, passwordHash, role) VALUES (?, ?, ?, ?)";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement checkUsernamePreparedStatement = this.connection.prepareStatement(checkUsernameQuery); PreparedStatement insertUserPreparedStatement = this.connection.prepareStatement(insertUserQuery)) {
            checkUsernamePreparedStatement.setString(1, username);
            checkUsernamePreparedStatement.setQueryTimeout(30);
            ResultSet checkUsernameResultSet = checkUsernamePreparedStatement.executeQuery();
            if (checkUsernameResultSet.next()) {
                System.err.println("Username already exists, please try again.");
                // CWE-779: Logging of Excessive Data
                logger.fine("Username " + sanitizeForLog(username) + " already exists");
                registerAttempts++;
                return null;
            }
            UUID id = UUID.randomUUID();
            String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            Role userRole = isAdmin ? Role.ADMIN : Role.MEMBER;
            insertUserPreparedStatement.setString(1, id.toString());
            insertUserPreparedStatement.setString(2, username);
            insertUserPreparedStatement.setString(3, passwordHash);
            insertUserPreparedStatement.setString(4, userRole.toString());
            insertUserPreparedStatement.setQueryTimeout(30);
            insertUserPreparedStatement.executeUpdate();
            user = new User(id, username, userRole);
            // CWE-778: Insufficient Logging
            logger.info("User " + sanitizeForLog(username) + " registered");
            registerAttempts = 0;
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error registering user: " + e.getMessage());
        }
        return user;
    }

    /**
     * Login a user
     * 
     * @param username The username of the user
     * @param password The password of the user
     * @return The logged in user
     */
    public synchronized User login(String username, String password) {
        //CWE 307
        if (loginAttempts >= MAX_FAILED_ATTEMPTS) {
            System.out.println("Max login attempts reached.");
            logger.warning("Max login attempts reached for " + sanitizeForLog(username));
            return null;
        }
        User user = null;
        String loginQuery = "SELECT * FROM user WHERE username = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(loginQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String passwordHash = resultSet.getString("passwordHash");
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), passwordHash);
                if (result.verified) {
                    UUID id = UUID.fromString(resultSet.getString("id"));
                    Role role = Role.valueOf(resultSet.getString("role"));
                    user = new User(id, username, role);
                    // CWE-778: Insufficient Logging
                    logger.info("User " + sanitizeForLog(username) + " logged in successfully");
                    loginAttempts = 0; //CWE 307
                } else {
                    // CWE-779: Logging of Excessive Data
                    logger.fine("Username " + sanitizeForLog(username) + " failed to login");
                    loginAttempts++; //CWE 307
                }
            } else {
                // CWE-779: Logging of Excessive Data
                logger.fine("Username " + sanitizeForLog(username) + " does not exist");
                loginAttempts++; //CWE 307
            }
        } catch (SQLTimeoutException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error logging in: " + e.getMessage());
            System.out.println("Invalid login.");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error logging in: " + e.getMessage());
        }
        // instantiated user: logged in! null, failed
        return user;
    }

    /**
     * Get all the users
     * 
     * @return All the users
     */
    public synchronized List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String getUsersQuery = "SELECT * FROM user";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getUsersQuery)) {
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String username = resultSet.getString("username");
                Role role = Role.valueOf(resultSet.getString("role"));
                users.add(new User(id, username, role));
            }
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error getting users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Get a user by username
     * 
     * @param username The username of the user
     * @return The user
     */
    public synchronized User getUser(String username) {
        User user = null;
        String getUserQuery = "SELECT * FROM user WHERE username = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getUserQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                Role role = Role.valueOf(resultSet.getString("role"));
                user = new User(id, username, role);
            }
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error getting user: " + e.getMessage());
        }
        return user;
    }

     /**
     * Get a user by ID
     * 
     * @param id The ID of the user
     * @return The user
     */
    public synchronized User getUser(UUID id) {
        User user = null;
        String stringID = id.toString();
        String getUserQuery = "SELECT * FROM user where id = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getUserQuery)) {
            preparedStatement.setString(1, stringID);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                Role role = Role.valueOf(resultSet.getString("role"));
                user = new User(id, username, role);
            }
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error getting user: " + e.getMessage());
        }
        return user;

    }

    /**
     * Update the role of a user
     * 
     * @param username The username of the user
     * @param role     The new role of the user
     * @throws SQLException If there is an error updating the user role
     */
    public synchronized void updateUserRole(String username, Role role) throws SQLException {
        User user = getUser(username);
        if (user == null) {
            throw new SQLException("User not found");
        }
        String updateUserRoleQuery = "UPDATE user SET role = ? WHERE username = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(updateUserRoleQuery)) {
            preparedStatement.setString(1, role.toString());
            preparedStatement.setString(2, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            // CWE-778: Insufficient Logging
            logger.info("User " + sanitizeForLog(username) + " role updated to " + role.toString());
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error updating user role: " + e.getMessage());
        }
    }

    /**
     * Delete a user
     * 
     * @param username The username of the user
     * @throws SQLException If there is an error deleting the user
     */
    public synchronized void deleteUser(String username) throws SQLException {
        User user = getUser(username);
        if (user == null) {
            throw new SQLException("User not found");
        }
        String deleteUserQuery = "DELETE FROM user WHERE username = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(deleteUserQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            // CWE-778: Insufficient Logging
            logger.info("User " + sanitizeForLog(username) + " deleted");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error updating user role: " + e.getMessage());
        }
    }

    /**
     * Clean up the database connection and the handlers for the user handler
     */
    @Override
    public void cleanUp() {
        super.cleanUp();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
