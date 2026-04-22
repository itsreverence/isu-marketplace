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
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 * CWE-1062: Parent Class with References to Child Class - Compliant
 */
public abstract class DatabaseHandler {
    protected Connection connection; // connection to the database
    protected Logger logger; // logger for the database
    private String databaseName; // name of the database

    /**
     * Constructor which drops tables if set in the
     * config, creates the table if it doesn't exist, and initializes the logger
     * 
     * @param databaseName the name of the database
     */
    public DatabaseHandler(String databaseName) {
        this.databaseName = databaseName;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:db/" + this.databaseName + ".db");
            this.logger = initLogger();
            dropTables();
            createTable();
            // CWE-778: Insufficient Logging
            logger.info(this.databaseName + " database initialized");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Drops the tables if set in the JSON config
     */
    private void dropTables() {
        boolean dropTables = false;
        // CWE-459: Incomplete Cleanup
        try (FileReader fileReader = new FileReader("config.json")) {
            Object object = new JSONParser().parse(fileReader);
            JSONObject jsonObject = (JSONObject) object;
            dropTables = (boolean) jsonObject.get("dropTables");
        } catch (IOException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error reading config: " + e.getMessage());
        } catch (ParseException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error parsing config: " + e.getMessage());
        }

        if (dropTables) {
            String query = "DROP TABLE IF EXISTS " + this.databaseName;
            // CWE-459: Incomplete Cleanup
            try (PreparedStatement preparedStatement = this.connection.prepareStatement(query)) {
                preparedStatement.setQueryTimeout(30);
                preparedStatement.executeUpdate();
                // CWE-778: Insufficient Logging
                logger.info(this.databaseName + " tables dropped");
            } catch (SQLException e) {
                // CWE-778: Insufficient Logging
                logger.severe("Error dropping tables: " + e.getMessage());
            }
        }
    }

    /**
     * Clean up the database connection
     */
    public void cleanUp() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Create the table if it doesn't exist
     */
    public abstract void createTable();

    /**
     * Initialize the logger for the database
     */
    public abstract Logger initLogger();

}
