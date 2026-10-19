package poepartone;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Part 1 unit tests using the test data from the PoE brief.
 *
 * @author Student
 */
public class RegistrationNGTest {

    // ---------- assertEquals tests (messages) ----------

    @Test
    public void testUsernameMessage_Valid() {
        assertEquals(new Registration().usernameMessage("kyl_1"), "Username successfully captured.");
    }

    @Test
    public void testUsernameMessage_Invalid() {
        assertEquals(new Registration().usernameMessage("kyle!!!!!!!"),
                "Username is not correctly formatted; please ensure that your username contains an "
                + "underscore and is no more than five characters in length.");
    }

    @Test
    public void testPasswordMessage_Valid() {
        assertEquals(new Registration().passwordMessage("Ch&&sec@ke99!"), "Password successfully captured.");
    }

    @Test
    public void testPasswordMessage_Invalid() {
        assertEquals(new Registration().passwordMessage("password"),
                "Password is not correctly formatted; please ensure that the password contains at least "
                + "eight characters, a capital letter, a number, and a special character.");
    }

    @Test
    public void testCellMessage_Valid() {
        assertEquals(new Registration().cellMessage("+27838968976"), "Cell phone number successfully added.");
    }

    @Test
    public void testCellMessage_Invalid() {
        assertEquals(new Registration().cellMessage("08966553"),
                "Cell phone number incorrectly formatted or does not contain international code.");
    }

    @Test
    public void testRegisterUser_Valid() {
        assertEquals(new Registration().registerUser("kyl_1", "Ch&&sec@ke99!", "+27838968976"),
                "Registration was successful");
    }

    @Test
    public void testRegisterUser_BadUsername() {
        assertEquals(new Registration().registerUser("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"),
                Registration.USERNAME_BAD);
    }

    @Test
    public void testRegisterUser_BadPassword() {
        assertEquals(new Registration().registerUser("kyl_1", "password", "+27838968976"),
                Registration.PASSWORD_BAD);
    }

    // ---------- assertTrue / assertFalse tests ----------

    @Test
    public void testCheckUserName_True() {
        assertTrue(new Registration().checkUserName("kyl_1"));
    }

    @Test
    public void testCheckUserName_False() {
        assertFalse(new Registration().checkUserName("kyle!!!!!!!"));
    }

    @Test
    public void testCheckPasswordComplexity_True() {
        assertTrue(new Registration().checkPasswordComplexity("Ch&&sec@ke99!"));
    }

    @Test
    public void testCheckPasswordComplexity_False() {
        assertFalse(new Registration().checkPasswordComplexity("password"));
    }

    @Test
    public void testCheckCellPhoneNumber_True() {
        assertTrue(new Registration().checkCellPhoneNumber("+27838968976"));
    }

    @Test
    public void testCheckCellPhoneNumber_False() {
        assertFalse(new Registration().checkCellPhoneNumber("08966553"));
    }

    @Test
    public void testRegisterStatus_True() {
        assertTrue(new Registration().registerStatus("kyl_1", "Ch&&sec@ke99!", "+27838968976"));
    }

    @Test
    public void testRegisterStatus_False() {
        assertFalse(new Registration().registerStatus("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976"));
    }

    // ---------- Login tests ----------

    private Login createRegisteredUser() {
        Login user = new Login();
        user.registerUser("Kyle", "Smith", "kyl_1", "Ch&&sec@ke99!", "+27838968976");
        return user;
    }

    @Test
    public void testLoginUser_Successful() {
        assertTrue(createRegisteredUser().loginUser("kyl_1", "Ch&&sec@ke99!"));
    }

    @Test
    public void testLoginUser_Failed() {
        assertFalse(createRegisteredUser().loginUser("kyl_1", "wrongPass1!"));
    }

    @Test
    public void testReturnLoginStatus_Successful() {
        assertEquals(createRegisteredUser().returnLoginStatus(true),
                "Welcome Kyle, Smith it is great to see you again.");
    }

    @Test
    public void testReturnLoginStatus_Failed() {
        assertEquals(createRegisteredUser().returnLoginStatus(false),
                "Username or password incorrect, please try again.");
    }
}
