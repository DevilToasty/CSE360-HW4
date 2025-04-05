package application;

import java.time.LocalDateTime;
import java.util.UUID;

public class PrivateFeedback {
    private UUID id;
    private String fromUser;
    private String reviewer;
    private String feedback;
    private LocalDateTime timestamp;
    
    public PrivateFeedback(UUID id, String fromUser, String reviewer, String feedback, LocalDateTime timestamp) {
        this.id = id;
        this.fromUser = fromUser;
        this.reviewer = reviewer;
        this.feedback = feedback;
        this.timestamp = timestamp;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getFromUser() {
        return fromUser;
    }
    
    public String getReviewer() {
        return reviewer;
    }
    
    public String getFeedback() {
        return feedback;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return fromUser + ": " + feedback + " (" + timestamp + ")";
    }
}
