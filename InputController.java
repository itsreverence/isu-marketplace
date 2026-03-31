import java.util.Scanner;

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
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1:
                user = login(userHandler);
                break;
            case 2:
                user = register(userHandler);
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Please enter a valid choice between 1 and 3.");
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
}
