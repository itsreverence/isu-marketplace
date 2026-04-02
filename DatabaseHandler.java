import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseHandler {
    protected Connection connection;
    private String databaseName;

    public DatabaseHandler(String databaseName) {
        this.databaseName = databaseName;
        try {
            this.connection = DriverManager.getConnection("jbdc:sqlite:" + this.databaseName + ".db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void createTable();
    
}
