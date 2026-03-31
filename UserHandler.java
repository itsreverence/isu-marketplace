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
            statement.executeUpdate("drop table if exists users");
            statement.executeUpdate("create table user (id string, username string, passwordHash string)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void seedTable() {
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("insert into user values (" + UUID.randomUUID() + ", 'user1', '" + BCrypt.hashpw("password", BCrypt.gensalt()) + "')");
            statement.executeUpdate("insert into user values (" + UUID.randomUUID() + ", 'user2', '" + BCrypt.hashpw("password", BCrypt.gensalt()) + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User register(String username, String password) {
        User user = null;
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            UUID id = UUID.randomUUID();
            statement.executeUpdate("insert into user values (" + id.toString() + ", '" + username + "', '" + passwordHash + "')");
            user = new User(id, username, passwordHash);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User login(String username, String password) {
        User user = null;
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery("select * from user where username = '" + username + "'");
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
