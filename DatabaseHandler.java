import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Abstract database handler to manage our different databases
 */
public abstract class DatabaseHandler {
    protected Connection connection; // connection to the database
    private String databaseName; // name of the database
    private Logger logger; // logger for the database

    /**
     * Constructor which drops tables if set in the
     * config, creates the table if it doesn't exist, and initializes the logger
     * 
     * @param databaseName the name of the database
     */
    public DatabaseHandler(String databaseName) {
        this.databaseName = databaseName;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseName + ".db");
            dropTables();
            createTable();
            initLogger(this.logger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Drops the tables if set in the JSON config
     */
    private void dropTables() {
        boolean dropTables = false;
        try {
            Object object = new JSONParser().parse(new FileReader("config.json"));
            JSONObject jsonObject = (JSONObject) object;
            dropTables = (boolean) jsonObject.get("dropTables");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dropTables) {
            try {
                String query = "DROP TABLE IF EXISTS " + this.databaseName;
                PreparedStatement preparedStatement = this.connection.prepareStatement(query);
                preparedStatement.setQueryTimeout(30);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create the table if it doesn't exist
     */
    public abstract void createTable();

    /**
     * Initialize the logger for the database
     * 
     * @param logger the logger to initialize
     */
    public abstract void initLogger(Logger logger);

}
