package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UpdateReviewPage {

    private final DatabaseHelper databaseHelper;
    private final User currentUser;
    private final Review reviewToUpdate;
    private final Runnable refreshCallback;

    public UpdateReviewPage(DatabaseHelper databaseHelper, User currentUser, Review reviewToUpdate, Runnable refreshCallback) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        this.reviewToUpdate = reviewToUpdate;
        this.refreshCallback = refreshCallback;
    }

    public void show(CustomTrackedStage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Update Review");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextArea reviewTextArea = new TextArea(reviewToUpdate.getReviewText());
        reviewTextArea.setPrefWidth(500);
        reviewTextArea.setPrefHeight(200);

        Button updateButton = new Button("Update");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            String newText = reviewTextArea.getText().trim();
            if (newText.isEmpty()) {
                showAlert("Review text cannot be empty.");
                return;
            }
            // update the existing review
            ReviewManager.updateReview(reviewToUpdate, newText, databaseHelper);
            System.out.println(reviewToUpdate + " - " + newText);
            refreshCallback.run();
            primaryStage.goBack();
        });

        cancelButton.setOnAction(e -> primaryStage.goBack());

        HBox buttonRow = new HBox(10, updateButton, cancelButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, reviewTextArea, buttonRow);
        layout.setAlignment(Pos.CENTER);
        root.setCenter(layout);

        Scene scene = new Scene(root, 600, 500);
        primaryStage.showScene(scene);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notice");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
