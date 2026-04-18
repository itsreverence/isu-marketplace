import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Helper class to provide functionality to user input validation.
 * Currently validates user inputs with integers, floats, and strings.
 * CWE-1080: Source Code File with Excessive Number of Lines of Code
 * This file should stay below 1000 lines of code or be split into multiple files
 */
public class InputValidation {

    private static Logger logger = initLogger(); // logger for the input validation

    private static final Scanner SCANNER = new Scanner(System.in); // scanner for the input validation

    /**
     * Method to validate a user inputted String, and returns a valid String.
     * 
     * @param askingArgument The prompt the user is given for inputting a string.
     * @param failedString   The prompt the user is given for inputting a bad
     *                       string.
     * @return A correctly formatted string.
     */
    public static String readString(String askingArgument, String failedString) {
        String stringResult = null;
        // CWE-1095: Loop Condition Value Update within the Loop
        while (true) {
            System.out.println(askingArgument);
            stringResult = SCANNER.nextLine();

            // normalizeString;
            stringResult = Normalizer.normalize(stringResult, Form.NFKC);
            if (stringResult == null || stringResult.isEmpty() || stringResult.isBlank()) {
                System.out.println(failedString);
            } else {
                return stringResult;
            }
        }
    }

    /**
     * Method to validate a user inputted integer, and returns a valid integer.
     * 
     * @param askingArgument The prompt the user is given for inputting an integer.
     * @param failedString   The prompt the user is given for inputting a bad
     *                       integer.
     * @param maxVal         The largest value some arbitrary menu should accept.
     * @return A correctly formatted integer.
     */
    public static int readInt(String askingArgument, String failedString, int maxVal) {
        // CWE-1095: Loop Condition Value Update within the Loop
        while (true) {
            System.out.println(askingArgument);
            try {
                int choice = SCANNER.nextInt();
                if (choice < 1 || choice > maxVal) {
                    System.out.println(failedString);
                } else {
                    // if we get here, we have a good number!
                    return choice;
                }
            } catch (InputMismatchException e) {
                // CWE-778: Insufficient Logging
                logger.severe("Error reading int: " + e.getMessage());
                System.out.println(failedString);
            } finally {
                SCANNER.nextLine(); // consume
            }
        }
    }

    /**
     * Method to validate a user inputted float, and returns a valid float.
     * 
     * @param askingArgument The prompt the user is given for inputting a float.
     * @param failedString   The prompt the user is given for inputting a bad float.
     * @return A correctly formatted float.
     */
    public static float readFloat(String askingArgument, String failedString) {
        // CWE-1095: Loop Condition Value Update within the Loop
        while (true) {
            System.out.println(askingArgument);
            try {
                float floatResult = SCANNER.nextFloat();
                if (floatResult < 0) {
                    System.out.println(failedString);
                } else {
                    return floatResult;
                }
            } catch (InputMismatchException e) {
                // CWE-778: Insufficient Logging
                logger.severe("Error reading float: " + e.getMessage());
                System.out.println(failedString);
            } finally {
                SCANNER.nextLine(); // consume
            }
        }
    }

    /**
     * Initializes the logger for the input validation
     * 
     * @return the logger for the input validation
     */
    private static Logger initLogger() {
        try {
            Logger logger = Logger.getLogger("InputValidationLogger");
            File logDirectory = new File("./logs/");
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
            }
            // CWE-779: Logging of Excessive Data
            FileHandler fileHandler = new FileHandler("./logs/InputValidationLogger.log", 1000000, 1, true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            // CWE-778: Insufficient Logging
            logger.info("InputValidation logger initialized");
            return logger;
        } catch (IOException e) {
            // CWE-778: Insufficient Logging
            System.err.println("Error initializing logger: " + e.getMessage());
        }
        return null;
    }

    /**
     * Clean up the handlers for the input validation
     */
    public static void cleanUp() {
        SCANNER.close();
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) {
            handler.close();
            logger.removeHandler(handler);
        }
    }
}
