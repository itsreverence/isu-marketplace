import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class UserHandler extends DatabaseHandler {

    public UserHandler(String databaseName) {
        super(databaseName);
    }

    @Override
    public void createTable() {
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("if not exists (create table user (id string, username string, passwordHash string))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User register(String username, String password) {
        User user = null;
        try {
            String query = "INSERT INTO user (id, username, passwordHash) VALUES (?, ?, ?)";
            UUID id = UUID.randomUUID();
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, passwordHash);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            user = new User(id, username, passwordHash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

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
                if (BCrypt.checkpw(password, passwordHash)) {
                    UUID id = UUID.fromString(resultSet.getString("id"));
                    user = new User(id, username, passwordHash);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
