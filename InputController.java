import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.bytes.Bytes;

/**
 * Controller class to handle the input and output of the application
 */
public class InputController {
    private Logger logger; // logger for the input controller
    private static final String INPUT_PROMPT = "Enter your choice: "; // prompt for the user to enter their choice
    private static final String USERNAME_PROMPT = "Enter your username: "; // prompt for the user to enter their
                                                                           // username
    private static final String PASSWORD_PROMPT = "Enter your password: "; // prompt for the user to enter their
                                                                           // password

    private static final String LISTING_TITLE_PROMPT = "Enter the title of the listing: "; // prompt for the user to
                                                                                           // enter the title of the
                                                                                           // listing
    private static final String LISTING_DESC_PROMPT = "Enter the description of the listing: "; // prompt for the user
                                                                                                // to enter the
                                                                                                // description of the
                                                                                                // listing
    private static final String LISTING_PRICE_PROMPT = "Enter the price of the listing: "; // prompt for the user to
                                                                                           // enter the price of the
                                                                                           // listing

    private static final String BUY_LISTING_PROMPT = "Enter the ID of the listing you want to buy: "; // prompt for the
                                                                                                      // user to enter
                                                                                                      // the ID of the
                                                                                                      // listing they
                                                                                                      // want to buy
    private static final String REMOVE_LISTING_PROMPT = "Enter the ID of the listing you want to remove: "; // prompt
                                                                                                            // for the
                                                                                                            // user to
                                                                                                            // enter the
                                                                                                            // ID of the
                                                                                                            // listing
                                                                                                            // they want
                                                                                                            // to remove

    private static final String UPDATE_USER_ROLE_USERNAME_PROMPT = "Enter the username of the user: "; // prompt for the
                                                                                                       // user to enter
                                                                                                       // the username
                                                                                                       // of the user
                                                                                                       // they want to
                                                                                                       // update the
                                                                                                       // role of
    private static final String UPDATE_USER_ROLE_PROMPT = "Enter the role of the user: "; // prompt for the user to
                                                                                          // enter the role of the user
                                                                                          // they want to update

    private static final String DELETE_USER_USERNAME_PROMPT = "Enter the username of the user: "; // prompt for the user
                                                                                                  // to enter the
                                                                                                  // username of the
                                                                                                  // user they want to
                                                                                                  // delete

    private static final String INVALID_PROMPT = "Invalid operation, please try again."; // prompt for the user to enter
                                                                                         // an invalid operation


    
    public InputController() {
        this.logger = initLogger();
    }

    /**
     * Prompts the user to login or register
     * 
     * @param userHandler the user handler to use
     * @return the user object
     */
    public User loginOrRegister(UserHandler userHandler) {
        User user = null;
        System.out.println("1.) Login");
        System.out.println("2.) Register");
        System.out.println("3.) Quit");
        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 3);

        switch (choice) {
            case 1:
                user = login(userHandler);
                break;
            case 2:
                user = register(userHandler);
                break;
            case 3:
                // i hope this is acceptable
                System.exit(0);
        }
        // no default needed as readInt ensures we are in the range
        return user;
    }

    /**
     * Prompts the user to login
     * 
     * @param userHandler the user handler to use
     * @return the user object
     */
    private User login(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_PROMPT);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PROMPT);
            user = userHandler.login(username, password);
            // if .login returns null, login failed.
            if (user == null) {
                // encapsulate login operation via the same prompt
                // CWE-778: Insufficient Logging
                logger.info("Login failed for user " + username);
                System.out.println(INVALID_PROMPT);
            }
        }
        // logged in
        System.out.println("Welcome back, " + user.getUsername() + "!");

        // CWE-778: Insufficient Logging
        logger.info("User " + user.getUsername() + " logged in");
        return user;
    }

    /**
     * Prompts the user to register
     * 
     * @param userHandler the user handler to use
     * @return the user object
     */
    private User register(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_PROMPT);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PROMPT);
            user = userHandler.register(username, password);
            // TODO: we should also handle the case where user is null (if .register()
            // fails)
        }
        System.out.println("Welcome, " + user.getUsername() + "!");

        // CWE-778: Insufficient Logging
        logger.info("User " + user.getUsername() + " registered");
        return user;
    }

    /**
     * Prompts the user to select an option from the main menu
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     * @param userHandler    the user handler to use
     * @return true if the user wants to continue running the application, false
     *         otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public boolean mainMenu(User user, ListingHandler listingHandler, UserHandler userHandler) {
        try {
            if (user.getRole() == Role.ADMIN) {
                return adminMenu(user, listingHandler, userHandler);
            } else {
                System.out.println("1.) Your Listings");
                System.out.println("2.) New Listing");
                System.out.println("3.) Remove Listing");
                System.out.println("4.) Browse Listings");
                System.out.println("5.) Buy Listing");
                System.out.println("6.) Help");
                System.out.println("7.) Exit");
    
                int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 7);
                boolean returnValue = false;
    
                switch (choice) {
                    case 1:
                        userListings(user, listingHandler);
                        returnValue = true;
                        break;
                    case 2:
                        newListing(user, listingHandler);
                        returnValue = true;
                        break;
                    case 3:
                        removeListing(user, listingHandler);
                        returnValue = true;
                        break;
                    case 4:
                        browseListings(listingHandler);
                        returnValue = true;
                        break;
                    case 5:
                        buyListing(user, listingHandler);
                        returnValue = true;
                        break;
                    case 6:
                        help("mainMenu");
                        returnValue = true;
                        break;
                    case 7:
                        // again, i hope this is okay
                        System.exit(0);
                }
                return returnValue;
            }
        } catch (SQLException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error in main menu: " + e.getMessage());
        }
        return false;
    }

    /**
     * Prompts the user to select an option from the admin menu
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     * @param userHandler    the user handler to use
     * @return true if the user wants to continue running the application, false
     *         otherwise
     * @throws SQLException if there is an error accessing the database
     */
    public boolean adminMenu(User user, ListingHandler listingHandler, UserHandler userHandler) throws SQLException {
        System.out.println("1.) Your Listings");
        System.out.println("2.) New Listing");
        System.out.println("3.) Remove Listing");
        System.out.println("4.) Browse Listings");
        System.out.println("5.) Buy Listing");
        System.out.println("6.) Manage Users");
        System.out.println("7.) Manage Listings");
        System.out.println("8.) Help");
        System.out.println("9.) Exit");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 9);
        boolean returnValue = false;

        switch (choice) {
            case 1:
                userListings(user, listingHandler);
                returnValue = true;
                break;
            case 2:
                newListing(user, listingHandler);
                returnValue = true;
                break;
            case 3:
                removeListing(user, listingHandler);
                returnValue = true;
                break;
            case 4:
                browseListings(listingHandler);
                returnValue = true;
                break;
            case 5:
                buyListing(user, listingHandler);
                returnValue = true;
                break;
            case 6:
                manageUsers(userHandler, listingHandler);
                returnValue = true;
                break;
            case 7:
                manageListings(listingHandler);
                returnValue = true;
                break;
            case 8:
                help("adminMenu");
                returnValue = true;
                break;
            case 9:
                // again, i hope this is okay
                System.exit(0);
        }
        return returnValue;
    }

    /**
     * Prompts the user to select an option from the manage users menu
     * 
     * @param userHandler    the user handler to use
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void manageUsers(UserHandler userHandler, ListingHandler listingHandler) throws SQLException {
        System.out.println("1.) View Users");
        System.out.println("2.) Update User Role");
        System.out.println("3.) Delete User");
        System.out.println("4.) Help");
        System.out.println("5.) Exit");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 5);

        switch (choice) {
            case 1:
                viewUsers(userHandler);
                break;
            case 2:
                updateUserRole(userHandler);
                break;
            case 3:
                deleteUser(userHandler, listingHandler);
                break;
            case 4:
                help("manageUsers");
                manageUsers(userHandler, listingHandler);
                break;
            case 5:
                // again, i hope this is okay
                System.exit(0);
        }
    }

    /**
     * Prompts the user to select an option from the manage listings menu
     * 
     * @param userHandler    the user handler to use
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void manageListings(ListingHandler listingHandler) throws SQLException {
        System.out.println("1.) Delete Listing");
        System.out.println("2.) Help");
        System.out.println("3.) Exit");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 3);
        switch (choice) {
            case 1:
                deleteListing(listingHandler);
                break;
            case 2:
                help("manageListings");
                manageListings(listingHandler);
                break;
            case 3:
                // again, i hope this is okay
                System.exit(0);
        }
    }

    /**
     * Displays the information of all the users of the application
     * 
     * @param userHandler the user handler to use
     */
    private void viewUsers(UserHandler userHandler) {
        List<User> users = userHandler.getUsers();
        for (User user : users) {
            System.out.println("ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Role: " + user.getRole());
            System.out.println("");
        }
    }

    /**
     * Prompts the user to delete a listing
     * 
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteListing(ListingHandler listingHandler) throws SQLException {
        String listingId = InputValidation.readString(REMOVE_LISTING_PROMPT, INVALID_PROMPT);
        Listing listing = listingHandler.getListing(UUID.fromString(listingId));
        if (listing == null) {
            System.out.println("The specified listing does not exist.");
            return;
        }
        listingHandler.removeListing(listing);
        System.out.println("The specified listing has been deleted.");

        // CWE-778: Insufficient Logging
        logger.info("Listing " + listing.getId() + " deleted");
    }

    /**
     * Prompts the user to update the role of a user
     * 
     * @param userHandler the user handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void updateUserRole(UserHandler userHandler) throws SQLException {
        String username = InputValidation.readString(UPDATE_USER_ROLE_USERNAME_PROMPT, INVALID_PROMPT);
        System.out.println("1.) Member");
        System.out.println("2.) Admin");
        int choice = InputValidation.readInt(UPDATE_USER_ROLE_PROMPT, INVALID_PROMPT, 2);
        switch (choice) {
            case 1:
                userHandler.updateUserRole(username, Role.MEMBER);
                // CWE-778: Insufficient Logging
                logger.info(username + " was updated to member");
                break;
            case 2:
                userHandler.updateUserRole(username, Role.ADMIN);
                // CWE-778: Insufficient Logging
                logger.info(username + " was updated to admin");
                break;
        }
    }

    /**
     * Prompts the user to delete a user
     * 
     * @param userHandler    the user handler to use
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteUser(UserHandler userHandler, ListingHandler listingHandler) throws SQLException {
        String username = InputValidation.readString(DELETE_USER_USERNAME_PROMPT, INVALID_PROMPT);
        userHandler.deleteUser(username);
        listingHandler.deleteUserListings(username);

        System.out.println("The specified user " + username + " has been deleted.");

        // CWE-778: Insufficient Logging
        logger.info(username + " deleted");
    }

    /**
     * Displays the listings of a user
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     */
    private void userListings(User user, ListingHandler listingHandler) {
        List<Listing> listings = listingHandler.getUserListings(user);
        for (Listing listing : listings) {
            System.out.println("Title: " + listing.getTitle());
            System.out.println("Description: " + listing.getDescription());
            System.out.println("Price: " + listing.getPrice());
            System.out.println("");
        }
    }

    /**
     * Prompts the user to create a new listing
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     */
    private void newListing(User user, ListingHandler listingHandler) {
        String title = InputValidation.readString(LISTING_TITLE_PROMPT, INVALID_PROMPT);
        String description = InputValidation.readString(LISTING_DESC_PROMPT, INVALID_PROMPT);
        float price = InputValidation.readFloat(LISTING_PRICE_PROMPT, INVALID_PROMPT);

        // create listing with validated input
        Listing listing = listingHandler.createListing(user, title, description, price);
        System.out.println("Your listing has been published to the marketplace.");

        // CWE-778: Insufficient Logging
        logger.info(user.getUsername() + " created listing " + listing.getId());
    }

    /**
     * Displays the listings of the application
     * 
     * @param listingHandler the listing handler to use
     */
    private void browseListings(ListingHandler listingHandler) {
        List<Listing> listings = listingHandler.getListings();
        for (Listing listing : listings) {
            System.out.println("ID: " + listing.getId());
            System.out.println("Title: " + listing.getTitle());
            System.out.println("Description: " + listing.getDescription());
            System.out.println("Price: " + listing.getPrice());
            System.out.println("");
        }
    }

    /**
     * Prompts the user to buy a listing
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void buyListing(User user, ListingHandler listingHandler) {
        String listingId = InputValidation.readString(BUY_LISTING_PROMPT, INVALID_PROMPT);
        Listing listing = listingHandler.getListing(UUID.fromString(listingId));

        // Ensure listing exists
        if (listing == null) {
            System.out.println("Invalid listing.");
            return;
        }
        // Ensure we are not buying our own
        if (listing.getUserId().equals(user.getId())) {
            System.out.println("You cannot buy your own listing.");
            return;
        }
        // Finally, allow the user to purchase the listing
        listingHandler.removeListing(listing);
        System.out.println("The specified listing has been purchased.");

        // CWE-778: Insufficient Logging
        logger.info(user.getUsername() + " purchased listing " + listing.getId());
    }

    /**
     * Prompts the user to remove a listing
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void removeListing(User user, ListingHandler listingHandler) {
        String listingId = InputValidation.readString(REMOVE_LISTING_PROMPT, INVALID_PROMPT);
        Listing listing = listingHandler.getListing(UUID.fromString(listingId));

        // Ensure listing exists
        if (listing == null) {
            System.out.println("Invalid listing.");
            return;
        }
        // Ensure we are deleting our own
        if (!listing.getUserId().equals(user.getId())) {
            // TODO: Consider a statement for not revealing the listing id exists
            System.out.println("Invalid listing.");
            return;
        }
        listingHandler.removeListing(listing);
        System.out.println("Your specified listing has been removed.");

        // CWE-778: Insufficient Logging
        logger.info(user.getUsername() + " removed listing " + listing.getId());
    }

    /**
     * Displays the help file for the application
     * 
     * @param fileName the name of the help file to display
     */
    private void help(String fileName) {
        try {
            String filePath = "docs/" + fileName + ".txt";
            File file = new File(filePath);
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                System.out.println(line);
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            // CWE-778: Insufficient Logging
            logger.severe("Error displaying help file: " + e.getMessage());
        }
    }

    /**
     * Initializes the logger for the input controller
     * 
     * @return the logger for the input controller
     */
    private Logger initLogger() {
        try {
            Logger logger = Logger.getLogger("InputLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            // CWE-779: Logging of Excessive Data
            FileHandler fileHandler = new FileHandler("./logs/InputLogger.log", 1000000, 1, true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            return logger;
        } catch (IOException e) {
            // CWE-778: Insufficient Logging
            System.err.println("Error initializing logger: " + e.getMessage());
        }
        return null;
    }
}
