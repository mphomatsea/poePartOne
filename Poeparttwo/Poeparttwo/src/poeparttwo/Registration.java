package poepartone;

/**
 * Handles the registration business logic: validating a username, password
 * and South African cell phone number, and storing the captured details.
 *
 * @author Student
 */
public class Registration {

    // Messages required by the PoE brief
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure that your "
            + "username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure that the "
            + "password contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_OK = "Cell phone number successfully added.";
    public static final String CELL_BAD = "Cell phone number incorrectly formatted or does not contain "
            + "international code.";

    String firstName;
    String lastName;
    String userName;
    String passWord;
    String cellPhone;

    /**
     * @return true if the username contains an underscore and is no more
     * than five characters long.
     */
    public boolean checkUserName(String userName) {
        return userName != null
                && userName.contains("_")
                && userName.length() <= 5;
    }

    /**
     * @return true if the password is at least eight characters long and
     * contains a capital letter, a number, and a special character.
     */
    public boolean checkPasswordComplexity(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    
    public boolean checkCellPhoneNumber(String cellphone) {
        return cellphone != null
                && cellphone.matches("^\\+27[0-9]{1,10}$");
    }

    /** @return the message for the username check (success or failure). */
    public String usernameMessage(String username) {
        return checkUserName(username) ? USERNAME_OK : USERNAME_BAD;
    }

    /** @return the message for the password check (success or failure). */
    public String passwordMessage(String password) {
        return checkPasswordComplexity(password) ? PASSWORD_OK : PASSWORD_BAD;
    }

    /** @return the message for the cell phone check (success or failure). */
    public String cellMessage(String cellphone) {
        return checkCellPhoneNumber(cellphone) ? CELL_OK : CELL_BAD;
    }

  
    public String registerUser(String username, String password, String cellphone) {
        if (!checkUserName(username)) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity(password)) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber(cellphone)) {
            return CELL_BAD;
        }
        this.userName = username;
        this.passWord = password;
        this.cellPhone = cellphone;
        return "Registration was successful";
    }

    /**
     * @return true if the username, password and cell phone number are all
     * correctly formatted.
     */
    public boolean registerStatus(String username, String password, String cellphone) {
        return checkUserName(username)
                && checkPasswordComplexity(password)
                && checkCellPhoneNumber(cellphone);
    }
}
