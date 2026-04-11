import java.sql.SQLException;

public class Driver {
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
