package application;

import java.time.LocalDateTime;
import java.util.UUID;

public class PrivateMessage {
    private UUID id;
    private String sender;
    private String recipient;
    private String subject;
    private String message;
    private LocalDateTime timestamp;

    public PrivateMessage(UUID id, String sender, String recipient, String subject, String message, LocalDateTime timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.timestamp = timestamp;
    }

    // constructor for new messages
    public PrivateMessage(String sender, String recipient, String subject, String message) {
        this(UUID.randomUUID(), sender, recipient, subject, message, LocalDateTime.now());
    }

    public UUID getId() {
        return id;
    }
    
    public String getSender() {
        return sender;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return subject + " (From: " + sender + " at " + timestamp.toLocalTime().withSecond(0).withNano(0) + ")";
    }
}
