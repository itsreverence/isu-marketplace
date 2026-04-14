import java.sql.SQLException;

/**
 * Driver class to run the application
 */
public class Driver {
    /**
     * Main method that sets up the databases, logs in the user, and opens the main
     * menu for the user
     * 
     * @param args the arguments passed to the application
     */
    public static void main(String[] args) {
        UserHandler userHandler = new UserHandler("user");
        ListingHandler listingHandler = new ListingHandler("listing");
        InputController inputController = new InputController();
        User user = inputController.loginOrRegister(userHandler);
        boolean continueRunning = true;
        while (continueRunning) {
            try {
                continueRunning = inputController.mainMenu(user, listingHandler, userHandler);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
