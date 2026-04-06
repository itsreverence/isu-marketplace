import java.sql.PreparedStatement;
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
            statement.executeUpdate("if not exists (create table listing (id string, userId string, title string, description string, price float))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Listing> getUserListings(User user) {
        List<Listing> listings = new ArrayList<>();
        UUID userId = user.getId();
        try {
            String query = "SELECT * FROM listing WHERE userId = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, userId.toString());
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                UUID id = UUID.fromString(resultSet.getString("id"));
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                float price = resultSet.getFloat("price");
                listings.add(new Listing(id, userId, title, description, price));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listings;
    }

    public Listing createListing(User user, String title, String description, float price) {
        Listing listing = null;
        try {
            String query = "INSERT INTO listing (id, userId, title, description, price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            UUID id = UUID.randomUUID();
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, user.getId().toString());
            preparedStatement.setString(3, title);
            preparedStatement.setString(4, description);
            preparedStatement.setFloat(5, price);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            listing = new Listing(id, user.getId(), title, description, price);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listing;
    }

    public List<Listing> getListings() {
        List<Listing> listings = new ArrayList<>();
        try {
            String query = "SELECT * FROM listing";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
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

    public void buyListing(Listing listing) {
        UUID listingId = listing.getId();
        try {
            String query = "DELETE FROM listing WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, listingId.toString());
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Listing getListing(UUID listingId) {
        Listing listing = null;
        try {
            String query = "SELECT * FROM listing WHERE id = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, listingId.toString());
            preparedStatement.setQueryTimeout(30);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID userId = UUID.fromString(resultSet.getString("userId"));
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                float price = resultSet.getFloat("price");
                listing = new Listing(listingId, userId, title, description, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listing;
    }
}
