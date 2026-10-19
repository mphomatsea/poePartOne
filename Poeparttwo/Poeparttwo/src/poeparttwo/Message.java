package poepartone;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Handles creating, validating, sending, storing and displaying messages
 * for the QuickChat application (Part 2).
 *
 * @author Student
 */
public class Message {

    // Parallel lists holding the messages that were SENT while the program runs
    ArrayList<String> messageIds = new ArrayList<>();
    ArrayList<String> recipientNumber = new ArrayList<>();
    ArrayList<String> messages = new ArrayList<>();
    ArrayList<String> msgHash = new ArrayList<>();

    // JSON objects (as text) for messages the user chose to store for later
    ArrayList<String> storedJsonEntries = new ArrayList<>();

    Scanner input;
    Random random = new Random();

    int numMessageSent = 0;
    String jsonFileName = "storedMessages.json";

    /** Creates a Message object that reads from the keyboard. */
    public Message() {
        this(new Scanner(System.in));
    }

    /** Creates a Message object that reads from the supplied scanner. */
    public Message(Scanner input) {
        this.input = input;
    }

   
    public void sendingMessages(int numberOfMessages) {
        // for loop: runs exactly once per message the user said they would enter
        for (int i = 0; i < numberOfMessages; i++) {
            System.out.println("\nMESSAGE " + (i + 1) + " of " + numberOfMessages);

            String messageID = generateMessageID();
            System.out.println("Message ID generated: " + messageID);

            // Recipient: keep asking until the number is valid
            String recipient;
            while (true) {
                System.out.println("Enter recipient cell number (with international code, e.g. +27718693002):");
                recipient = input.nextLine().trim();
                String recipientResult = checkRecipientCell(recipient);
                System.out.println(recipientResult);
                if (recipientResult.equals("Cell phone number successfully captured.")) {
                    break;
                }
            }

            // Message text: keep asking until it is 1-250 characters
            String messageText;
            while (true) {
                System.out.println("Enter your message (max 250 characters):");
                messageText = input.nextLine();
                String lengthResult = checkMessageLength(messageText);
                System.out.println(lengthResult);
                if (lengthResult.equals("Message ready to send.")) {
                    break;
                }
            }

           String messageHash = createMessageHash(messageID, i + 1, messageText);

            int choice = promptChoice();
            System.out.println(SentMessage(choice));

            switch (choice) {
                case 1 -> {
                    messageIds.add(messageID);
                    recipientNumber.add(recipient);
                    messages.add(messageText);
                    msgHash.add(messageHash);
                    numMessageSent++;
                    System.out.println(formatMessageDetails(messages.size() - 1));
                }
                case 2 -> {
                    String confirm = input.nextLine().trim();
                    if (confirm.equals("0")) {
                        System.out.println("Message deleted.");
                    } else {
                        System.out.println("Message disregarded.");
                    }
                }
                case 3 -> storeMessage(messageID, messageHash, recipient, messageText);
                default -> { }
            }
        }

        System.out.println("\n===== ALL SENT MESSAGES =====");
        System.out.println(printMessages());
        System.out.println("Total number of messages sent: " + returnTotalMessages());
    }

    /** Asks the user what to do with the current message and returns 1, 2 or 3. */
    private int promptChoice() {
        while (true) {
            System.out.println("\nChoose what you want to do with this message:");
            System.out.println("1) Send Message");
            System.out.println("2) Disregard Message");
            System.out.println("3) Store Message to send later");
            System.out.println("Enter your choice: ");
            String text = input.nextLine().trim();
            if (text.equals("1") || text.equals("2") || text.equals("3")) {
                return Integer.parseInt(text);
            }
            System.out.println("Invalid option, please enter 1, 2 or 3.");
        }
    }

    /** @return the total number of messages sent so far. */
    public int returnTotalMessages() {
        return numMessageSent;
    }

  
    public String generateMessageID() {
        String messageID;
        do {
            long number = 1000000000L + random.nextLong(9000000000L);
            messageID = String.valueOf(number);
        } while (messageIds.contains(messageID));
        return messageID;
    }

    /** @return true if the message ID is no more than ten characters long. */
    public boolean checkMessageID(String messageID) {
        return messageID != null
                && !messageID.isEmpty()
                && messageID.length() <= 10;
    }

   
    public String checkRecipientCell(String cellphone) {
        Registration check = new Registration();
        if (check.checkCellPhoneNumber(cellphone)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

   
    public String checkMessageLength(String messageText) {
        if (messageText == null || messageText.trim().isEmpty()) {
            return "Please enter a message of at least one character.";
        }
        if (messageText.length() > 250) {
            int extra = messageText.length() - 250;
            return "Message exceeds 250 characters by " + extra + "; please reduce the size.";
        }
        return "Message ready to send.";
    }

   
    public String createMessageHash(String messageID, int messageNumber, String messageText) {
        String firstTwoNumbers = messageID.substring(0, 2);
        String[] words = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");
        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + lastWord;
        return hash.toUpperCase();
    }
    
    public String SentMessage(int choice) {
        switch (choice) {
            case 1 -> {
                return "Message successfully sent.";
            }
            case 2 -> {
                return "Press 0 to delete the message.";
            }
            case 3 -> {
                return "Message successfully stored.";
            }
            default -> {
                return "Invalid option.";
            }
        }
    }

    /** @return the details of one sent message: ID, hash, recipient, message. */
    private String formatMessageDetails(int index) {
        return "Message ID: " + messageIds.get(index)
                + "\nMessage Hash: " + msgHash.get(index)
                + "\nRecipient: " + recipientNumber.get(index)
                + "\nMessage: " + messages.get(index);
    }

    /** @return the details of every message sent while the program has been running. */
    public String printMessages() {
        StringBuilder allMessages = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            allMessages.append(formatMessageDetails(i)).append("\n\n");
        }
        return allMessages.toString();
    }

   
    public void storeMessage(String messageID, String messageHash, String recipient, String messageText) {
        String entry = "  {\n"
                + "    \"messageID\": \"" + escapeJson(messageID) + "\",\n"
                + "    \"messageHash\": \"" + escapeJson(messageHash) + "\",\n"
                + "    \"recipient\": \"" + escapeJson(recipient) + "\",\n"
                + "    \"message\": \"" + escapeJson(messageText) + "\"\n"
                + "  }";
        storedJsonEntries.add(entry);

        try (FileWriter writer = new FileWriter(jsonFileName)) {
            writer.write("[\n" + String.join(",\n", storedJsonEntries) + "\n]\n");
        } catch (IOException e) {
            System.out.println("Could not write to the JSON file: " + e.getMessage());
        }
    }

    /** Escapes characters that are not allowed raw inside a JSON string. */
    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
