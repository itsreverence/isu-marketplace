import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Controller class to handle the input and output of the application
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
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

    //private static final String INVALID_PROMPT = "Invalid operation, please try again."; // prompt for the user to enter
                                                                                         // an invalid operation
    private static final String MENU_LINES = "--------------------"; // lines to separate menu content

    private static final String INVALID_LOGIN = "Login failed, please try again."; // prompt for when user login fails

    private static final String INVALID_MENU_CHOICE = "Invalid choice, please try again."; // prompt for when user enters invalid menu choice integer

    private static final String INVALID_USERNAME = "Username cannot be empty, please try again."; // prompt for when user enters empty username
    
    private static final String INVALID_PASSWORD = "Password cannot be empty, please try again."; // prompt for when user enters empty password

    private static final String INVALID_LISTING_ID = "Listing ID cannot be empty, please try again."; // prompt for when user enters empty listing ID

    private static final String INVALID_LISTING_TITLE = "Listing title cannot be empty, please try again."; // prompt for when user enters empty listing title

    private static final String INVALID_LISTING_DESCRIPTION = "Listing description cannot be empty, please try again."; // prompt for when user enters empty listing description

    private static final String INVALID_LISTING_PRICE = "Listing price cannot be negative, please try again."; // prompt for when user enters invalid listing price

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
        System.out.println("\nWelcome to the ISU Marketplace!");
        System.out.println(MENU_LINES);
        System.out.println("1.) Login");
        System.out.println("2.) Register");
        System.out.println("3.) Quit");
        System.out.println(MENU_LINES + "\n");
        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 3);

        switch (choice) {
            case 1:
                user = login(userHandler);
                break;
            case 2:
                user = register(userHandler);
                break;
            case 3:
                return null;
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
        // CWE-1095: Loop Condition Value Update within the Loop
        while (true) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_USERNAME);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PASSWORD);
            User user = userHandler.login(username, password);
            // CWE-307 Need to check if the user has reached the login attempts.
            if (userHandler.isLockedOut()) {
                System.out.println("Too many failed login attempts. Exiting.");
                System.exit(0);
            }
            // if .login returns null, login failed.
            if (user == null) {
                // encapsulate login operation via the same prompt
                System.out.println(INVALID_LOGIN);
            } else {
                // logged in
                System.out.println("\nWelcome back, " + user.getUsername() + "!");

                // CWE-778: Insufficient Logging
                logger.info("User " + user.getUsername() + " logged in");
                return user;
            }
        }
    }

    /**
     * Prompts the user to register
     * 
     * @param userHandler the user handler to use
     * @return the user object
     */
    private User register(UserHandler userHandler) {
        // CWE-1095: Loop Condition Value Update within the Loop
        while (true) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_USERNAME);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PASSWORD);
            User user = userHandler.register(username, password, false);
            if (user != null) {
                System.out.println("\nWelcome, " + user.getUsername() + "!");
                // CWE-778: Insufficient Logging
                logger.info("User " + user.getUsername() + " registered");
                return user;
            }
        }
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
                System.out.println("\n" + MENU_LINES);
                System.out.println("1.) Your Listings");
                System.out.println("2.) New Listing");
                System.out.println("3.) Remove Listing");
                System.out.println("4.) Browse Listings");
                System.out.println("5.) Buy Listing");
                System.out.println("6.) Help");
                System.out.println("7.) Exit");
                System.out.println(MENU_LINES + "\n");
    
                int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 7);
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
                        handleBrowseMenu(listingHandler);
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
                        return false;
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
        requireAdmin(user); //CWE-306
        System.out.println("\n" + MENU_LINES);
        System.out.println("1.) Your Listings");
        System.out.println("2.) New Listing");
        System.out.println("3.) Remove Listing");
        System.out.println("4.) Browse Listings");
        System.out.println("5.) Buy Listing");
        System.out.println("6.) Manage Users");
        System.out.println("7.) Manage Listings");
        System.out.println("8.) Help");
        System.out.println("9.) Exit");
        System.out.println(MENU_LINES + "\n");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 9);
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
                handleBrowseMenu(listingHandler);
                returnValue = true;
                break;
            case 5:
                buyListing(user, listingHandler);
                returnValue = true;
                break;
            case 6:
                manageUsers(userHandler, listingHandler, user);
                returnValue = true;
                break;
            case 7:
                manageListings(listingHandler, userHandler, user);
                returnValue = true;
                break;
            case 8:
                help("adminMenu");
                returnValue = true;
                break;
            case 9:
                return false;
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
    private void manageUsers(UserHandler userHandler, ListingHandler listingHandler, User user) throws SQLException {
        requireAdmin(user); //CWE-306
        System.out.println("\n" + MENU_LINES);
        System.out.println("1.) View Users");
        System.out.println("2.) Update User Role");
        System.out.println("3.) Delete User");
        System.out.println("4.) Help");
        System.out.println("5.) Back");
        System.out.println(MENU_LINES + "\n");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 5);

        switch (choice) {
            case 1:
                viewUsers(userHandler);
                break;
            case 2:
                updateUserRole(userHandler);
                break;
            case 3:
                deleteUser(userHandler, listingHandler, user);
                break;
            case 4:
                help("manageUsers");
                manageUsers(userHandler, listingHandler, user);
                break;
            case 5:
                return;
        }
    }

    /**
     * Prompts the user to select an option from the manage listings menu
     * 
     * @param userHandler    the user handler to use
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void manageListings(ListingHandler listingHandler, UserHandler userHandler, User user) throws SQLException {
        requireAdmin(user); //CWE-306
        System.out.println("1.) Delete Listing");
        System.out.println("2.) Help");
        System.out.println("3.) Back");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 3);
        switch (choice) {
            case 1:
                deleteListing(listingHandler, userHandler, user);
                break;
            case 2:
                help("manageListings");
                manageListings(listingHandler, userHandler, user);
                break;
            case 3:
                return;
        }
    }

    /**
     * Displays the information of all the users of the application
     * 
     * @param userHandler the user handler to use
     */
    private void viewUsers(UserHandler userHandler) {
        List<User> users = userHandler.getUsers();
        System.out.println("There are " + users.size() + " user(s) total.");
        bulkPrintList(users);
    }

    /**
     * Prompts the user to delete a listing
     * 
     * @param listingHandler the listing handler to use
     * @throws SQLException if there is an error accessing the database
     */
    private void deleteListing(ListingHandler listingHandler, UserHandler userHandler, User user) throws SQLException {
        requireAdmin(user); //CWE-306
        String listingId = InputValidation.readString(REMOVE_LISTING_PROMPT, INVALID_LISTING_ID);
        // CWE-229: Improper Handling of Values - validate UUID format before parsing
        UUID listingUUID;
        try {
            listingUUID = UUID.fromString(listingId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid listing ID format.");
            return;
        }
        Listing listing = listingHandler.getListing(listingUUID);
        User userListingToDelete = userHandler.getUser(listing.getUserId());
        // if invalid listing or if we are a member deleting an admin
        if (listing == null || (user.getRole().equals(Role.MEMBER) && userListingToDelete.getRole().equals(Role.ADMIN))) {
            System.out.println("Invalid listing.");
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
        String username = InputValidation.readString(UPDATE_USER_ROLE_USERNAME_PROMPT, INVALID_USERNAME);
        System.out.println("1.) Member");
        System.out.println("2.) Admin");
        int choice = InputValidation.readInt(UPDATE_USER_ROLE_PROMPT, INVALID_MENU_CHOICE, 2);
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
    private void deleteUser(UserHandler userHandler, ListingHandler listingHandler, User user) throws SQLException {
        requireAdmin(user); //CWE-306
        String username = InputValidation.readString(DELETE_USER_USERNAME_PROMPT, INVALID_USERNAME);
        User userToDelete = userHandler.getUser(username);
        if (userToDelete == null || userToDelete.getRole().equals(Role.ADMIN) || user.getId().equals(userToDelete.getId())) {
            System.out.println("Invalid user.");
            return;
        }
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
        System.out.println("\nYou have " + listings.size() + " listing(s).\n");
        bulkPrintList(listings);
    }

    /**
     * Prompts the user to create a new listing
     * 
     * @param user           the user object
     * @param listingHandler the listing handler to use
     */
    private void newListing(User user, ListingHandler listingHandler) {
        String title = InputValidation.readString(LISTING_TITLE_PROMPT, INVALID_LISTING_TITLE);
        String description = InputValidation.readString(LISTING_DESC_PROMPT, INVALID_LISTING_DESCRIPTION);
        float price = InputValidation.readFloat(LISTING_PRICE_PROMPT, INVALID_LISTING_PRICE);

        // create listing with validated input
        // CWE-229: Improper Handling of Values - check return value before use
        Listing listing = listingHandler.createListing(user, title, description, price);
        if (listing == null) {
            System.out.println("Failed to create listing, please try again.");
            return;
        }
        System.out.println("\nYour listing has been published to the marketplace.");

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
        System.out.println("There are " + listings.size() + "listing(s) total.");
        bulkPrintList(listings);
    }

    private void handleBrowseMenu(ListingHandler listingHandler) throws SQLException{
        System.out.println(MENU_LINES);
        System.out.println("1.) Browse all listings");
        System.out.println("2.) Search for a listing by title");
        System.out.println("3.) Help");
        System.out.println("4.) Back");
        System.out.println(MENU_LINES + "\n");
        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_MENU_CHOICE, 4);
        List<Listing> listings;
        switch (choice) {
            case 1:
                browseListings(listingHandler);
                break;
            case 2: 
                String listingTitle = InputValidation.readString(LISTING_TITLE_PROMPT, INVALID_LISTING_TITLE);
                listings = listingHandler.searchListingsByTitle(listingTitle);
                //System.out.println();
                if(listings.isEmpty()) {
                    System.out.println("No listings found with that title.");
                } else {
                    bulkPrintList(listings);
                }
                break;
            case 3:
                help("browseListings");
                handleBrowseMenu(listingHandler);
                break;
            case 4: 
                return;
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
        String listingId = InputValidation.readString(BUY_LISTING_PROMPT, INVALID_LISTING_ID);
        // CWE-229: Improper Handling of Values - validate UUID format before parsing
        UUID listingUUID;
        try {
            listingUUID = UUID.fromString(listingId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid listing ID format.");
            return;
        }
        Listing listing = listingHandler.getListing(listingUUID);

        // Ensure listing exists
        if (listing == null) {
            // CWE-779: Logging of Excessive Data
            logger.fine(user.getUsername() + " attempted to buy listing " + listingId + " which does not exist");
            System.out.println("Invalid listing.");
            return;
        }
        // Ensure we are not buying our own
        if (listing.getUserId().equals(user.getId())) {
            // CWE-779: Logging of Excessive Data
            logger.fine(user.getUsername() + " attempted to buy listing " + listing.getId() + " which they own");
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
        String listingId = InputValidation.readString(REMOVE_LISTING_PROMPT, INVALID_LISTING_ID);
        // CWE-229: Improper Handling of Values - validate UUID format before parsing
        UUID listingUUID;
        try {
            listingUUID = UUID.fromString(listingId);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid listing ID format.");
            return;
        }
        Listing listing = listingHandler.getListing(listingUUID);

        // Ensure listing exists
        if (listing == null) {
            // CWE-779: Logging of Excessive Data
            logger.fine(user.getUsername() + " attempted to delete listing " + listingId + " which does not exist");
            System.out.println("Invalid listing.");
            return;
        }
        // Ensure we are deleting our own
        if (!listing.getUserId().equals(user.getId())) {
            // CWE-778: Insufficient Logging
            logger.warning(user.getUsername() + " attempted to delete listing " + listing.getId() + " which they don't own");
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
        String filePath = "docs/" + fileName + ".txt";
        File file = new File(filePath);
        // CWE-459: Incomplete Cleanup
        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                System.out.println(line);
            }
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

    private static <E> void bulkPrintList(List<E> itemList) {
        if (itemList == null || itemList.size() == 0)
            return;

        System.out.println(MENU_LINES);
        for (int i = 0; i < itemList.size(); i++) {
            System.out.println(itemList.get(i));
            if (i < itemList.size() - 1) {
                System.out.println();
            }
        }
        System.out.println(MENU_LINES);
    }
    
    /**
     * CWE-306 Need to check for critical functions if the user is an admin
     * @param user
     */
    private void requireAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            logger.warning("Unauthorized admin access attempt");
            System.out.println("Access denied.");
            System.exit(0);
        }
    }

    /**
     * Clean up the handlers for the input controller and the input validation class
     */
    public void cleanUp() {
        InputValidation.cleanUp();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
