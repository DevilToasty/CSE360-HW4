package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.util.List;
import java.util.UUID;

public class StudentHomePage {

    private Scene scene;
    private final DatabaseHelper databaseHelper;
    private final QuestionManager questionManager;
    private final User currentUser;

    public StudentHomePage(DatabaseHelper databaseHelper, QuestionManager questionManager, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.questionManager = questionManager;
        this.currentUser = currentUser;
    }

    public void show(CustomTrackedStage primaryStage) {
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: #ffffff;");

        Button backButton = BackButton.createBackButton(primaryStage);
        BorderPane.setMargin(backButton, new Insets(10));
        BorderPane.setAlignment(backButton, Pos.TOP_LEFT);
        borderPane.setTop(backButton);
        
        Button discussionButton = new Button("View Discussion");
		discussionButton.setOnAction(a -> {
		       new DiscussionView(databaseHelper, questionManager, currentUser).show(primaryStage);     
		});
		
		Button settingsButton = new Button("View Settings");
		settingsButton.setOnAction(a -> {
		       new ManageDiscussionPage(databaseHelper, questionManager, currentUser).show(primaryStage);     
		});
		
		Button userReviewerButton = new Button("Reviewer Manager");
		userReviewerButton.setOnAction(a -> {
		       new StudentReviewerInterface(databaseHelper, questionManager, currentUser).show(primaryStage);     
		});
		
		Button privateMessageButton = new Button("Private Messaging");
		privateMessageButton.setOnAction(a -> {
		       new PrivateMessagingPage(databaseHelper, currentUser).show(primaryStage);     
		});

        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: #ddd; -fx-border-width: 1px;");

        centerBox.getChildren().addAll(
        	discussionButton,
        	settingsButton,
        	userReviewerButton,
        	privateMessageButton
        );
        borderPane.setCenter(centerBox);
        scene = new Scene(borderPane, 800, 700);
        primaryStage.showScene(scene);
    }
}
