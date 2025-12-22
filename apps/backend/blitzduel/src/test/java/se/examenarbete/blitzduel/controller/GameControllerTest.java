package se.examenarbete.blitzduel.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.dto.QuestionDTO;
import se.examenarbete.blitzduel.dto.SubmitAnswerRequest;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.service.GameService;
import se.examenarbete.blitzduel.service.LobbyService;
import se.examenarbete.blitzduel.service.UserService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

    @Mock
    private GameService gameService;

    @Mock
    private LobbyService lobbyService;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private UserService userService;

    @InjectMocks
    private GameController gameController;

    private String testLobbyCode;
    private Long testQuizId;
    private String testHostName;
    private String testGuestName;
    private Long testHostUserId;
    private Long testGuestUserId;
    private Lobby testLobby;
    private GameSession testSession;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        testLobbyCode = "ABC123";
        testQuizId = 1L;
        testHostName = "Host";
        testGuestName = "Guest";
        testHostUserId = 100L;
        testGuestUserId = 200L;

        testLobby = new Lobby(testLobbyCode, testQuizId, testHostName);
        testLobby.setGuestName(testGuestName);
        testLobby.setHostUserId(testHostUserId);
        testLobby.setGuestUserId(testGuestUserId);

        testSession = new GameSession(testLobbyCode, testQuizId, testHostName, testGuestName);
        testSession.setCurrentQuestionIndex(0);
        testSession.setHostUserId(testHostUserId);
        testSession.setGuestUserId(testGuestUserId);

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setQuestionText("What is 2+2?");
        testQuestion.setOptions(Arrays.asList("3", "4", "5", "6"));
        testQuestion.setCorrectAnswerIndex(1);
    }

    @Test
    void startGame_ShouldCreateSessionAndBroadcastQuestion() {
        // Given
        when(lobbyService.getLobby(testLobbyCode)).thenReturn(Optional.of(testLobby));
        when(gameService.startGame(testLobbyCode, testQuizId, testHostName, testGuestName, testHostUserId, testGuestUserId))
                .thenReturn(testSession);
        when(gameService.getCurrentQuestion(testLobbyCode)).thenReturn(testQuestion);

        // When
        gameController.startGame(testLobbyCode);

        // Then
        verify(lobbyService).getLobby(testLobbyCode);
        verify(gameService).startGame(testLobbyCode, testQuizId, testHostName, testGuestName, testHostUserId, testGuestUserId);
        verify(gameService).getCurrentQuestion(testLobbyCode);

        // Verify GAME_STARTING message
        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/lobby/" + testLobbyCode + "/start"),
                eq("GAME_STARTING")
        );

        // Verify question message
        ArgumentCaptor<QuestionDTO> questionCaptor = ArgumentCaptor.forClass(QuestionDTO.class);
        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/game/" + testLobbyCode),
                questionCaptor.capture()
        );

        QuestionDTO sentQuestion = questionCaptor.getValue();
        assertEquals("QUESTION", sentQuestion.getType());
        assertEquals(0, sentQuestion.getCurrentQuestionIndex());
        assertEquals("What is 2+2?", sentQuestion.getQuestionText());
        assertEquals(testHostName, sentQuestion.getHostName());
        assertEquals(testGuestName, sentQuestion.getGuestName());

        // Verify timer and timeout scheduled
        verify(gameService).startTimerBroadcast(eq(testLobbyCode), eq(5));
        verify(gameService).scheduleTimeout(eq(testLobbyCode), any(Runnable.class));
    }

    @Test
    void startGame_ShouldThrowException_WhenLobbyNotFound() {
        // Given
        when(lobbyService.getLobby(testLobbyCode)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            gameController.startGame(testLobbyCode);
        });

        verify(lobbyService).getLobby(testLobbyCode);
        verifyNoInteractions(gameService);
        verifyNoInteractions(simpMessagingTemplate);
    }

    @Test
    void submitAnswer_ShouldReturnEarly_WhenWaitingForOtherPlayer() {
        // Given
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setName(testHostName);
        request.setAnswerIndex(1);

        GameUpdateResponse waitingResponse = new GameUpdateResponse();
        waitingResponse.setStatus("WAITING");

        when(gameService.submitAnswer(testLobbyCode, testHostName, 1)).thenReturn(waitingResponse);

        // When
        gameController.submitAnswer(testLobbyCode, request);

        // Then
        verify(gameService).submitAnswer(testLobbyCode, testHostName, 1);
        verifyNoInteractions(simpMessagingTemplate);
        verify(gameService, never()).cancelTimeout(any());
        verify(gameService, never()).cancelTimerBroadcast(any());
    }

    @Test
    void submitAnswer_ShouldBroadcastResponse_WhenBothAnswered() {
        // Given
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setName(testHostName);
        request.setAnswerIndex(1);

        GameUpdateResponse bothAnsweredResponse = new GameUpdateResponse();
        bothAnsweredResponse.setStatus("BOTH_ANSWERED");
        bothAnsweredResponse.setHostScore(1);
        bothAnsweredResponse.setGuestScore(0);
        bothAnsweredResponse.setCorrectAnswerIndex(1);

        when(gameService.submitAnswer(testLobbyCode, testHostName, 1)).thenReturn(bothAnsweredResponse);

        // When
        gameController.submitAnswer(testLobbyCode, request);

        // Then
        verify(gameService).submitAnswer(testLobbyCode, testHostName, 1);
        verify(gameService).cancelTimeout(testLobbyCode);
        verify(gameService).cancelTimerBroadcast(testLobbyCode);

        ArgumentCaptor<GameUpdateResponse> responseCaptor = ArgumentCaptor.forClass(GameUpdateResponse.class);
        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/game/" + testLobbyCode),
                responseCaptor.capture()
        );

        GameUpdateResponse sentResponse = responseCaptor.getValue();
        assertEquals("BOTH_ANSWERED", sentResponse.getStatus());
        assertEquals(1, sentResponse.getHostScore());
        assertEquals(0, sentResponse.getGuestScore());
    }

    @Test
    void submitAnswer_ShouldHandleGameOver() {
        // Given
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setName(testHostName);
        request.setAnswerIndex(1);

        GameUpdateResponse gameOverResponse = new GameUpdateResponse();
        gameOverResponse.setStatus("GAME_OVER");
        gameOverResponse.setHostScore(6);
        gameOverResponse.setGuestScore(4);
        gameOverResponse.setHostName(testHostName);
        gameOverResponse.setGuestName(testGuestName);

        when(gameService.submitAnswer(testLobbyCode, testHostName, 1)).thenReturn(gameOverResponse);

        // When
        gameController.submitAnswer(testLobbyCode, request);

        // Then
        verify(gameService).submitAnswer(testLobbyCode, testHostName, 1);
        verify(gameService).cancelTimeout(testLobbyCode);
        verify(gameService).cancelTimerBroadcast(testLobbyCode);

        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/game/" + testLobbyCode),
                any(GameUpdateResponse.class)
        );
    }

    @Test
    void submitAnswer_ShouldCallServiceWithCorrectParameters() {
        // Given
        SubmitAnswerRequest request = new SubmitAnswerRequest();
        request.setName("PlayerName");
        request.setAnswerIndex(2);

        GameUpdateResponse response = new GameUpdateResponse();
        response.setStatus("WAITING");

        when(gameService.submitAnswer("LOBBY123", "PlayerName", 2)).thenReturn(response);

        // When
        gameController.submitAnswer("LOBBY123", request);

        // Then
        verify(gameService).submitAnswer("LOBBY123", "PlayerName", 2);
    }
}
