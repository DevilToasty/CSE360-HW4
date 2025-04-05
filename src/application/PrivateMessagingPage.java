package application;

import databasePart1.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class PrivateMessagingPage {

    private Scene scene;
    private final DatabaseHelper databaseHelper;
    private final User currentUser;

    public PrivateMessagingPage(DatabaseHelper databaseHelper, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
    }

    public void show(CustomTrackedStage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #f9f9f9;");

        Button backButton = BackButton.createBackButton(primaryStage);
        Label titleLabel = new Label("Private Messaging");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane headerPane = new BorderPane();
        headerPane.setLeft(backButton);
        headerPane.setCenter(titleLabel);
        BorderPane.setAlignment(backButton, Pos.CENTER_LEFT);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        headerPane.setPadding(new Insets(10));
        root.setTop(headerPane);

        TabPane tabPane = new TabPane();
        
        // Inbox Tab
        Tab inboxTab = new Tab("Inbox");
        inboxTab.setClosable(false);
        inboxTab.setContent(createInboxPane());
        
        // Compose Tab
        Tab composeTab = new Tab("Compose Message");
        composeTab.setClosable(false);
        composeTab.setContent(createComposePane());
        
        tabPane.getTabs().addAll(inboxTab, composeTab);
        root.setCenter(tabPane);

        scene = new Scene(root, 900, 700);
        primaryStage.showScene(scene);
    }
    
    // Inbox pane
    private VBox createInboxPane() {
        VBox inboxPane = new VBox(10);
        inboxPane.setPadding(new Insets(10));
        inboxPane.setAlignment(Pos.TOP_CENTER);
        
        Label inboxLabel = new Label("Inbox Messages");
        inboxLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        ListView<PrivateMessage> inboxListView = new ListView<>();
        ObservableList<PrivateMessage> messages = FXCollections.observableArrayList(
                databaseHelper.getPrivateMessagesForUser(currentUser.getUserName())
        );
        inboxListView.setItems(messages);
        inboxListView.setPrefHeight(400);
        
        // Display selected message details.
        TextArea messageDetails = new TextArea();
        messageDetails.setEditable(false);
        messageDetails.setWrapText(true);
        messageDetails.setPrefHeight(150);
        
        inboxListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                messageDetails.setText("From: " + newVal.getSender() + "\nSubject: " + newVal.getSubject() + "\n\n" +
                        newVal.getMessage() + "\n\nReceived: " + newVal.getTimestamp());
            }
        });
        
        inboxPane.getChildren().addAll(inboxLabel, inboxListView, messageDetails);
        return inboxPane;
    }
    
    // Create Compose pane with a selection tool for recipients.
    private VBox createComposePane() {
        VBox composePane = new VBox(10);
        composePane.setPadding(new Insets(10));
        composePane.setAlignment(Pos.TOP_CENTER);
        
        Label composeLabel = new Label("Compose New Message");
        composeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField recipientField = new TextField();
        recipientField.setPromptText("Select Recipient");
        recipientField.setEditable(false);
        
        // Button to open a dialog for recipient selection
        Button selectRecipientButton = new Button("Select Recipient");
        selectRecipientButton.setOnAction(e -> {
            try {
                List<User> allUsers = databaseHelper.getUsers();
                
                // Exclude the current user
                List<User> recipients = allUsers.stream()
                        .filter(u -> !u.getUserName().equalsIgnoreCase(currentUser.getUserName()))
                        .collect(Collectors.toList());
                if (recipients.isEmpty()) {
                    showAlert("No users available.");
                    return;
                }
                // Convert to usernames
                List<String> recipientNames = recipients.stream()
                        .map(User::getUserName)
                        .collect(Collectors.toList());
                ChoiceDialog<String> dialog = new ChoiceDialog<>(recipientNames.get(0), recipientNames);
                dialog.setTitle("Select Recipient");
                dialog.setHeaderText("Choose a recipient for your message:");
                dialog.setContentText("Recipient:");
                dialog.showAndWait().ifPresent(selected -> {
                    recipientField.setText(selected);
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert("Error retrieving users.");
            }
        });
        
        HBox recipientBox = new HBox(10, recipientField, selectRecipientButton);
        recipientBox.setAlignment(Pos.CENTER);
        
        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject");
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here...");
        messageArea.setPrefHeight(200);
        
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String recipient = recipientField.getText().trim();
            String subject = subjectField.getText().trim();
            String messageText = messageArea.getText().trim();
            if (recipient.isEmpty() || subject.isEmpty() || messageText.isEmpty()) {
                showAlert("Please fill in recipient, subject, and message.");
                return;
            }
            PrivateMessage msg = new PrivateMessage(currentUser.getUserName(), recipient, subject, messageText);
            boolean success = databaseHelper.insertPrivateMessage(msg);
            if (success) {
                showAlert("Message sent.");
                recipientField.clear();
                subjectField.clear();
                messageArea.clear();
            } else {
                showAlert("Failed to send message.");
            }
        });
        
        composePane.getChildren().addAll(composeLabel, recipientBox, subjectField, messageArea, sendButton);
        return composePane;
    }
    
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Private Messaging");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
