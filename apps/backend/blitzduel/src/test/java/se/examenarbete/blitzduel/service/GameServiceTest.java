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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private QuizService quizService;

    @Mock
    private TimerService timerService;

    @Mock
    private AnswerService answerService;

    @InjectMocks
    private GameService gameService;

    private String testLobbyCode;
    private Long testQuizId;
    private String testPlayer1;
    private String testPlayer2;
    private Long testHostUserId;
    private Long testGuestUserId;

    @BeforeEach
    void setUp() {
        testLobbyCode = "ABC123";
        testQuizId = 1L;
        testPlayer1 = "Player1";
        testPlayer2 = "Player2";
        testHostUserId = 100L;
        testGuestUserId = 200L;
    }

    @Test
    void startGame_ShouldCreateAndReturnGameSession() {
        // When
        GameSession result = gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        // Then
        assertNotNull(result);
        assertEquals(testLobbyCode, result.getLobbyCode());
        assertEquals(testQuizId, result.getQuizId());
        assertEquals(testPlayer1, result.getHostName());
        assertEquals(testPlayer2, result.getGuestName());
        assertEquals(testHostUserId, result.getHostUserId());
        assertEquals(testGuestUserId, result.getGuestUserId());
        assertEquals(0, result.getCurrentQuestionIndex());
        assertTrue(result.getQuestionStartTime() > 0);
    }

    @Test
    void startGame_ShouldStoreGameSessionInMap() {
        // When
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        Optional<GameSession> retrieved = gameService.getGameSession(testLobbyCode);

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(testLobbyCode, retrieved.get().getLobbyCode());
    }

    @Test
    void getGameSession_ShouldReturnEmpty_WhenSessionDoesNotExist() {
        // When
        Optional<GameSession> result = gameService.getGameSession("NONEXISTENT");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getGameSession_ShouldReturnSession_WhenSessionExists() {
        // Given
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        // When
        Optional<GameSession> result = gameService.getGameSession(testLobbyCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testLobbyCode, result.get().getLobbyCode());
    }

    @Test
    void getCurrentQuestion_ShouldReturnCorrectQuestion() {
        // Given
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        Question mockQuestion = new Question();
        mockQuestion.setQuestionText("Test Question");

        when(quizService.getQuestionsByQuizId(testQuizId))
                .thenReturn(List.of(mockQuestion));

        // When
        Question result = gameService.getCurrentQuestion(testLobbyCode);

        // Then
        assertNotNull(result);
        assertEquals("Test Question", result.getQuestionText());
        verify(quizService).getQuestionsByQuizId(testQuizId);
    }

    @Test
    void getCurrentQuestion_ShouldThrowException_WhenGameNotFound() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.getCurrentQuestion("NONEXISTENT");
        });

        assertEquals("Game not found", exception.getMessage());
    }

    @Test
    void submitAnswer_ShouldCallAnswerService() {
        // Given
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        GameUpdateResponse mockResponse = new GameUpdateResponse();
        mockResponse.setStatus("WAITING");

        when(answerService.submitAnswer(eq(testLobbyCode), any(GameSession.class), eq(testPlayer1), eq(0)))
                .thenReturn(mockResponse);

        // When
        GameUpdateResponse result = gameService.submitAnswer(testLobbyCode, testPlayer1, 0);

        // Then
        assertNotNull(result);
        assertEquals("WAITING", result.getStatus());
        verify(answerService).submitAnswer(eq(testLobbyCode), any(GameSession.class), eq(testPlayer1), eq(0));
    }

    @Test
    void submitAnswer_ShouldThrowException_WhenGameNotFound() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.submitAnswer("NONEXISTENT", testPlayer1, 0);
        });

        assertEquals("Game not found", exception.getMessage());
    }

    @Test
    void scheduleTimeout_ShouldCallTimerService() {
        // Given
        Runnable mockRunnable = () -> {};

        // When
        gameService.scheduleTimeout(testLobbyCode, mockRunnable);

        // Then
        verify(timerService).scheduleTimeout(testLobbyCode, mockRunnable);
    }

    @Test
    void cancelTimeout_ShouldCallTimerService() {
        // When
        gameService.cancelTimeout(testLobbyCode);

        // Then
        verify(timerService).cancelTimeout(testLobbyCode);
    }

    @Test
    void startTimerBroadcast_ShouldCallTimerService_WhenSessionExists() {
        // Given
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        int timeLimit = 30;

        // When
        gameService.startTimerBroadcast(testLobbyCode, timeLimit);

        // Then
        verify(timerService).startTimerBroadcast(eq(testLobbyCode), any(GameSession.class), eq(timeLimit));
    }

    @Test
    void startTimerBroadcast_ShouldNotCallTimerService_WhenSessionDoesNotExist() {
        // Given
        int timeLimit = 30;

        // When
        gameService.startTimerBroadcast("NONEXISTENT", timeLimit);

        // Then
        verify(timerService, never()).startTimerBroadcast(anyString(), any(), anyInt());
    }

    @Test
    void cancelTimerBroadcast_ShouldCallTimerService() {
        // When
        gameService.cancelTimerBroadcast(testLobbyCode);

        // Then
        verify(timerService).cancelTimerBroadcast(testLobbyCode);
    }

    @Test
    void handleTimeout_ShouldCallAnswerService() {
        // Given
        gameService.startGame(
                testLobbyCode,
                testQuizId,
                testPlayer1,
                testPlayer2,
                testHostUserId,
                testGuestUserId
        );

        GameUpdateResponse mockResponse = new GameUpdateResponse();
        mockResponse.setStatus("BOTH_ANSWERED");

        when(answerService.handleTimeout(eq(testLobbyCode), any(GameSession.class)))
                .thenReturn(mockResponse);

        // When
        GameUpdateResponse result = gameService.handleTimeout(testLobbyCode);

        // Then
        assertNotNull(result);
        assertEquals("BOTH_ANSWERED", result.getStatus());
        verify(answerService).handleTimeout(eq(testLobbyCode), any(GameSession.class));
    }

    @Test
    void handleTimeout_ShouldThrowException_WhenGameNotFound() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            gameService.handleTimeout("NONEXISTENT");
        });

        assertEquals("Game not found", exception.getMessage());
    }
}
