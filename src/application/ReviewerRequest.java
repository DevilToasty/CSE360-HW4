package application;
import java.util.UUID;

public class ReviewerRequest {
    private UUID id;
    private String username;
    private Boolean isApproved;
    
    public ReviewerRequest(UUID id, String username, Boolean isApproved) {
        this.id = id;
        this.username = username;
        this.isApproved = isApproved;
    }
    
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public Boolean getIsApproved() { return isApproved; }
    
    @Override
    public String toString() {
        return username;
    }
}
