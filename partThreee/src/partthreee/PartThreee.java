package partthreee;

import javax.swing.JOptionPane;

/**
 *
 * @author Student
 */
public class PartThreee {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Login register = new Login();

        Message msg = new Message();
        // ADDED: read the stored messages from the JSON file into the arrays
        msg.loadStoredMessages();

        JOptionPane.showMessageDialog(null,
                "===== Register =====",
                "Registration",
                JOptionPane.INFORMATION_MESSAGE);

        String name = JOptionPane.showInputDialog(null, "Please enter the username: ");

        String password = JOptionPane.showInputDialog(null, "Please enter your password");

        String cellphone = JOptionPane.showInputDialog(null, "Please enter your cellphone");

        JOptionPane.showMessageDialog(null, register.registerUser(name, password, cellphone));

        if (!register.registerStatus(name,
                password, cellphone)) {
            return;
        }

        JOptionPane.showMessageDialog(null, "===== Login =====",
                "Login",
                JOptionPane.INFORMATION_MESSAGE);

        String loginUsername = JOptionPane.showInputDialog(null, "Enter username to login");
        String loginPassword = JOptionPane.showInputDialog(null, "Enter password to login");

        boolean status = register.loginUser(loginUsername, loginPassword);
        JOptionPane.showMessageDialog(null, register.returnLoginStatus(status));

        if (status == true) {

            // ADDED: welcome message required in Part 2
            JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

            int option;

            do {
                String menu = "===== Select any of the option below =====\n"
                        + "1. Send message\n"
                        + "2. Message Management\n"
                        + "3. Exit\n"
                        + "4. Stored Messages\n"
                        + "Select an option: ";
                String input = JOptionPane.showInputDialog(null,
                        menu,
                        "Main menu",
                        JOptionPane.QUESTION_MESSAGE);

                if (input == null) {
                    option = 3;
                } else {

                    try {
                        option = Integer.parseInt(input.trim());
                        switch (option) {
                            case 1 ->
                                msg.sendingMessages();
                            case 2 ->
                                msg.messageManagementMenu();
                            case 3 ->
                                JOptionPane.showMessageDialog(null, "Good bye!!");
                            case 4 ->
                                msg.storedMessagesMenu();
                            default ->
                                JOptionPane.showMessageDialog(null, "Invalid option!!",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (NumberFormatException e) {
                        option = 0;

                        JOptionPane.showMessageDialog(null,
                                "Please enter a valid number",
                                "Input Error",
                                JOptionPane.WARNING_MESSAGE);

                    }
                }
            } while (option != 3);
        }

    }

}