package application;

import databasePart1.DatabaseHelper;
import java.util.List;

/**
 * The HW3TestCases class contains test cases that verify 
 * the functionalities of updating, retrieving, and deleting questions and answers 
 * within the database. The test cases ensure that changes made to the objects 
 * are consistently reflected in the database, preserving data continuity.
 * 
 * The test cases included are:
 * 
 *   Test 25: Update question in DB – This test calls a helper function 
 *       that updates the text of an existing question and then synchronizes the changes with the database.
 *   Test 26: Update answer in DB – This test updates an existing answer’s text 
 *       and its solution status, then updates all associated references in the database.
 *   Test 27: Retrieve all questions from DB – This test retrieves the list of all 
 *       questions from the database to verify that the filtering and retrieval functionalities are working as expected.
 *   Test 28: Retrieve answers for DB question – Similar to test 27, this test 
 *       retrieves all answers from the database.
 *   Test 29: Delete answer from DB – This test confirms that an answer can be 
 *       safely removed from the database, ensuring that any related object references are also updated.
 *
 * @author BRADLEY BREEN 
 * @version 1.0
 */

public class HW3TestCases {

    public static void main(String[] args) {
        testQuestionManagerFunctions();
    }

    /**
     * Runs a series of test cases that verify the functionalities of question and answer management
     * using the DatabaseHelper class.
     * 
     * Each test case increments counters for tests run and tests passed, and outputs the result 
     * to the console. In the event of an exception, the error is printed and the test execution continues.
     */
    private static void testQuestionManagerFunctions() {
        int testsRun = 0, testsPassed = 0;


        try {
            // Instantiate a new DatabaseHelper for running test cases.
            DatabaseHelper dbHelper = new DatabaseHelper();
            dbHelper.connectToDatabase();
            QuestionManager qManager = new QuestionManager(dbHelper);

            // Create a Question and Answer for testing purposes.
            Question dbQ = new Question("DBUser", "A", 
                    "Database test question that meets the requirements for insertion.");
            Answer dbA = new Answer("Database test answer that is valid for insertion purposes.", 
                    "DBResponder");
            dbA.markAsSolution();

            // --------------------------------------------------------------------
            // Test 25: Update question in DB
            // This test calls a helper function to update the question's text.
            // The updated text is then synchronized with the database, ensuring that 
            // all references and data continuity are maintained.
            // --------------------------------------------------------------------
            testsRun++;
            String newQuestion = "Updated DB question text for testing update functionality.";
            qManager.updateQuestionText(dbQ, newQuestion);
            if (dbQ.getQuestionText().equals(newQuestion)) {
                testsPassed++;
                System.out.println("Test 25 passed");
            } else {
                System.out.println("Test 25 failed");
            }

            // ------------------------------------------------------------------
            // Test 26: Update answer in DB
            // This test updates the text of an existing answer and its solution status.
            // The helper function ensures that all associated references in the database 
            // are updated accordingly.
            // --------------------------------------------------------------------
            testsRun++;
            String newAnswer = "Updated DB answer text for testing update functionality.";
            qManager.updateAnswerText(dbA, newAnswer);
            if (dbA.getAnswerText().equals(newAnswer)) {
                testsPassed++;
                System.out.println("Test 26 passed");
            } else {
                System.out.println("Test 26 failed");
            }

            // -------------------------------------------------------------------
            // Test 27: Retrieve all questions from DB using QuestionManager
            // This test retrieves all questions stored in the database.
            // It verifies that the retrieval functionality works correctly and that the 
            // database contains at least one question.
            // --------------------------------------------------------------------
            testsRun++;
            List<Question> dbQuestions = qManager.getAllQuestions();
            if (dbQuestions.size() >= 1) {
                testsPassed++;
                System.out.println("Test 27 passed");
            } else {
                System.out.println("Test 27 failed");
            }

            // --------------------------------------------------------------------
            // Test 28: Retrieve answers for DB question
            // This test retrieves all answers from the database.
            // It confirms that the database returns the expected results.
            // --------------------------------------------------------------------=-
            testsRun++;
            List<Answer> qMAnswers =  qManager.getAnswers();
            if (qMAnswers.size() >= 1) {
                testsPassed++;
                System.out.println("Test 28 passed");
            } else {
                System.out.println("Test 28 failed");
            }

            // --------------------------------------------------------------------
            // Test 29: Delete answer from DB
            // This test ensures that an answer can be safely deleted from the database.
            // It verifies that the deletion process updates all relevant references in the 
            // associated objects as well as the database.
            // -----------------------------------------------------------------
            testsRun++;
            boolean dbDelA = qManager.deleteAnswer(qManager.getAnswers().getFirst().getId());
            if (dbDelA) {
                testsPassed++;
                System.out.println("Test 29 passed");
            } else {
                System.out.println("Test 29 failed");
            }

            // Output the test results.
            System.out.println("Total tests run: " + testsRun + ", Passed: " + testsPassed);

            // Close the database connection.
            dbHelper.closeConnection();
        } catch (Exception e) {
            System.out.println("Exception during tests: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
