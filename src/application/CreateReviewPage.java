package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class CreateReviewPage {

    private final DatabaseHelper databaseHelper;
    private final QuestionManager questionManager;
    private final User currentUser;
    private final Runnable refreshCallback; // to refresh the review list on the home page

    public CreateReviewPage(DatabaseHelper databaseHelper, QuestionManager questionManager, User currentUser, Runnable refreshCallback) {
        this.databaseHelper = databaseHelper;
        this.questionManager = questionManager;
        this.currentUser = currentUser;
        this.refreshCallback = refreshCallback;
    }

    public void show(CustomTrackedStage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Create New Review");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ListView to allow selection of an answers to review
        Label answerLabel = new Label("Select Answer to Review:");
        answerLabel.setStyle("-fx-font-size: 14px;");
        ListView<Answer> answerListView = new ListView<>();
        List<Answer> allAnswers = questionManager.getAnswers();
        answerListView.setItems(FXCollections.observableArrayList(allAnswers));
        answerListView.setPrefHeight(200);
        answerListView.setPrefWidth(600);
        // custom cell to show question title, answer snippet, and author
        answerListView.setCellFactory(lv -> new ListCell<Answer>() {
            @Override
            protected void updateItem(Answer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String questionTitle = (item.getQuestion() != null) ? item.getQuestion().getTitle() : "Unknown Question";
                    String answerSnippet = item.getAnswerText();
                    if (answerSnippet.length() > 100) {
                        answerSnippet = answerSnippet.substring(0, 100) + "...";
                    }
                    setText("Question: " + questionTitle + "\nAnswer: " + answerSnippet + "\nBy: " + item.getAuthor());
                    setStyle("-fx-padding: 10px; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
                }
            }
        });

        // TextArea for entering review
        Label reviewLabel = new Label("Review Text:");
        reviewLabel.setStyle("-fx-font-size: 14px;");
        TextArea reviewTextArea = new TextArea();
        reviewTextArea.setPromptText("Enter your review here...");
        reviewTextArea.setPrefWidth(600);
        reviewTextArea.setPrefHeight(200);

        Button submitButton = new Button("Submit Review");
        Button cancelButton = new Button("Cancel");

        submitButton.setOnAction(e -> {
            Answer selectedAnswer = answerListView.getSelectionModel().getSelectedItem();
            String reviewText = reviewTextArea.getText().trim();
            if (selectedAnswer == null || reviewText.isEmpty()) {
                showAlert("Please select an answer and enter review text.");
                return;
            }
            // Create a new review and add it with ReviewManager
            Review newReview = new Review(reviewText, currentUser.getUserName(), selectedAnswer.getId());
            ReviewManager.addReview(newReview, databaseHelper);
            refreshCallback.run();
            primaryStage.goBack();
        });

        cancelButton.setOnAction(e -> primaryStage.goBack());

        HBox buttonRow = new HBox(10, submitButton, cancelButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, titleLabel, answerLabel, answerListView, reviewLabel, reviewTextArea, buttonRow);
        layout.setAlignment(Pos.CENTER);
        root.setCenter(layout);

        Scene scene = new Scene(root, 700, 600);
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
