import java.sql.SQLException;
import java.sql.Statement;


public class ListingHandler extends DatabaseHandler {
    
    public ListingHandler(String databaseName) {
        super(databaseName);
    }

    @Override
    public void createTable() {
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            statement.executeUpdate("drop table if exists listing");
            statement.executeUpdate("create table listing (id string, userId string, title string, description string, price float)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
