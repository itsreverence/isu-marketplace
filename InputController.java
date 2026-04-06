import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class InputController {
    private static final String INPUT_PROMPT = "Enter your choice: ";
    private static final String USERNAME_PROMPT = "Enter your username: ";
    private static final String PASSWORD_PROMPT = "Enter your password: ";
    
    private static final String LISTING_TITLE_PROMPT = "Enter the title of the listing: ";
    private static final String LISTING_DESC_PROMPT = "Enter the description of the listing: ";
    private static final String LISTING_PRICE_PROMPT = "Enter the price of the listing: ";

    private static final String BUY_LISTING_PROMPT = "Enter the ID of the listing you want to buy: ";

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
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt()); // password hash via bcrypt
            user = userHandler.register(username, passwordHash);
            // TODO: we should also handle the case where user is null (if .register() fails)
        }
        System.out.println("Welcome, " + user.getUsername() + "!");
        return user;
    }

    public boolean mainMenu(User user, ListingHandler listingHandler) {
        System.out.println("1.) Your Listings");
        System.out.println("2.) New Listing");
        System.out.println("3.) Browse Listings");
        System.out.println("4.) Buy Listing");
        System.out.println("5.) Help");
        System.out.println("6.) Exit");

        int choice = InputValidation.readInt(INPUT_PROMPT, INVALID_PROMPT, 6);
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
                browseListings(listingHandler);
                returnValue = true;
                break;
            case 4:
                buyListing(listingHandler);
                returnValue = true;
                break;
            case 5:
                help("mainMenu");
                returnValue = true;
                break;
            case 6:
                // again, i hope this is okay
                System.exit(0); 
        }
        return returnValue;
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

    private void buyListing(ListingHandler listingHandler) {
        String listingId = InputValidation.readString(BUY_LISTING_PROMPT, INVALID_PROMPT);
        Listing listing = listingHandler.getListing(UUID.fromString(listingId));
        // TODO: Ensure the listing is validated
        listingHandler.buyListing(listing);
        System.out.println("The specified listing has been purchased.");
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
