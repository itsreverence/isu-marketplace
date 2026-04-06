import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Helper class to provide functionality to user input validation.
 * <br>
 * Currently validates user inputs with integers and strings.
 */
public class InputValidation {

    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Method to validate a user inputted String, and returns a valid String.
     * @param askingArgument The prompt the user is given for inputting a string.
     * @param failedString The prompt the user is given for inputting a bad string.
     * @return A correctly formatted string.
     */
    public static String string(String askingArgument, String failedString) {
        String stringResult = null;
        boolean invalidInput = true;
        while (invalidInput) {
            System.out.println(askingArgument);
            stringResult = SCANNER.nextLine();

            // normalizeString;
            stringResult = Normalizer.normalize(stringResult, Form.NFKC);
            if (stringResult == null || stringResult.isEmpty() || stringResult.isBlank()) {
                System.out.println(failedString);
            } else {
                invalidInput = false;
            }
        }
        return stringResult;
    }

    /**
     * Method to validate a user inputted integer, and returns a valid integer.
     * @param askingArgument The prompt the user is given for inputting an integer.
     * @param failedString The prompt the user is given for inputting a bad integer.
     * @param maxVal The largest value some arbitrary menu should accept.
     * @return A correctly formatted integer.
     */
    public static int integer(String askingArgument, String failedString, int maxVal) {
        int choice = 0; // automatically out of range
        boolean invalidInput = true;
        while (invalidInput) {
            System.out.println(askingArgument);
            try {
                choice = SCANNER.nextInt();
                if (choice < 1 || choice > maxVal) {
                    System.out.println(failedString);
                } else {
                    // if we get here, we have a good number!
                    invalidInput = false;
                }
            } catch (InputMismatchException e) {
                // log here -->
                System.out.println(failedString);
            } finally {
                SCANNER.nextLine(); // consume
            }
        }
        return choice;
    }
}
