package application;

import java.time.LocalDateTime;
import java.util.UUID;

public class Review {
    private UUID id;
    private String reviewText;
    private String reviewer;
    private UUID answerId;
    private LocalDateTime timestamp;
    private int privateFeedbackCount;
    private UUID previousReviewId;

    // Constructor for a new review
    public Review(String reviewText, String reviewer, UUID answerId) {
        this.id = UUID.randomUUID();
        this.reviewText = reviewText;
        this.reviewer = reviewer;
        this.answerId = answerId;
        this.timestamp = LocalDateTime.now();
        this.privateFeedbackCount = 0;
        this.previousReviewId = null;
    }
    
    // Constructor for an updated review
    public Review(String reviewText, String reviewer, UUID answerId, UUID previousReviewId) {
        this.id = UUID.randomUUID();
        this.reviewText = reviewText;
        this.reviewer = reviewer;
        this.answerId = answerId;
        this.timestamp = LocalDateTime.now();
        this.privateFeedbackCount = 0;
        this.previousReviewId = previousReviewId;
    }
    
    public UUID getId() { return id; }
    public String getReviewText() { return reviewText; }
    public String getReviewer() { return reviewer; }
    public UUID getAnswerId() { return answerId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getPrivateFeedbackCount() { return privateFeedbackCount; }
    public UUID getPreviousReviewId() { return previousReviewId; }

    public void setReviewText(String newText) {
        this.reviewText = newText;
    }
    
    public void setTimestamp(LocalDateTime newTimestamp) {
        this.timestamp = newTimestamp;
    }
    
    public void setPrivateFeedbackCount(int count) {
        this.privateFeedbackCount = count;
    }
    
    public void setPreviousReviewId(UUID previousReviewId) {
        this.previousReviewId = previousReviewId;
    }
    
    @Override
    public String toString() {
        String snippet = reviewText.length() > 50 ? reviewText.substring(0, 47) + "..." : reviewText;
        return snippet + " (Feedback: " + privateFeedbackCount + ")";
    }
}
