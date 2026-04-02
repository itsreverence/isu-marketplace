import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

public class InputController {
    private Scanner scanner;

    public InputController() {
        scanner = new Scanner(System.in);
    }

    public User loginOrRegister(UserHandler userHandler) {
        User user = null;
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1:
                user = login(userHandler);
                break;
            case 2:
                user = register(userHandler);
                break;
            default:
                System.out.println("Please enter a valid choice between 1 and 2.");
        }
        return user;
    }

    private User login(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            user = userHandler.login(username, password);
        }
        System.out.println("Welcome back, " + user.getUsername() + "!");
        return user;
    }

    private User register(UserHandler userHandler) {
        User user = null;
        while (user == null) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
            user = userHandler.register(username, passwordHash);
        }
        System.out.println("Welcome, " + user.getUsername() + "!");
        return user;
    }

    public boolean mainMenu(User user, ListingHandler listingHandler) {
        System.out.println("1.) Your Listings");
        System.out.println("2.) New Listing");
        System.out.println("3.) Browse Listings");
        System.out.println("4.) Buy Listing");
        System.out.println("5.) Exit");

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an option: ");
        int choice = Integer.parseInt(scanner.nextLine());
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
                returnValue = true;
                break;
            case 5:
                break;
            default:
                System.out.println("Please enter a valid choice between 1 and 5.");
        }

        scanner.close();
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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the title of the listing: ");
        String title = scanner.nextLine();
        System.out.print("Enter the description of the listing: ");
        String description = scanner.nextLine();
        System.out.print("Enter the price of the listing: ");
        float price = scanner.nextFloat();
        listingHandler.createListing(user, title, description, price);
        System.out.println("Your listing has been published to the marketplace.");
        scanner.close();
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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the ID of the listing you want to buy: ");
        String listingId = scanner.nextLine();
        Listing listing = listingHandler.getListing(UUID.fromString(listingId));
        listingHandler.buyListing(listing);
        System.out.println("The specified listing has been purchased.");
    }
 }
