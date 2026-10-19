package poepartone;

import java.util.Scanner;

/**
 * Console entry point: registers a new user, logs them in, and then runs the
 * QuickChat menu until the user chooses to quit.
 *
 * @author Student
 */
public class Poeparttwo {

    public static void main(String[] args) {
        Login user = new Login();
        Scanner scan = new Scanner(System.in);

        System.out.println("====================");
        System.out.println(" Welcome, let's begin!! ");
        System.out.println("====================\n");

        // ---------- REGISTRATION (repeat until all details are valid) ----------
        System.out.println("===== REGISTRATION =====");

        System.out.print("Enter first name: ");
        String firstName = scan.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scan.nextLine();

        boolean registered = false;
        while (!registered) {
            System.out.print("Enter username: ");
            String username = scan.nextLine();
            System.out.println(user.usernameMessage(username));

            System.out.print("Enter password: ");
            String password = scan.nextLine();
            System.out.println(user.passwordMessage(password));

            System.out.print("Enter South African cell phone number (e.g. +27838968976): ");
            String cellphone = scan.nextLine();
            System.out.println(user.cellMessage(cellphone));

            String registrationResult = user.registerUser(firstName, lastName, username, password, cellphone);
            registered = registrationResult.equals("Registration was successful");
            System.out.println("\n" + registrationResult);

            if (!registered) {
                System.out.println("Please correct your details and try registering again.\n");
            }
        }

        // ---------- LOGIN (repeat until correct) ----------
        System.out.println("\n===== LOGIN =====");
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.print("Enter username: ");
            String loginUsername = scan.nextLine();

            System.out.print("Enter password: ");
            String loginPassword = scan.nextLine();

            loggedIn = user.loginUser(loginUsername, loginPassword);
            System.out.println("\n" + user.returnLoginStatus(loggedIn));
        }

        // ---------- QUICKCHAT (only reachable after a successful login) ----------
        System.out.println("\nWelcome to QuickChat.");

        Message msg = new Message(scan);
        String option = "";

        // while loop: the application runs until the user selects Quit
        while (!option.equals("3")) {
            System.out.println("\n===== MENU =====");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("Enter your choice: ");

            option = scan.nextLine().trim();

            switch (option) {
                case "1" -> {
                    // Ask how many messages to send only when the user picks option 1
                    int numberOfMessages = 0;
                    while (numberOfMessages <= 0) {
                        System.out.println("How many messages do you want to send?");
                        try {
                            numberOfMessages = Integer.parseInt(scan.nextLine().trim());
                        } catch (NumberFormatException e) {
                            numberOfMessages = 0;
                        }
                        if (numberOfMessages <= 0) {
                            System.out.println("Please enter a whole number greater than 0.");
                        }
                    }
                    msg.sendingMessages(numberOfMessages);
                }
                case "2" -> System.out.println("Coming Soon.");
                case "3" -> System.out.println("Goodbye!!");
                default -> System.out.println("Invalid option, please enter 1, 2 or 3.");
            }
        }

        scan.close();
    }
}