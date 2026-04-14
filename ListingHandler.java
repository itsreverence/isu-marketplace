import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Listing handler to manage the listings in the application
 */
public class ListingHandler extends DatabaseHandler {

    /**
     * Constructor for the ListingHandler class
     * 
     * @param databaseName The name of the database
     */
    public ListingHandler(String databaseName) {
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
                    "create table if not exists listing (id string, userId string, title string, description string, price float)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the logger for the listing handler
     * 
     * @param logger The logger to initialize
     */
    @Override
    public void initLogger(Logger logger) {
        try {
            logger = Logger.getLogger("ListingLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            FileHandler fileHandler = new FileHandler("./logs/ListingLogger.log");
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the listings for a user
     * 
     * @param user The user to get the listings for
     * @return The listings for the user
     */
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

    /**
     * Create a new listing
     * 
     * @param user        The user to create the listing for
     * @param title       The title of the listing
     * @param description The description of the listing
     * @param price       The price of the listing
     * @return The new listing
     */
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

    /**
     * Get all the listings
     * 
     * @return All the listings
     */
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

    /**
     * Remove a listing
     * 
     * @param listing The listing to remove
     */
    public void removeListing(Listing listing) {
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

    /**
     * Get a listing by ID
     * 
     * @param listingId The ID of the listing to get
     * @return The listing
     */
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

    /**
     * Delete all the listings for a user
     * 
     * @param username The username of the user to delete the listings for
     * @throws SQLException If there is an error deleting the listings
     */
    public void deleteUserListings(String username) throws SQLException {
        try {
            String query = "DELETE FROM listing WHERE username = ?";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
