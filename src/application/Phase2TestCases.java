package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class Phase2TestCases {
    public static void main(String[] args) {
        runTests();
    }
    
    public static void runTests() {
        int testsRun = 0, testsPassed = 0;
        try {
            // Connect to the database
            DatabaseHelper db = new DatabaseHelper();
            db.connectToDatabase();
            
            // Initialize QuestionManager
            QuestionManager qm = new QuestionManager(db);
            
            // --- Test 1: Create a valid question ---
            testsRun++;
            try {
                qm.createQuestion("Tester1", "Test Q", 
                    "This is a valid test question containing more than ten words for testing purposes.");
                if(qm.getAllQuestions().size() >= 1) {
                    System.out.println("Test 1 (Create valid question) passed");
                    testsPassed++;
                } else {
                    System.out.println("Test 1 failed");
                }
            } catch(Exception e) {
                System.out.println("Test 1 failed: " + e.getMessage());
            }
            
            // --- Test 2: Create an invalid question (too few words) ---
            testsRun++;
            try {
                qm.createQuestion("Tester2", "Test Q", "Too short");
                System.out.println("Test 2 (Create invalid question) failed");
            } catch(IllegalArgumentException e) {
                System.out.println("Test 2 passed");
                testsPassed++;
            }
            
            // Get a question to work with
            Question q = qm.getAllQuestions().get(0);
            
            // --- Test 3: Create a valid answer (length requirement removed) ---
            testsRun++;
            int initialAns = q.getAnswers().size();
            try {
                qm.createAnswer("Responder1", 
                    "This is a valid answer text even if it is short.", q);
                if(q.getAnswers().size() == initialAns + 1) {
                    System.out.println("Test 3 (Create valid answer) passed");
                    testsPassed++;
                } else {
                    System.out.println("Test 3 failed");
                }
            } catch(Exception e) {
                System.out.println("Test 3 failed: " + e.getMessage());
            }
            
            // --- Test 4: Mark an answer as solution ---
            testsRun++;
            Answer a = q.getAnswers().get(0);
            try {
                qm.markAnswerAsSolution(q, a);
                if(a.isApprovedSolution() && q.isResolved()) {
                    System.out.println("Test 4 (Mark answer as solution) passed");
                    testsPassed++;
                } else {
                    System.out.println("Test 4 failed");
                }
            } catch(Exception e) {
                System.out.println("Test 4 failed: " + e.getMessage());
            }
            
            // --- Test 5: Unmark an answer as solution ---
            testsRun++;
            try {
                qm.unmarkAnswerAsSolution(q, a);
                if(!a.isApprovedSolution() && !q.isResolved()) {
                    System.out.println("Test 5 (Unmark answer as solution) passed");
                    testsPassed++;
                } else {
                    System.out.println("Test 5 failed");
                }
            } catch(Exception e) {
                System.out.println("Test 5 failed: " + e.getMessage());
            }
            
            // --- Test 6: Update question text ---
            testsRun++;
            String oldText = q.getQuestionText();
            qm.editQuestiontext(q, "Updated question text that is long enough for update testing.");
            if(!q.getQuestionText().equals(oldText)) {
                System.out.println("Test 6 (Update question text) passed");
                testsPassed++;
            } else {
                System.out.println("Test 6 failed");
            }
            
            // --- Test 7: Update answer text ---
            testsRun++;
            String oldAnsText = a.getAnswerText();
            qm.updateAnswerText(a, "Updated answer text that changes the content.");
            if(!a.getAnswerText().equals(oldAnsText)) {
                System.out.println("Test 7 (Update answer text) passed");
                testsPassed++;
            } else {
                System.out.println("Test 7 failed");
            }
            
            // --- Test 8: Search questions by keyword ---
            testsRun++;
            List<Question> searchResults = qm.searchQuestionsByKeyword("Updated");
            if(searchResults.size() > 0) {
                System.out.println("Test 8 (Search questions) passed");
                testsPassed++;
            } else {
                System.out.println("Test 8 failed");
            }
            
            // --- Test 9: Delete an answer ---
            testsRun++;
            int countBefore = q.getAnswers().size();
            boolean ansDeleted = qm.deleteAnswer(a.getId());
            if(ansDeleted && q.getAnswers().size() == countBefore - 1) {
                System.out.println("Test 9 (Delete answer) passed");
                testsPassed++;
            } else {
                System.out.println("Test 9 failed");
            }
            
            // --- Test 10: Delete question (should also remove its answers) ---
            testsRun++;
            boolean qDeleted = qm.deleteQuestion(q.getId());
            if(qDeleted && qm.getAllQuestions().isEmpty()) {
                System.out.println("Test 10 (Delete question) passed");
                testsPassed++;
            } else {
                System.out.println("Test 10 failed");
            }
            
            // --- Summary ---
            System.out.println("Total tests run: " + testsRun + ", Passed: " + testsPassed);
            
            db.closeConnection();
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
