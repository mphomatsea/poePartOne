package partthreee;

import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Handles creating, sending, storing, searching and deleting messages.
 * Messages are kept in parallel ArrayLists (same index = same message).
 *
 * @author Student
 */
public class Message {
    ArrayList<String> sentMessages = new ArrayList<>();      // text of every message kept in the app
    ArrayList<String> messageIdsList = new ArrayList<>();    // Message ID array
    HashSet<String> messageIds = new HashSet<>();
    ArrayList<String> msgHash = new ArrayList<>();           // Message Hash array
    ArrayList<String> recipientNumber = new ArrayList<>();
    ArrayList<String> messageStatus = new ArrayList<>();     // "sent", "stored" or "Disregarded"
    ArrayList<String> disregardedMessages = new ArrayList<>(); // Disregarded Messages array
    ArrayList<String> storedMessages = new ArrayList<>();      // Stored Messages array (read from JSON)
    ArrayList<String> senders = new ArrayList<>();
    ArrayList<String> messageID = new ArrayList<>();

    Random random = new Random();

    int numMessageSent = 0;

    // File used to store messages in JSON (can be changed, e.g. by unit tests)
    String jsonFile = "storedMessages.json";

    public void sendingMessages() {

        int numberOfMessages = 0;
        while (true) {
            String input = JOptionPane.showInputDialog(null,
                    "How many messages do you want to send? ",
                    "Send Messages",
                    JOptionPane.QUESTION_MESSAGE);
            if (input == null) {
                return;
            }
            try {
                numberOfMessages = Integer.parseInt(input.trim());
                if (numberOfMessages < 1) {
                    // FIX: was showInputDialog, an error must use showMessageDialog
                    JOptionPane.showMessageDialog(null,
                            "Please enter a number greater than 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        for (int i = 1; i <= numberOfMessages; i++) {

            String messageID = generateMessageID();
            String recipient;

            while (true) {
                recipient = JOptionPane.showInputDialog(null,
                        "Message " + i + " of " + numberOfMessages
                        + "\n\nEnter recipient cell number: ",
                        "Recipient", JOptionPane.QUESTION_MESSAGE);
                if (recipient == null) {
                    return;
                }
                recipient = recipient.trim();
                String recipientResult = checkRecipientCell(recipient);
                if (recipientResult.equals("Valid number")) {
                    break;
                }
                // FIX: removed the stray "+" before JOptionPane.ERROR_MESSAGE
                JOptionPane.showMessageDialog(null,
                        recipientResult,
                        "Invalid number",
                        JOptionPane.ERROR_MESSAGE);
            }
            String messageText;
            while (true) {

                messageText = JOptionPane.showInputDialog(null,
                        "Enter your message (1 to 250 characters long): ",
                        "Message", JOptionPane.QUESTION_MESSAGE);

                if (messageText == null) {
                    return;
                }

                if (messageText.length() > 250 || messageText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            checkMessageLength(messageText),
                            "Invalid message",
                            JOptionPane.ERROR_MESSAGE);

                } else {
                    break;
                }
            }
            int messageNumber = sentMessages.size() + 1;
            String messageHash = createMessageHash(messageID, messageNumber, messageText);
            msgHash.add(messageHash);
            sentMessages.add(messageText);
            messageIdsList.add(messageID);
            recipientNumber.add(recipient);

            String sender = JOptionPane.showInputDialog(null,
                    "Enter sender name", "sender");
            if (sender == null || sender.trim().isEmpty()) {
                sender = "unknown";
            }
            senders.add(sender.trim());

            String status = SentMessage();

            // FIX: a deleted message was already removed from every list,
            // so no status may be added for it (this kept the arrays in line).
            if (!status.equals("deleted")) {
                messageStatus.add(status);
            }
            if (status.equals("sent")) {
                numMessageSent++;
            }
            if (status.equals("stored")) {
                // FIX: stored messages were never put in the stored array or the JSON file
                storedMessages.add(messageText);
                storeMessage();
            }
        }
        showText("All Messages", printMessages());

        JOptionPane.showMessageDialog(null,
                "Total messages sent: "
                + returnTotalMessages(),
                "Total",
                JOptionPane.INFORMATION_MESSAGE);

    }

    public int returnTotalMessages() {

        return numMessageSent;
    }

    public String createMessageHash(String messageID, int messageNumber, String messageText) {

        String firstTwoNumbers = messageID.substring(0, 2);

        String cleanMessage = messageText.trim();

        String[] words = cleanMessage.split("\\s+");

        // FIX: punctuation is removed so "tonight?" becomes TONIGHT (HITONIGHT)
        String firstWord = words[0].replaceAll("[^A-Za-z0-9]", "");

        String lastWord = words[words.length - 1].replaceAll("[^A-Za-z0-9]", "");

        String hash = firstTwoNumbers + ":" + messageNumber + ":" + firstWord + lastWord;

        return hash.toUpperCase();
    }

    public String generateMessageID() {

        String messageID;

        do {

            long number = 1000000000L + random.nextLong(9000000000L);

            messageID = String.valueOf(number);

        } while (messageIds.contains(messageID));

        // FIX: the ID was never added to the set so duplicates were not detected
        messageIds.add(messageID);

        return messageID;
    }

    // ADDED (Part 2 requirement): message ID must be no more than ten characters
    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }

    // ADDED (Part 2 requirement): message length check with the required wording
    public String checkMessageLength(String messageText) {
        if (messageText.length() > 250) {
            int over = messageText.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
        if (messageText.trim().isEmpty()) {
            return "Please enter a message of 1 to 250 characters long";
        }
        return "Message ready to send.";
    }

    public String checkRecipientCell(String cellphone) {

        if (cellphone.matches("^\\+27[0-9]{9}$") || cellphone.matches("^0[0-9]{9}$")) {

            return "Valid number";

        } else {

            return "Invalid number";
        }
    }

    public String SentMessage() {
        Object[] options = {"Send", "Disregarded", "Store"};

        int choice = JOptionPane.showOptionDialog(null,
                "Choose what you want to do with this message!",
                "Message Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options,
                options[0]);

        switch (choice) {
            case 0 -> {
                JOptionPane.showMessageDialog(null,
                        "Message sent successfully",
                        "Sent",
                        JOptionPane.INFORMATION_MESSAGE);
                return "sent";
            }
            case 1 -> {
                int deleteChoice = JOptionPane.showConfirmDialog(null,
                        "Do you want to delete this message?",
                        "Disregarded Message",
                        JOptionPane.YES_NO_OPTION);

                if (deleteChoice == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null,
                            "Message deleted",
                            "Deleted",
                            JOptionPane.INFORMATION_MESSAGE);
                    int lastIndex = sentMessages.size() - 1;
                    String messageID = messageIdsList.get(lastIndex);
                    // FIX: the disregarded array was never populated
                    disregardedMessages.add(sentMessages.get(lastIndex));
                    messageIds.remove(messageID);
                    recipientNumber.remove(lastIndex);
                    sentMessages.remove(lastIndex);
                    msgHash.remove(lastIndex);
                    senders.remove(lastIndex);
                    messageIdsList.remove(lastIndex);
                    // FIX: returns "deleted" so no status is added for a removed message
                    return "deleted";

                } else {

                    JOptionPane.showMessageDialog(null,
                            "Message kept");

                    return "Disregarded";
                }
            }
            case 2 -> {
                JOptionPane.showMessageDialog(null,
                        "Message successfully stored");
                return "stored";

            }
            default -> {
                JOptionPane.showMessageDialog(null, "Invalid option!");

                return "Disregarded";
            }

        }
    }

    // FIX: no longer prints to the console; the text is shown with JOptionPane.
    // Order changed to the one required: Message ID, Message Hash, Recipient, Message.
    public String printMessages() {

        StringBuilder allMessages = new StringBuilder();

        for (int i = 0; i < sentMessages.size(); i++) {

            int messageNumber = i + 1;

            allMessages.append("\n==============================");
            allMessages.append("\nMessage Number: ").append(messageNumber);
            allMessages.append("\nMessage ID: ").append(messageIdsList.get(i));
            allMessages.append("\nMessage Hash: ").append(msgHash.get(i));
            allMessages.append("\nRecipient: ").append(recipientNumber.get(i));
            allMessages.append("\nMessage: ").append(sentMessages.get(i));
            allMessages.append("\nStatus: ").append(messageStatus.get(i));
            allMessages.append("\n");
        }

        return allMessages.toString();
    }

    public void messageManagementMenu() {

        while (true) {

            String option = JOptionPane.showInputDialog(null,
                    "1. Show disregarded messages\n"
                    + "2. Show all stored messages\n"
                    + "3. Delete a message by Hash ID\n"
                    + "4. Display full report\n"
                    + "5. Search for a message\n"
                    + "6. Display the longest message\n"
                    + "7. Back to menu\n\n"
                    + "Choose an option (1-7): ",
                    "Message Management",
                    JOptionPane.QUESTION_MESSAGE);

            if (option == null) {
                return;
            }

            switch (option.trim()) {
                case "1" -> showDisregardedMessages();
                case "2" -> showAllStoredMessages();
                case "3" -> deleteMessageByHash();
                case "4" -> displayFullReport();
                case "5" -> searchMessageByHashID();
                case "6" -> findLongestMessage();
                case "7" -> {
                    return;
                }
                default -> JOptionPane.showMessageDialog(null,
                        "Invalid choice. Please enter a number from 1 to 7.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ADDED: the fourth main menu option "Stored Messages" (Part 3, point 2 a-f)
    public void storedMessagesMenu() {

        while (true) {

            String option = JOptionPane.showInputDialog(null,
                    "1. Display sender and recipient of all stored messages\n"
                    + "2. Display the longest stored message\n"
                    + "3. Search for a message ID\n"
                    + "4. Search all messages for a recipient\n"
                    + "5. Delete a message using the message hash\n"
                    + "6. Display report of all stored messages\n"
                    + "7. Back to menu\n\n"
                    + "Choose an option (1-7): ",
                    "Stored Messages",
                    JOptionPane.QUESTION_MESSAGE);

            if (option == null) {
                return;
            }

            switch (option.trim()) {
                case "1" -> showSendersAndRecipients();
                case "2" -> {
                    int index = indexOfLongest("stored");
                    if (index == -1) {
                        JOptionPane.showMessageDialog(null, "No stored messages to analyze.");
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Longest stored message:\n" + sentMessages.get(index),
                                "Longest stored message",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case "3" -> {
                    String id = JOptionPane.showInputDialog(null,
                            "Enter the message ID to search: ",
                            "Search by Message ID",
                            JOptionPane.QUESTION_MESSAGE);
                    if (id != null) {
                        JOptionPane.showMessageDialog(null,
                                searchByMessageID(id),
                                "Search result",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                case "4" -> {
                    String recipient = JOptionPane.showInputDialog(null,
                            "Enter the recipient cell number: ",
                            "Search by recipient",
                            JOptionPane.QUESTION_MESSAGE);
                    if (recipient != null) {
                        ArrayList<String> found = getMessagesByRecipient(recipient);
                        if (found.isEmpty()) {
                            JOptionPane.showMessageDialog(null,
                                    "No messages found for " + recipient.trim());
                        } else {
                            showText("Messages for " + recipient.trim(),
                                    String.join("\n", found));
                        }
                    }
                }
                case "5" -> deleteMessageByHash();
                case "6" -> showText("Stored Messages Report", buildReport("stored"));
                case "7" -> {
                    return;
                }
                default -> JOptionPane.showMessageDialog(null,
                        "Invalid choice. Please enter a number from 1 to 7.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // FIX: now uses JOptionPane, shows the real length and the right labels
    public void findLongestMessage() {
        int index = indexOfLongest(null);

        if (index == -1) {
            JOptionPane.showMessageDialog(null,
                    "No sent message to analyze the longest",
                    "Longest message",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Length: " + sentMessages.get(index).length() + " characters long\n"
                + "Hash: " + msgHash.get(index) + "\n"
                + "Message ID: " + messageIdsList.get(index) + "\n"
                + "Sender: " + senders.get(index) + "\n"
                + "Recipient: " + recipientNumber.get(index) + "\n"
                + "Message: " + sentMessages.get(index),
                "LONGEST MESSAGE SENT",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ADDED: index of the longest message; filter = status ("sent"/"stored") or null for all
    public int indexOfLongest(String filter) {
        int maxLength = -1;
        int index = -1;

        for (int i = 0; i < sentMessages.size(); i++) {
            if (filter != null && !messageStatus.get(i).equalsIgnoreCase(filter)) {
                continue;
            }
            int currentLength = sentMessages.get(i).length();

            if (currentLength > maxLength) {
                maxLength = currentLength;
                index = i;
            }
        }
        return index;
    }

    // ADDED: text of the longest message (used by unit tests)
    public String getLongestMessage() {
        int index = indexOfLongest(null);
        return index == -1 ? "" : sentMessages.get(index);
    }

    // ADDED: all messages with the "sent" flag (Sent Messages array)
    public ArrayList<String> getSentMessages() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < sentMessages.size(); i++) {
            if (messageStatus.get(i).equalsIgnoreCase("sent")) {
                result.add(sentMessages.get(i));
            }
        }
        return result;
    }

    // ADDED: search for a message ID and return its recipient and message
    public String searchByMessageID(String id) {
        int index = messageIdsList.indexOf(id.trim());
        if (index == -1) {
            return "No message exists with this Message ID: " + id.trim();
        }
        return "Recipient: " + recipientNumber.get(index)
                + "\nMessage: " + sentMessages.get(index);
    }

    // ADDED: all sent or stored messages for one recipient
    public ArrayList<String> getMessagesByRecipient(String recipient) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < sentMessages.size(); i++) {
            String status = messageStatus.get(i);
            if (recipientNumber.get(i).equals(recipient.trim())
                    && (status.equalsIgnoreCase("sent") || status.equalsIgnoreCase("stored"))) {
                result.add(sentMessages.get(i));
            }
        }
        return result;
    }

    // ADDED: sender and recipient of every stored message
    public void showSendersAndRecipients() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < sentMessages.size(); i++) {
            if (messageStatus.get(i).equalsIgnoreCase("stored")) {
                count++;
                sb.append(count).append(". Sender: ").append(senders.get(i))
                        .append("  |  Recipient: ").append(recipientNumber.get(i)).append("\n");
            }
        }
        if (count == 0) {
            JOptionPane.showMessageDialog(null, "No stored messages to display");
            return;
        }
        showText("Stored message senders and recipients", sb.toString());
    }

    // FIX: now uses JOptionPane and the number is really displayed (was the text "i + 1")
    public void showDisregardedMessages() {

        if (disregardedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No deleted message yet.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < disregardedMessages.size(); i++) {
            sb.append(i + 1).append(". ").append(disregardedMessages.get(i)).append("\n");
        }
        showText("DELETED MESSAGES", sb.toString());
    }

    // FIX: now uses JOptionPane
    public void showAllStoredMessages() {

        if (storedMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored messages to display");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < storedMessages.size(); i = i + 1) {
            sb.append(i + 1).append(". ").append(storedMessages.get(i)).append("\n");
        }
        showText("All stored messages", sb.toString());
    }

    public void deleteMessageByHash() {

        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No sent messages to delete",
                    "Delete",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String hashId;

        while (true) {

            hashId = JOptionPane.showInputDialog(null,
                    "Enter the hash ID to delete: ",
                    "Delete by Hash ID",
                    JOptionPane.QUESTION_MESSAGE);

            if (hashId == null) {
                return;
            }

            if (hashId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Hash ID cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            break;
        }

        hashId = hashId.trim().toUpperCase();

        int index = msgHash.indexOf(hashId);

        if (index == -1) {
            JOptionPane.showMessageDialog(null,
                    "No message exists with this Hash ID: " + hashId,
                    "Not found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Do you want to delete this message?\n\n"
                + "Message: " + sentMessages.get(index),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null,
                    "Message kept",
                    "Cancelled",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // The removing itself is done in removeMessageByHash so it can be unit tested
        String result = removeMessageByHash(hashId);

        JOptionPane.showMessageDialog(null,
                result,
                "Deleted",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ADDED: removes a message by hash from every array and from the JSON file
    public String removeMessageByHash(String hashId) {

        String hash = hashId.trim().toUpperCase();
        int index = msgHash.indexOf(hash);

        if (index == -1) {
            return "No message exists with this Hash ID: " + hash;
        }

        String text = sentMessages.get(index);
        String status = messageStatus.get(index);

        if (status.equals("sent")) {
            numMessageSent--;
        }
        if (status.equals("stored")) {
            storedMessages.remove(text);
        }

        disregardedMessages.add(text);
        messageIds.remove(messageIdsList.get(index));
        sentMessages.remove(index);
        senders.remove(index);
        recipientNumber.remove(index);
        messageIdsList.remove(index);
        msgHash.remove(index);
        messageStatus.remove(index);

        if (status.equals("stored")) {
            storeMessage(); // rewrite the JSON file without the deleted message
        }

        return "Message: \"" + text + "\" successfully deleted.";
    }

    // FIX: now uses JOptionPane (the table is built in buildReport)
    public void displayFullReport() {

        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sent messages to display.");
            return;
        }
        showText("Full Message Report", buildReport(null));
    }

    // ADDED: builds the report text; filter = "sent"/"stored" or null for every message
    public String buildReport(String filter) {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s | %-10s | %-14s | %-12s | %-18s | %-10s | %s%n",
                "No.", "Sender", "Recipient", "Message ID", "Message Hash", "Status", "Message"));

        int count = 0;
        int storedCount = 0;
        for (int i = 0; i < sentMessages.size(); i++) {
            String status = messageStatus.get(i);
            if (status.equals("stored")) {
                storedCount++;
            }
            if (filter != null && !status.equalsIgnoreCase(filter)) {
                continue;
            }
            count++;
            sb.append(String.format("%-4d | %-10s | %-14s | %-12s | %-18s | %-10s | %s%n",
                    count,
                    senders.get(i),
                    recipientNumber.get(i),
                    messageIdsList.get(i),
                    msgHash.get(i),
                    status,
                    sentMessages.get(i)));
        }

        if (count == 0) {
            sb.append("\nNo messages to display.\n");
        }

        sb.append("\nTotal sent messages: ").append(numMessageSent);
        sb.append("\nTotal disregarded messages: ").append(disregardedMessages.size());
        sb.append("\nTotal stored messages: ").append(storedCount);
        return sb.toString();
    }

    public void searchMessageByHashID() {

        if (sentMessages.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "No sent messages to search",
                    "Search",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String hashId;

        while (true) {

            hashId = JOptionPane.showInputDialog(null,
                    "Enter the hash ID to search: ",
                    "Search by Hash ID",
                    JOptionPane.QUESTION_MESSAGE);

            if (hashId == null) {
                return;
            }

            if (hashId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Hash ID cannot be empty",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                continue;
            }

            break;
        }

        hashId = hashId.trim().toUpperCase();

        int index = msgHash.indexOf(hashId);

        if (index == -1) {
            JOptionPane.showMessageDialog(null,
                    "No message exists with this Hash ID: " + hashId,
                    "Not found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String details = "========Message Details========\n"
                + "Hash ID: " + msgHash.get(index) + "\n"
                + "Message ID: " + messageIdsList.get(index) + "\n"
                + "From: " + senders.get(index) + "\n"
                + "To: " + recipientNumber.get(index) + "\n"
                + "Message: " + sentMessages.get(index) + "\n"
                + "Length: " + sentMessages.get(index).length()
                + " characters long";

        JOptionPane.showMessageDialog(null,
                details,
                "Message Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ADDED: adds a message without dialogs (used by the unit tests and test data).
    // flag = "sent", "stored" or "disregard". Returns the message hash.
    public String addMessage(String recipient, String text, String flag, String sender) {

        String id = generateMessageID();
        int messageNumber = sentMessages.size() + 1;
        String hash = createMessageHash(id, messageNumber, text);

        if (flag.equalsIgnoreCase("disregard")) {
            disregardedMessages.add(text);
            messageIds.remove(id);
            return hash;
        }

        String status = flag.equalsIgnoreCase("stored") ? "stored" : "sent";

        msgHash.add(hash);
        sentMessages.add(text);
        messageIdsList.add(id);
        recipientNumber.add(recipient);
        senders.add(sender);
        messageStatus.add(status);

        if (status.equals("sent")) {
            numMessageSent++;
        } else {
            storedMessages.add(text);
            storeMessage();
        }
        return hash;
    }

    // ===================== JSON STORAGE =====================

    // ADDED (Part 2 requirement): writes every stored message to a JSON file.
    // Written by hand so no extra library is needed. The file is rewritten each time.
    public void storeMessage() {

        StringBuilder sb = new StringBuilder("[\n");
        boolean first = true;

        for (int i = 0; i < sentMessages.size(); i++) {
            if (!messageStatus.get(i).equals("stored")) {
                continue;
            }
            if (!first) {
                sb.append(",\n");
            }
            first = false;
            sb.append("  {")
                    .append("\"messageId\": \"").append(escapeJson(messageIdsList.get(i))).append("\", ")
                    .append("\"messageHash\": \"").append(escapeJson(msgHash.get(i))).append("\", ")
                    .append("\"sender\": \"").append(escapeJson(senders.get(i))).append("\", ")
                    .append("\"recipient\": \"").append(escapeJson(recipientNumber.get(i))).append("\", ")
                    .append("\"message\": \"").append(escapeJson(sentMessages.get(i))).append("\"")
                    .append("}");
        }
        sb.append("\n]\n");

        try {
            Files.write(Paths.get(jsonFile), sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not save the JSON file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ADDED (Part 3 requirement): reads the JSON file back into the arrays.
    // Call once when the program starts. Messages already in memory are skipped.
    public void loadStoredMessages() {

        Path path = Paths.get(jsonFile);
        if (!Files.exists(path)) {
            return;
        }

        String content;
        try {
            content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Could not read the JSON file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // one JSON string value, allowing escaped characters
        String str = "\"((?:[^\"\\\\]|\\\\.)*)\"";
        String sep = "\\s*,\\s*";
        Pattern pattern = Pattern.compile("\\{\\s*"
                + "\"messageId\"\\s*:\\s*" + str + sep
                + "\"messageHash\"\\s*:\\s*" + str + sep
                + "\"sender\"\\s*:\\s*" + str + sep
                + "\"recipient\"\\s*:\\s*" + str + sep
                + "\"message\"\\s*:\\s*" + str + "\\s*\\}");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String id = unescapeJson(matcher.group(1));
            if (messageIdsList.contains(id)) {
                continue;
            }
            String text = unescapeJson(matcher.group(5));

            messageIdsList.add(id);
            messageIds.add(id);
            msgHash.add(unescapeJson(matcher.group(2)));
            senders.add(unescapeJson(matcher.group(3)));
            recipientNumber.add(unescapeJson(matcher.group(4)));
            sentMessages.add(text);
            messageStatus.add("stored");
            storedMessages.add(text);
        }
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String unescapeJson(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    default -> sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ADDED: shows long text in a scrollable, fixed-width JOptionPane
    private void showText(String title, String text) {
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(800, 320));
        JOptionPane.showMessageDialog(null, scroll, title, JOptionPane.INFORMATION_MESSAGE);
    }
}