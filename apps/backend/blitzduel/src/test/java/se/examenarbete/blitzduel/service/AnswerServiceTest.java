package se.examenarbete.blitzduel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Question;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private AnswerService answerService;

    private GameSession testSession;
    private String lobbyCode;
    private Long quizId;
    private String hostName;
    private String guestName;
    private List<Question> testQuestions;

    @BeforeEach
    void setUp() {
        lobbyCode = "ABC123";
        quizId = 1L;
        hostName = "Host";
        guestName = "Guest";

        testSession = new GameSession(lobbyCode, quizId, hostName, guestName);
        testSession.setCurrentQuestionIndex(0);
        testSession.setHostUserId(100L);
        testSession.setGuestUserId(200L);

        // Create test questions (10 questions to match GameSession.isGameOver() logic)
        testQuestions = Arrays.asList(
                createQuestion(1L, "Question 1", 0),
                createQuestion(2L, "Question 2", 1),
                createQuestion(3L, "Question 3", 2),
                createQuestion(4L, "Question 4", 0),
                createQuestion(5L, "Question 5", 1),
                createQuestion(6L, "Question 6", 2),
                createQuestion(7L, "Question 7", 0),
                createQuestion(8L, "Question 8", 1),
                createQuestion(9L, "Question 9", 2),
                createQuestion(10L, "Question 10", 0)
        );
    }

    private Question createQuestion(Long id, String text, int correctAnswerIndex) {
        Question question = new Question();
        question.setId(id);
        question.setQuestionText(text);
        question.setCorrectAnswerIndex(correctAnswerIndex);
        return question;
    }

    @Test
    void submitAnswer_ShouldReturnWaiting_WhenOnlyHostAnswered() {
        // Given - No mocking needed for this test

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, hostName, 0);

        // Then
        assertEquals("WAITING", result.getStatus());
        assertEquals(0, testSession.getHostNameAnswer());
        assertNull(testSession.getGuestNameAnswer());
    }

    @Test
    void submitAnswer_ShouldReturnWaiting_WhenOnlyGuestAnswered() {
        // Given - No mocking needed for this test

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 1);

        // Then
        assertEquals("WAITING", result.getStatus());
        assertNull(testSession.getHostNameAnswer());
        assertEquals(1, testSession.getGuestNameAnswer());
    }

    @Test
    void submitAnswer_ShouldProcessBothAnswers_WhenBothPlayersAnswered() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // Host answers first
        answerService.submitAnswer(lobbyCode, testSession, hostName, 0);

        // When - Guest answers (both answered now)
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 1);

        // Then
        assertEquals("BOTH_ANSWERED", result.getStatus());
        assertEquals(0, result.getCorrectAnswerIndex());
        assertTrue(result.getHostCorrect());
        assertFalse(result.getGuestCorrect());
        assertEquals(1, result.getHostScore());
        assertEquals(0, result.getGuestScore());
    }

    @Test
    void submitAnswer_ShouldIncrementScores_WhenAnswersAreCorrect() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // Both answer correctly
        testSession.setHostNameAnswer(0);
        testSession.setGuestNameAnswer(0);

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 0);

        // Then
        assertEquals(1, testSession.getHostNameScore());
        assertEquals(1, testSession.getGuestNameScore());
        assertTrue(result.getHostCorrect());
        assertTrue(result.getGuestCorrect());
    }

    @Test
    void submitAnswer_ShouldNotIncrementScores_WhenAnswersAreIncorrect() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // Both answer incorrectly
        testSession.setHostNameAnswer(2);
        testSession.setGuestNameAnswer(1);

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 1);

        // Then
        assertEquals(0, testSession.getHostNameScore());
        assertEquals(0, testSession.getGuestNameScore());
        assertFalse(result.getHostCorrect());
        assertFalse(result.getGuestCorrect());
    }

    @Test
    void submitAnswer_ShouldAdvanceToNextQuestion_WhenBothAnswered() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        testSession.setHostNameAnswer(0);
        assertEquals(0, testSession.getCurrentQuestionIndex());

        // When
        answerService.submitAnswer(lobbyCode, testSession, guestName, 0);

        // Then
        assertEquals(1, testSession.getCurrentQuestionIndex());
    }

    @Test
    void submitAnswer_ShouldResetAnswers_AfterProcessing() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        testSession.setHostNameAnswer(0);

        // When
        answerService.submitAnswer(lobbyCode, testSession, guestName, 1);

        // Then
        assertNull(testSession.getHostNameAnswer());
        assertNull(testSession.getGuestNameAnswer());
    }

    @Test
    void submitAnswer_ShouldReturnGameOver_WhenLastQuestion() {
        // Given
        testSession.setCurrentQuestionIndex(9); // Last question (index 9 out of 10)
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        testSession.setHostNameAnswer(0);

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 0);

        // Then
        assertEquals("GAME_OVER", result.getStatus());
        assertEquals(10, testSession.getCurrentQuestionIndex());
    }

    @Test
    void handleTimeout_ShouldProcessPartialAnswers() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // Only host answered
        testSession.setHostNameAnswer(0);

        // When
        GameUpdateResponse result = answerService.handleTimeout(lobbyCode, testSession);

        // Then
        assertNotNull(result);
        assertEquals("BOTH_ANSWERED", result.getStatus());
        assertTrue(result.getHostCorrect());
        assertFalse(result.getGuestCorrect());
        assertEquals(1, testSession.getHostNameScore());
        assertEquals(0, testSession.getGuestNameScore());
    }

    @Test
    void handleTimeout_ShouldHandleNoAnswers() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // No answers
        assertNull(testSession.getHostNameAnswer());
        assertNull(testSession.getGuestNameAnswer());

        // When
        GameUpdateResponse result = answerService.handleTimeout(lobbyCode, testSession);

        // Then
        assertNotNull(result);
        assertFalse(result.getHostCorrect());
        assertFalse(result.getGuestCorrect());
        assertEquals(0, testSession.getHostNameScore());
        assertEquals(0, testSession.getGuestNameScore());
    }

    @Test
    void handleTimeout_ShouldAdvanceQuestion() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);
        testSession.setCurrentQuestionIndex(0);

        // When
        answerService.handleTimeout(lobbyCode, testSession);

        // Then
        assertEquals(1, testSession.getCurrentQuestionIndex());
    }

    @Test
    void handleTimeout_ShouldResetAnswers() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);
        testSession.setHostNameAnswer(1);
        testSession.setGuestNameAnswer(2);

        // When
        answerService.handleTimeout(lobbyCode, testSession);

        // Then
        assertNull(testSession.getHostNameAnswer());
        assertNull(testSession.getGuestNameAnswer());
    }

    @Test
    void handleTimeout_ShouldReturnGameOver_WhenLastQuestion() {
        // Given
        testSession.setCurrentQuestionIndex(9); // Last question (index 9 out of 10)
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);

        // When
        GameUpdateResponse result = answerService.handleTimeout(lobbyCode, testSession);

        // Then
        assertEquals("GAME_OVER", result.getStatus());
    }

    @Test
    void submitAnswer_ShouldIncludePlayerNames() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);
        testSession.setHostNameAnswer(0);

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 0);

        // Then
        assertEquals(hostName, result.getHostName());
        assertEquals(guestName, result.getGuestName());
    }

    @Test
    void submitAnswer_ShouldIncludeAnswerIndices() {
        // Given
        when(quizService.getQuestionsByQuizId(quizId)).thenReturn(testQuestions);
        testSession.setHostNameAnswer(2);

        // When
        GameUpdateResponse result = answerService.submitAnswer(lobbyCode, testSession, guestName, 1);

        // Then
        assertEquals(2, result.getHostAnswerIndex());
        assertEquals(1, result.getGuestAnswerIndex());
    }
}
