package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class ReviewerProfilePage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    
    private String reviewerExperience = "";

    public ReviewerProfilePage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(CustomTrackedStage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f9f9f9;");

        Button backButton = BackButton.createBackButton(primaryStage);
        Label headerLabel = new Label("Reviewer Profile");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        StackPane centeredPane = new StackPane(headerLabel);
        centeredPane.setMaxWidth(Double.MAX_VALUE);
        HBox header = new HBox(10, backButton, centeredPane);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));
        HBox.setHgrow(centeredPane, Priority.ALWAYS);
        root.setTop(header);

        // Experience section
        Label expLabel = new Label("Your Experience:");
        TextArea expTextArea = new TextArea(reviewerExperience);
        expTextArea.setPromptText("Enter details about your reviewing experience...");
        expTextArea.setPrefHeight(100);
        expTextArea.setPrefWidth(600);
        Button saveExpButton = new Button("Save Experience");
        saveExpButton.setOnAction(e -> {
            reviewerExperience = expTextArea.getText();
            showAlert("Experience updated.");
        });
        
        // Reviews list section
        Label reviewsLabel = new Label("My Reviews (private feedback count shown):");
        ListView<Review> reviewsListView = new ListView<>();
        List<Review> myReviews = ReviewManager.getReviewsByReviewer(currentUser.getUserName());
        reviewsListView.setItems(FXCollections.observableArrayList(myReviews));
        reviewsListView.setPrefSize(600, 300);
        
        // Private Feedback section
        Label feedbackLabel = new Label("Private Feedback Received:");
        ListView<PrivateFeedback> feedbackListView = new ListView<>();
        List<PrivateFeedback> feedback = databaseHelper.getPrivateFeedbackForReviewer(currentUser.getUserName());
        feedbackListView.setItems(FXCollections.observableArrayList(feedback));
        feedbackListView.setPrefSize(600, 200);
        
        VBox centerLayout = new VBox(15, expLabel, expTextArea, saveExpButton, reviewsLabel, reviewsListView, feedbackLabel, feedbackListView);
        centerLayout.setAlignment(Pos.CENTER);
        root.setCenter(centerLayout);
        
        Scene scene = new Scene(root, 800, 700);
        primaryStage.showScene(scene);
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
