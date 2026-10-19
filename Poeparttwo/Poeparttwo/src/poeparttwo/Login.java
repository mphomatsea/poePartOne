package poepartone;

/**
 * Handles login: verifying a username/password pair against the details
 * captured during registration, and reporting the outcome.
 *
 * @author Student
 */
public class Login extends Registration {

    /**
     * Registers a user and remembers their first and last name so they can
     * be greeted after logging in.
     */
    public String registerUser(String firstName, String lastName, String username,
            String password, String cellphone) {
        String result = super.registerUser(username, password, cellphone);
        if (result.equals("Registration was successful")) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
        return result;
    }

    /**
     * @return true if the given username and password match the details
     * stored when the user registered.
     */
    public boolean loginUser(String username, String password) {
        return username != null
                && password != null
                && username.equals(this.userName)
                && password.equals(this.passWord);
    }

    /**
     * @return the message for a successful or failed login.
     */
    public String returnLoginStatus(boolean loginStatus) {
        if (loginStatus) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }
}
