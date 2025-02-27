package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;

public class DiscussionView {

    private Scene scene;
    private final DatabaseHelper databaseHelper;
    private final QuestionManager questionManager;
    private final User currentUser;
    
    private CheckBox unansweredCheckbox;
    private CheckBox unapprovedCheckbox;
    private CheckBox trustedReviewersCheckbox;
    private CheckBox myQuestionsCheckbox;
    private TextField searchField;
    private Button searchButton;
    
    private VBox questionsContainer;
    
    private CustomTrackedStage primaryStage;

    public DiscussionView(DatabaseHelper databaseHelper, QuestionManager questionManager, User currentUser) {
        this.databaseHelper = databaseHelper;
        this.questionManager = questionManager;
        this.currentUser = currentUser;
    }
    
    public void show(CustomTrackedStage primaryStage) {
        this.primaryStage = primaryStage;
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f9f9f9;");
        
        // header with back, title, and create buttons
        HBox headerRow = new HBox(10);
        headerRow.setPadding(new Insets(10));
        headerRow.setAlignment(Pos.CENTER);
        
        Button backButton = BackButton.createBackButton(primaryStage);
        Label titleLabel = new Label("Discussion Page");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Button createButton = new Button("Create");
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createButton.setOnAction(e -> {
            new CreateQuestionPage(databaseHelper, questionManager, currentUser, this::updateQuestionList)
                .show(primaryStage);
        });
        
        headerRow.getChildren().addAll(backButton, titleLabel, createButton);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        // filter row with checkboxes and search box
        HBox filterRow = new HBox(15);
        filterRow.setPadding(new Insets(5, 10, 10, 10));
        filterRow.setAlignment(Pos.CENTER_LEFT);
        
        unansweredCheckbox = new CheckBox("Unanswered");
        unapprovedCheckbox = new CheckBox("Unapproved");
        trustedReviewersCheckbox = new CheckBox("Trusted Reviewers");
        myQuestionsCheckbox = new CheckBox("My Questions");
        
        // event handlers to update the list when filters change
        unansweredCheckbox.setOnAction(e -> updateQuestionList());
        unapprovedCheckbox.setOnAction(e -> updateQuestionList());
        trustedReviewersCheckbox.setOnAction(e -> updateQuestionList());
        myQuestionsCheckbox.setOnAction(e -> updateQuestionList());
        
        // search bar
        searchField = new TextField();
        searchField.setPromptText("Search...");
        searchButton = new Button("Search");
        searchButton.setOnAction(e -> updateQuestionList());
        
        filterRow.getChildren().addAll(
                unansweredCheckbox,
                unapprovedCheckbox,
                trustedReviewersCheckbox,
                myQuestionsCheckbox
        );
        
        // spacer to push the search bar to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        filterRow.getChildren().addAll(spacer, searchField, searchButton);
        
        VBox topContainer = new VBox(5);
        topContainer.getChildren().addAll(headerRow, filterRow);
        root.setTop(topContainer);
        
        // questions container
        questionsContainer = new VBox(10);
        questionsContainer.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(questionsContainer);
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);
        
        scene = new Scene(root, 900, 700);
        primaryStage.showScene(scene);
        
        updateQuestionList();
    }
    
    private void updateQuestionList() {
        questionsContainer.getChildren().clear();
        List<Question> allQuestions = questionManager.getAllQuestions();
        List<Question> filtered = new ArrayList<>();
        
        String searchText = searchField.getText().trim().toLowerCase();
        
        for (Question q : allQuestions) {
            boolean matches = true;
            
            // filter by unanswered
            if (unansweredCheckbox.isSelected() && !q.getAnswers().isEmpty()) {
                matches = false;
            }
            
            // filter by unapproved
            if (unapprovedCheckbox.isSelected() && !q.getApprovedSolutions().isEmpty()) {
                matches = false;
            }
            
            // filter by trusted reviewers
            if (trustedReviewersCheckbox.isSelected()) {
                boolean found = false;
                for (Answer a : q.getAnswers()) {
                    if (currentUser.getApprovedReviewers().containsKey(a.getAuthor())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    matches = false;
                }
            }
            
            // filter by My Questions
            if (myQuestionsCheckbox.isSelected() && !q.getAuthor().equals(currentUser.getUserName())) {
                matches = false;
            }
            
            // apply search filter if text is entered
            
            // FAISAL CODE
            if (!searchText.isEmpty()) {
                String title = q.getTitle().toLowerCase();
                String questionText = q.getQuestionText().toLowerCase();
                
                boolean questionContainsSearch = title.contains(searchText) || questionText.contains(searchText);
                boolean answerContainsSearch = false;
                for (Answer ans : q.getAnswers()) {
                    if (ans.getAnswerText().toLowerCase().contains(searchText)) {
                        answerContainsSearch = true;
                        break;
                    }
                }
                if (!questionContainsSearch && !answerContainsSearch) {
                    matches = false;
                }
            }
            
            if (matches) {
                filtered.add(q);
            }
        }
        
        // display filtered questions
        for (Question q : filtered) {
            QuestionView qView = new QuestionView(q);
        	qView.setOnMouseClicked(e -> {
        	    new QuestionDetailView(q, questionManager, databaseHelper, currentUser, this::updateQuestionList) // updates page when detail view is closed 
        	        .show(primaryStage);
        	});
            questionsContainer.getChildren().add(qView);
        }
    }
}
