package poepartone;

import static org.testng.Assert.*;
import java.io.File;
import java.nio.file.Files;
import org.testng.annotations.Test;

/**
 * Part 2 unit tests using the test data from the PoE brief.
 *
 * @author Student
 */
public class MessageNGTest {

    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @Test
    public void testMessageLength_Success() {
        assertEquals(new Message().checkMessageLength(MESSAGE_1), "Message ready to send.");
    }

    @Test
    public void testMessageLength_Failure() {
        String longMessage = "a".repeat(260);
        assertEquals(new Message().checkMessageLength(longMessage),
                "Message exceeds 250 characters by 10; please reduce the size.");
    }

    @Test
    public void testRecipient_Success() {
        assertEquals(new Message().checkRecipientCell("+27718693002"),
                "Cell phone number successfully captured.");
    }

    @Test
    public void testRecipient_Failure() {
        assertEquals(new Message().checkRecipientCell("08575975889"),
                "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.");
    }

    @Test
    public void testMessageHash_TestCase1() {
        assertEquals(new Message().createMessageHash("0012345678", 0, MESSAGE_1), "00:0:HITONIGHT");
    }

    @Test
    public void testMessageHash_AllMessagesInLoop() {
        Message msg = new Message();
        String[] testMessages = {MESSAGE_1, MESSAGE_2};
        String[] expectedHashes = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
        for (int i = 0; i < testMessages.length; i++) {
            assertEquals(msg.createMessageHash("0012345678", i, testMessages[i]), expectedHashes[i]);
        }
    }

    @Test
    public void testMessageID_Generated() {
        Message msg = new Message();
        String id = msg.generateMessageID();
        assertEquals(id.length(), 10);
        assertTrue(msg.checkMessageID(id));
        System.out.println("Message ID generated: " + id);
    }

    @Test
    public void testCheckMessageID_TooLong() {
        assertFalse(new Message().checkMessageID("12345678901"));
    }

    @Test
    public void testSentMessage_Send() {
        assertEquals(new Message().SentMessage(1), "Message successfully sent.");
    }

    @Test
    public void testSentMessage_Disregard() {
        assertEquals(new Message().SentMessage(2), "Press 0 to delete the message.");
    }

    @Test
    public void testSentMessage_Store() {
        assertEquals(new Message().SentMessage(3), "Message successfully stored.");
    }

    @Test
    public void testReturnTotalMessages_Sent() {
        // Simulates the keyboard: message 1 is sent, message 2 is disregarded
        String keyboard = "+27718693002\n" + MESSAGE_1 + "\n1\n"
                + "08575975889\n+27718693003\n" + MESSAGE_2 + "\n2\n0\n";
        Message msg = new Message(new java.util.Scanner(keyboard));
        msg.sendingMessages(2);
        assertEquals(msg.returnTotalMessages(), 1);
        assertTrue(msg.printMessages().contains(MESSAGE_1));
        assertFalse(msg.printMessages().contains(MESSAGE_2));
    }

    @Test
    public void testStoreMessage_WritesJson() throws Exception {
        File temp = File.createTempFile("stored", ".json");
        temp.deleteOnExit();
        Message msg = new Message();
        msg.jsonFileName = temp.getAbsolutePath();
        msg.storeMessage("1234567890", "12:0:HITONIGHT", "+27718693002", MESSAGE_1);
        String content = Files.readString(temp.toPath());
        assertTrue(content.contains("\"messageID\": \"1234567890\""));
        assertTrue(content.contains(MESSAGE_1));
    }
}
