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
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Listing handler to manage the listings in the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 * CWE-366: All methods that access the shared database connection are synchronized to prevent
 * concurrent threads from reading/modifying data in an inconsistent state.
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
        // CWE-459: Incomplete Cleanup
        try (Statement statement = this.connection.createStatement()) {
            statement.setQueryTimeout(30);
            statement.executeUpdate(
                    "create table if not exists listing (id string, userId string, title string, description string, price float)");
            // CWE-778: Insufficient Logging
            logger.info("Listing table created");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Initialize the logger for the listing handler
     * 
     * @return the logger for the listing handler
     */
    @Override
    public Logger initLogger() {
        try {
            Logger logger = Logger.getLogger("ListingLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            // CWE-779: Logging of Excessive Data
            FileHandler fileHandler = new FileHandler("./logs/ListingLogger.log", 1000000, 1, true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            // CWE-778: Insufficient Logging
            logger.info("Listing logger initialized");
            return logger;
        } catch (IOException e) {
            // CWE-778: Insufficient Logging
            System.err.println("Error initializing logger: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get the listings for a user
     * 
     * @param user The user to get the listings for
     * @return The listings for the user
     */
    public synchronized List<Listing> getUserListings(User user) {
        List<Listing> listings = new ArrayList<>();
        UUID userId = user.getId();
        String getUserListingsQuery = "SELECT * FROM listing WHERE userId = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getUserListingsQuery)) {
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
            // CWE-778: Insufficient Logging
            logger.severe("Error getting user listings: " + e.getMessage());
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
    public synchronized Listing createListing(User user, String title, String description, float price) {
        Listing listing = null;
        String createListingQuery = "INSERT INTO listing (id, userId, title, description, price) VALUES (?, ?, ?, ?, ?)";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(createListingQuery)) {
            UUID id = UUID.randomUUID();
            preparedStatement.setString(1, id.toString());
            preparedStatement.setString(2, user.getId().toString());
            preparedStatement.setString(3, title);
            preparedStatement.setString(4, description);
            preparedStatement.setFloat(5, price);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            listing = new Listing(id, user.getId(), title, description, price);
            // CWE-778: Insufficient Logging
            logger.info("Listing " + id + " created");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error creating listing: " + e.getMessage());
        }
        return listing;
    }

    /**
     * Get all the listings
     * 
     * @return All the listings
     */
    public synchronized List<Listing> getListings() {
        List<Listing> listings = new ArrayList<>();
        String getListingsQuery = "SELECT * FROM listing";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getListingsQuery)) {
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
            // CWE-778: Insufficient Logging
            logger.severe("Error getting listings: " + e.getMessage());
        }
        return listings;
    }

    /**
     * Remove a listing
     * 
     * @param listing The listing to remove
     */
    public synchronized void removeListing(Listing listing) {
        UUID listingId = listing.getId();
        String deleteListingQuery = "DELETE FROM listing WHERE id = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(deleteListingQuery)) {
            preparedStatement.setString(1, listingId.toString());
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            // CWE-778: Insufficient Logging
            logger.info("Listing " + listingId + " removed");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error removing listing: " + e.getMessage());
        }
    }

    /**
     * Get a listing by ID
     * 
     * @param listingId The ID of the listing to get
     * @return The listing
     */
    public synchronized Listing getListing(UUID listingId) {
        Listing listing = null;
        String getListingQuery = "SELECT * FROM listing WHERE id = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(getListingQuery)) {
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
            // CWE-778: Insufficient Logging
            logger.severe("Error getting listing: " + e.getMessage());
        }
        return listing;
    }

    /**
     * Delete all the listings for a user
     * 
     * @param username The username of the user to delete the listings for
     * @throws SQLException If there is an error deleting the listings
     */
    public synchronized void deleteUserListings(String username) throws SQLException {
        String deleteUserListingsQuery = "DELETE FROM listing WHERE username = ?";
        // CWE-459: Incomplete Cleanup
        try (PreparedStatement preparedStatement = this.connection.prepareStatement(deleteUserListingsQuery)) {
            preparedStatement.setString(1, username);
            preparedStatement.setQueryTimeout(30);
            preparedStatement.executeUpdate();
            // CWE-778: Insufficient Logging
            logger.info("User " + username + " listings deleted");
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error deleting user listings: " + e.getMessage());
        }
    }

    /**
    * Search for listings by title
    * 
    * @param listingTitle the name of the listing 
    * @throws IllegalArgumentException if the listingTitle is not correct format
    */
    public synchronized List<Listing> searchListingsByTitle(String listingTitle) throws SQLException {
        if (listingTitle == null || listingTitle.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String searchListingsByTitleString = "SELECT * FROM listing WHERE title = ?";
        PreparedStatement searchListingsByTitlePS = this.connection.prepareStatement(searchListingsByTitleString);

        try {
            searchListingsByTitlePS.setString(1, listingTitle);
            List<Listing> listings = new ArrayList<>();
            ResultSet resultSet = searchListingsByTitlePS.executeQuery();
            while (resultSet.next())
            {
                Listing currentListing = new Listing(
                    UUID.fromString(resultSet.getString(1)),
                    UUID.fromString(resultSet.getString(2)),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getFloat(5));
                listings.add(currentListing);
            }

            return listings;
        } finally {
            try {
                searchListingsByTitlePS.close();
            } catch (SQLException e) {
                System.out.println("SQLException caught.");
            }
        }
    }

    /**
     * Clean up the database connection and the handlers for the listing handler
     */
    @Override
    public void cleanUp() {
        super.cleanUp();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
