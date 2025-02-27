package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class QuestionDetailView {
    
    private Scene scene;
    private Question question;
    private QuestionManager questionManager;
    private DatabaseHelper databaseHelper;
    private User currentUser;
    private Runnable onBack; // callback to refresh discussion view when going back
    
    private VBox answersContainer;    
    private VBox editQuestionContainer;
    private VBox questionReplyContainer;  
    
    private CustomTrackedStage primaryStage;
    
    private Label fullTextLabel;
    private Label errorLabel;
    
    public QuestionDetailView(Question question, QuestionManager questionManager, DatabaseHelper databaseHelper, User currentUser, Runnable onBack) {
        this.question = question;
        this.questionManager = questionManager;
        this.databaseHelper = databaseHelper;
        this.currentUser = currentUser;
        this.onBack = onBack;
    }
    
    // overloaded constructor if no callback is needed.
    public QuestionDetailView(Question question, QuestionManager questionManager, DatabaseHelper databaseHelper, User currentUser) {
        this(question, questionManager, databaseHelper, currentUser, null);
    }
    
    public void show(CustomTrackedStage primaryStage) {
        this.primaryStage = primaryStage;
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
       
        // Create header as a VBox containing an HBox for the top row.
        VBox header = new VBox(10);
        header.setPadding(new Insets(10));
        
        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        
        HBox secondRow = new HBox(10);
        secondRow.setAlignment(Pos.CENTER_LEFT);
        
        Button backButton = BackButton.createBackButton(primaryStage);
        backButton.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
            primaryStage.goBack();
        });
        
        topRow.getChildren().add(backButton);
        
        Label titleLabel = new Label(question.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button optionsButton = new Button("\u22EE");  // unicode ellipsis
        optionsButton.setLayoutX(10);
        optionsButton.setLayoutY(10);
        // style
        optionsButton.setStyle(
            "-fx-background-color: #e0e0e0; " +
            "-fx-border-color: #ccc; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 5px;"
        );
        
        editQuestionContainer = new VBox();

        // -- MENU --
        ContextMenu contextMenu = new ContextMenu();
        
        // if the current user is the question author, add an options button.
        if (currentUser.getUserName().equals(question.getAuthor())) {
	        
        	
        	// JASON EDIT FUNCTION //
        	MenuItem editQuestion = new MenuItem("Edit Question");
	        editQuestion.setOnAction(e -> {
	        	if (currentUser.getUserName().equals(question.getAuthor())) {
	                if (editQuestionContainer.getChildren().isEmpty()) {
	                    EditQuestionBox editBox = new EditQuestionBox(text -> {
	                        try {
	                            questionManager.editQuestiontext(question, text);
	                            refreshContent();
	                        } catch(Exception ex) {
	                            ex.printStackTrace();
	                        }
	                    });
	                    editQuestionContainer.getChildren().add(editBox);
	                } else {
	                    editQuestionContainer.getChildren().clear();
	                }
	            } else {
	                errorLabel.setText("You aren't the user who posted this question.");
	            }
	        });
	        
	        MenuItem deleteItem = new MenuItem("Delete Question");
	        deleteItem.setOnAction(e -> {
	            boolean success = questionManager.deleteQuestion(question.getId());
	            if (success) {
	            	System.out.println();
	                if (onBack != null) {
	                    onBack.run(); // refresh discussion view
	                }
	                primaryStage.goBack(); // navigate back to discussion page
	            } else {
	                errorLabel.setText("Error deleting question.");
	            }
	        });
	       
	        contextMenu.getItems().addAll(editQuestion, deleteItem);
        }
        
        MenuItem reportItem = new MenuItem("Report");
        reportItem.setOnAction(e -> {
            // to do
        });
        
        contextMenu.getItems().add(reportItem);
        optionsButton.setOnAction(e -> {
            contextMenu.show(optionsButton, Side.BOTTOM, 0, 0);
        });
        
        // -- MENU END --
        
        
            
        secondRow.getChildren().addAll(titleLabel, spacer, optionsButton);
        
        header.getChildren().addAll(topRow, secondRow);
        
        fullTextLabel = new Label(question.getQuestionText());
        fullTextLabel.setWrapText(true);
        fullTextLabel.setStyle("-fx-font-size: 14px;");
        
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
               
        questionReplyContainer = new VBox();
        Button replyButton = new Button("Reply");
        replyButton.setOnAction(e -> {
            if (questionReplyContainer.getChildren().isEmpty()) {
                ReplyBox replyBox = new ReplyBox(text -> {
                    try {
                        questionManager.createAnswer(currentUser.getUserName(), text, question);
                        refreshContent();
                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }
                });
                questionReplyContainer.getChildren().add(replyBox);
            } else {
                questionReplyContainer.getChildren().clear();
            }
        });
        
        VBox questionContainer = new VBox(10);
        questionContainer.getChildren().addAll(
            fullTextLabel, 
            errorLabel, 
            replyButton, 
            editQuestionContainer, 
            questionReplyContainer
        );
        
        answersContainer = new VBox(10);
        loadAnswers();
        ScrollPane answersScroll = new ScrollPane(answersContainer);
        answersScroll.setFitToWidth(true);
        
        VBox mainContent = new VBox(20);
        mainContent.getChildren().addAll(questionContainer, new Label("Answers:"), answersScroll);
        
        root.setTop(header);
        root.setCenter(mainContent);
        
        scene = new Scene(root, 900, 700);
        primaryStage.showScene(scene);
    }
    
    // loads (and refreshes) the list of answers
    private void loadAnswers() {
        answersContainer.getChildren().clear();
        for (Answer a : question.getAnswers()) {
            if (a.getParentAnswerId() == null) {
                AnswerView aView = new AnswerView(a, question, questionManager, currentUser, question.getAnswers(), this::refreshContent);
                answersContainer.getChildren().add(aView);
            }
        }
    }
    
    private void refreshContent() {
        questionManager.refreshQuestion(question);
        fullTextLabel.setText(question.getQuestionText());
        editQuestionContainer.getChildren().clear();
        questionReplyContainer.getChildren().clear();
        loadAnswers();
    }
}
