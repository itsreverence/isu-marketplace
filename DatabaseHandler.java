import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class DatabaseHandler {
    protected Connection connection;
    private String databaseName;

    public DatabaseHandler(String databaseName) {
        this.databaseName = databaseName;
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databaseName + ".db");
            dropTables();
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public abstract void createTable();
    
}
