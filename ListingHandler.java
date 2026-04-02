import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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

    public List<Listing> getUserListings(User user) {
        List<Listing> listings = new ArrayList<>();
        UUID userId = user.getId();
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            ResultSet resultSet = statement.executeQuery("select * from listing where userId = '" + userId.toString() + "'");
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                UUID listingUserId = UUID.fromString(resultSet.getString("userId"));
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                float price = resultSet.getFloat("price");
                listings.add(new Listing(id, listingUserId, title, description, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listings;
    }

    public Listing createListing(User user, String title, String description, float price) {
        Listing listing = null;
        try {
            Statement statement = this.connection.createStatement();
            statement.setQueryTimeout(30);
            UUID id = UUID.randomUUID();
            UUID userId = user.getId();
            statement.executeUpdate("insert into listing values (" + id.toString() + ", '" + userId.toString() + "', '" + title + "', '" + description + "', " + price + ")");
            listing = new Listing(id, userId, title, description, price);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listing;
    }
}
