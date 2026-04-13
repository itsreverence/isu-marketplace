import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.bytes.Bytes;

public class InputController {
    private static final String INPUT_PROMPT = "Enter your choice: ";
    private static final String USERNAME_PROMPT = "Enter your username: ";
    private static final String PASSWORD_PROMPT = "Enter your password: ";
    
    private static final String LISTING_TITLE_PROMPT = "Enter the title of the listing: ";
    private static final String LISTING_DESC_PROMPT = "Enter the description of the listing: ";
    private static final String LISTING_PRICE_PROMPT = "Enter the price of the listing: ";

    private static final String BUY_LISTING_PROMPT = "Enter the ID of the listing you want to buy: ";
    private static final String REMOVE_LISTING_PROMPT = "Enter the ID of the listing you want to remove: ";

    private static final String UPDATE_USER_ROLE_USERNAME_PROMPT = "Enter the username of the user: ";
    private static final String UPDATE_USER_ROLE_PROMPT = "Enter the role of the user: ";

    private static final String DELETE_USER_USERNAME_PROMPT = "Enter the username of the user: ";

    private static final String INVALID_PROMPT = "Invalid operation, please try again.";

    public InputController() {} // default constructor

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

    private User login(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_PROMPT);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PROMPT);
            user = userHandler.login(username, password);
            // if .login returns null, login failed.
            if (user == null) {
                // encapsulate login operation via the same prompt
                System.out.println(INVALID_PROMPT);
            }
        }
        // logged in
        System.out.println("Welcome back, " + user.getUsername() + "!");
        return user;
    }

    private User register(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            String username = InputValidation.readString(USERNAME_PROMPT, INVALID_PROMPT);
            String password = InputValidation.readString(PASSWORD_PROMPT, INVALID_PROMPT);
            user = userHandler.register(username, password);
            // TODO: we should also handle the case where user is null (if .register() fails)
        }
        System.out.println("Welcome, " + user.getUsername() + "!");
        return user;
    }

    public boolean mainMenu(User user, ListingHandler listingHandler, UserHandler userHandler) throws SQLException {
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
        
    }

    public boolean adminMenu(User user, ListingHandler listingHandler, UserHandler userHandler) throws SQLException {
        System.out.println("1.) Your Listings");
        System.out.println("2.) New Listing");
        System.out.println("3.) Remove Listing");
        System.out.println("4.) Browse Listings");
        System.out.println("5.) Buy Listing");
        System.out.println("6.) Manage Users");
        System.out.println("7.) Help");
        System.out.println("8.) Exit");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 8);
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
                help("adminMenu");
                returnValue = true;
                break;
            case 8:
                // again, i hope this is okay
                System.exit(0); 
        }
        return returnValue;
    }

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

    private void viewUsers(UserHandler userHandler) {
        List<User> users = userHandler.getUsers();
        for (User user : users) {
            System.out.println("ID: " + user.getId());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Role: " + user.getRole());
            System.out.println("");
        }
    }

    private void updateUserRole(UserHandler userHandler) throws SQLException {
        String username = InputValidation.readString(UPDATE_USER_ROLE_USERNAME_PROMPT, INVALID_PROMPT);
        System.out.println("1.) Member");
        System.out.println("2.) Admin");
        int choice = InputValidation.readInt(UPDATE_USER_ROLE_PROMPT, INVALID_PROMPT, 2);
        switch (choice) {
            case 1:
                userHandler.updateUserRole(username, Role.MEMBER);
                break;
            case 2:
                userHandler.updateUserRole(username, Role.ADMIN);
                break;
        }
    }

    private void deleteUser(UserHandler userHandler, ListingHandler listingHandler) throws SQLException {
        String username = InputValidation.readString(DELETE_USER_USERNAME_PROMPT, INVALID_PROMPT);
        userHandler.deleteUser(username);
        listingHandler.deleteUserListings(username);
    }

    private void userListings(User user, ListingHandler listingHandler) {
        List<Listing> listings = listingHandler.getUserListings(user);
        for (Listing listing : listings) {
            System.out.println("Title: " + listing.getTitle());
            System.out.println("Description: " + listing.getDescription());
            System.out.println("Price: " + listing.getPrice());
            System.out.println("");
        }
    }

    private void newListing(User user, ListingHandler listingHandler) {
        String title = InputValidation.readString(LISTING_TITLE_PROMPT, INVALID_PROMPT);
        String description = InputValidation.readString(LISTING_DESC_PROMPT, INVALID_PROMPT);
        float price = InputValidation.readFloat(LISTING_PRICE_PROMPT, INVALID_PROMPT);

        // create listing with validated input
        listingHandler.createListing(user, title, description, price);
        System.out.println("Your listing has been published to the marketplace.");
    }

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
    }

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
    }

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
            e.printStackTrace();
        }
    }
 }
