public class Driver {
    public static void main(String[] args) {
        UserHandler userHandler = new UserHandler("user");
        InputController inputController = new InputController();
        User user = inputController.loginOrRegister(userHandler);

    }
}
