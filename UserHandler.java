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
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * User handler to manage the users in the application
 */
public class UserHandler extends DatabaseHandler {

    /**
     * Constructor for the UserHandler class
     * 
     * @param databaseName The name of the database
     */
    public UserHandler(String databaseName) {
        super(databaseName);
    }

    /**
     * Create the table if it doesn't exist
     */
    @Override
    public void createTable() {
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate(
                    "create table if not exists user (id string, username string, passwordHash string, role string)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the logger for the user handler
     * 
     * @param logger The logger to initialize
     */
    @Override
    public void initLogger(Logger logger) {
        try {
            logger = Logger.getLogger("UserLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            FileHandler fileHandler = new FileHandler("./logs/UserLogger.log");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a new user
     * 
     * @param username The username of the user
     * @param password The password of the user
     * @return The new user
     */
    public User register(String username, String password) {
        User user = null;
        try {
            String checkUsernameQuery = "SELECT * FROM user WHERE username = ?";
            PreparedStatement checkUsernamePreparedStatement = this.connection.prepareStatement(checkUsernameQuery);
            checkUsernamePreparedStatement.setString(1, username);
            checkUsernamePreparedStatement.setQueryTimeout(30);
            ResultSet checkUsernameResultSet = checkUsernamePreparedStatement.executeQuery();
            if (checkUsernameResultSet.next()) {
                System.err.println("Username already exists, please try again.");
                return null;
            }
            String query = "INSERT INTO user (id, username, passwordHash, role) VALUES (?, ?, ?, ?)";
            UUID id = UUID.randomUUID();
            String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            Role userRole = Role.MEMBER; // user level role
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, passwordHash);
            preparedStatement.setString(4, userRole.toString());
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            user = new User(id, username, passwordHash, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
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
    public User login(String username, String password) {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String passwordHash = resultSet.getString("passwordHash");
                System.out.println(passwordHash); // debug print statement
                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), passwordHash);
                if (result.verified) {
                    UUID id = UUID.fromString(resultSet.getString("id"));
                    Role role = Role.valueOf(resultSet.getString("role"));
                    user = new User(id, username, passwordHash, role);
                } else {
                    System.out.println("Got row but invalid hash.");
                }
            }
        } catch (SQLTimeoutException e1) {
            System.out.println("Invalid login.");
        } catch (SQLException e2) {
            e2.printStackTrace();
        }
        // instantiated user: logged in! null, failed
        return user;
    }

    /**
     * Get all the users
     * 
     * @return All the users
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        try {
            String query = "SELECT * FROM user";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String username = resultSet.getString("username");
                String passwordHash = resultSet.getString("passwordHash");
                Role role = Role.valueOf(resultSet.getString("role"));
                users.add(new User(id, username, passwordHash, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get a user by username
     * 
     * @param username The username of the user
     * @return The user
     */
    public User getUser(String username) {
        User user = null;
        try {
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String passwordHash = resultSet.getString("passwordHash");
                Role role = Role.valueOf(resultSet.getString("role"));
                user = new User(id, username, passwordHash, role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    public void updateUserRole(String username, Role role) throws SQLException {
        User user = getUser(username);
        if (user == null) {
            throw new SQLException("User not found");
        }
        try {
            String query = "UPDATE user SET role = ? WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, role.toString());
            preparedStatement.setString(2, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a user
     * 
     * @param username The username of the user
     * @throws SQLException If there is an error deleting the user
     */
    public void deleteUser(String username) throws SQLException {
        User user = getUser(username);
        if (user == null) {
            throw new SQLException("User not found");
        }
        try {
            String query = "DELETE FROM user WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
