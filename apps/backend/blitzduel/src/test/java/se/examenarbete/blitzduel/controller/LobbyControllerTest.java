package se.examenarbete.blitzduel.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import se.examenarbete.blitzduel.dto.CreateLobbyRequest;
import se.examenarbete.blitzduel.dto.JoinLobbyRequest;
import se.examenarbete.blitzduel.dto.LobbyResponse;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.service.LobbyService;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyControllerTest {

    @Mock
    private LobbyService lobbyService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private LobbyController lobbyController;

    private String testLobbyCode;
    private Long testQuizId;
    private String testHostName;
    private String testGuestName;
    private Long testHostUserId;
    private Long testGuestUserId;

    @BeforeEach
    void setUp() {
        testLobbyCode = "ABC123";
        testQuizId = 1L;
        testHostName = "Host";
        testGuestName = "Guest";
        testHostUserId = 100L;
        testGuestUserId = 200L;
    }

    @Test
    void createLobby_ShouldReturnLobbyResponse() {
        // Given
        Lobby mockLobby = new Lobby(testLobbyCode, testQuizId, testHostName);
        mockLobby.setHostUserId(testHostUserId);

        CreateLobbyRequest request = new CreateLobbyRequest();
        request.setName(testHostName);
        request.setQuizId(testQuizId);
        request.setUserId(testHostUserId);

        when(lobbyService.createLobby(testHostName, testQuizId, testHostUserId))
                .thenReturn(mockLobby);

        // When
        LobbyResponse response = lobbyController.createLobby(request);

        // Then
        assertNotNull(response);
        assertEquals(testLobbyCode, response.getLobbyCode());
        assertEquals("WAITING", response.getStatus());
        assertEquals(testQuizId, response.getQuizId());
        assertEquals(testHostName, response.getHostName());
        assertNull(response.getGuestName());

        verify(lobbyService).createLobby(testHostName, testQuizId, testHostUserId);
    }

    @Test
    void joinLobby_ShouldBroadcastToTopic_WhenJoinSuccessful() {
        // Given
        Lobby mockLobby = new Lobby(testLobbyCode, testQuizId, testHostName);
        mockLobby.setGuestName(testGuestName);
        mockLobby.setGuestUserId(testGuestUserId);
        mockLobby.setStatus(Lobby.Status.READY);

        JoinLobbyRequest request = new JoinLobbyRequest();
        request.setName(testGuestName);
        request.setUserId(testGuestUserId);

        Principal principal = () -> testGuestName;

        when(lobbyService.joinLobby(testLobbyCode, testGuestName, testGuestUserId))
                .thenReturn(Optional.of(mockLobby));

        // When
        lobbyController.joinLobby(testLobbyCode, request, principal);

        // Then
        ArgumentCaptor<LobbyResponse> responseCaptor = ArgumentCaptor.forClass(LobbyResponse.class);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/lobby/" + testLobbyCode),
                responseCaptor.capture()
        );

        LobbyResponse sentResponse = responseCaptor.getValue();
        assertEquals(testLobbyCode, sentResponse.getLobbyCode());
        assertEquals("READY", sentResponse.getStatus());
        assertEquals(testHostName, sentResponse.getHostName());
        assertEquals(testGuestName, sentResponse.getGuestName());
        assertEquals(testQuizId, sentResponse.getQuizId());

        verify(lobbyService).joinLobby(testLobbyCode, testGuestName, testGuestUserId);
    }

    @Test
    void joinLobby_ShouldSendErrorToUser_WhenLobbyFull() {
        // Given
        JoinLobbyRequest request = new JoinLobbyRequest();
        request.setName(testGuestName);
        request.setUserId(testGuestUserId);

        Principal principal = () -> testGuestName;

        when(lobbyService.joinLobby(testLobbyCode, testGuestName, testGuestUserId))
                .thenReturn(Optional.empty());

        // When
        lobbyController.joinLobby(testLobbyCode, request, principal);

        // Then
        ArgumentCaptor<LobbyResponse> responseCaptor = ArgumentCaptor.forClass(LobbyResponse.class);
        verify(messagingTemplate).convertAndSendToUser(
                eq(testGuestName),
                eq("/queue/lobby"),
                responseCaptor.capture()
        );

        LobbyResponse sentResponse = responseCaptor.getValue();
        assertEquals(testLobbyCode, sentResponse.getLobbyCode());
        assertEquals("FULL", sentResponse.getStatus());
        assertNull(sentResponse.getQuizId());
        assertNull(sentResponse.getHostName());
        assertNull(sentResponse.getGuestName());

        verify(lobbyService).joinLobby(testLobbyCode, testGuestName, testGuestUserId);
    }

    @Test
    void joinLobby_ShouldSendErrorToUser_WhenLobbyNotFound() {
        // Given
        JoinLobbyRequest request = new JoinLobbyRequest();
        request.setName(testGuestName);
        request.setUserId(testGuestUserId);

        Principal principal = () -> testGuestName;

        when(lobbyService.joinLobby("NONEXISTENT", testGuestName, testGuestUserId))
                .thenReturn(Optional.empty());

        // When
        lobbyController.joinLobby("NONEXISTENT", request, principal);

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                eq(testGuestName),
                eq("/queue/lobby"),
                any(LobbyResponse.class)
        );

        verify(lobbyService).joinLobby("NONEXISTENT", testGuestName, testGuestUserId);
    }

    @Test
    void createLobby_ShouldCallServiceWithCorrectParameters() {
        // Given
        CreateLobbyRequest request = new CreateLobbyRequest();
        request.setName("TestPlayer");
        request.setQuizId(5L);
        request.setUserId(999L);

        Lobby mockLobby = new Lobby("XYZ789", 5L, "TestPlayer");

        when(lobbyService.createLobby("TestPlayer", 5L, 999L))
                .thenReturn(mockLobby);

        // When
        lobbyController.createLobby(request);

        // Then
        verify(lobbyService).createLobby("TestPlayer", 5L, 999L);
    }
}
