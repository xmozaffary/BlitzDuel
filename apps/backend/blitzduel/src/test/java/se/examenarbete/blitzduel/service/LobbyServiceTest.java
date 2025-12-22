package se.examenarbete.blitzduel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.util.RandomCodeGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

    @Mock
    private RandomCodeGenerator randomCodeGenerator;

    @InjectMocks
    private LobbyService lobbyService;

    private String testNickname;
    private Long testQuizId;
    private Long testUserId;
    private String testCode;

    @BeforeEach
    void setUp() {
        testNickname = "TestPlayer";
        testQuizId = 1L;
        testUserId = 100L;
        testCode = "ABC123";
    }

    @Test
    void createLobby_ShouldGenerateCodeAndCreateLobby() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);

        // When
        Lobby result = lobbyService.createLobby(testNickname, testQuizId, testUserId);

        // Then
        assertNotNull(result);
        assertEquals(testCode, result.getLobbyCode());
        assertEquals(testNickname, result.getHostName());
        assertEquals(testQuizId, result.getQuizId());
        assertEquals(testUserId, result.getHostUserId());
        assertEquals(Lobby.Status.WAITING, result.getStatus());
        assertNull(result.getGuestName());
        assertNotNull(result.getCreatedAt());

        verify(randomCodeGenerator).generate();
    }

    @Test
    void createLobby_ShouldRegenerateCode_WhenCodeCollides() {
        // Given
        String firstCode = "ABC123";
        String secondCode = "DEF456";

        when(randomCodeGenerator.generate())
                .thenReturn(firstCode)
                .thenReturn(firstCode)
                .thenReturn(secondCode);

        // Create first lobby
        lobbyService.createLobby("Player1", testQuizId, 1L);

        // When - Create second lobby (should get different code)
        Lobby result = lobbyService.createLobby("Player2", testQuizId, 2L);

        // Then
        assertEquals(secondCode, result.getLobbyCode());
        verify(randomCodeGenerator, times(3)).generate();
    }

    @Test
    void createLobby_ShouldStoreInMap() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);

        // When
        lobbyService.createLobby(testNickname, testQuizId, testUserId);
        Optional<Lobby> retrieved = lobbyService.getLobby(testCode);

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(testCode, retrieved.get().getLobbyCode());
    }

    @Test
    void joinLobby_ShouldSucceed_WhenLobbyExistsAndNotFull() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);
        lobbyService.createLobby("Host", testQuizId, 1L);

        String guestNickname = "Guest";
        Long guestUserId = 200L;

        // When
        Optional<Lobby> result = lobbyService.joinLobby(testCode, guestNickname, guestUserId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(guestNickname, result.get().getGuestName());
        assertEquals(guestUserId, result.get().getGuestUserId());
        assertEquals(Lobby.Status.READY, result.get().getStatus());
        assertTrue(result.get().isFull());
    }

    @Test
    void joinLobby_ShouldReturnEmpty_WhenLobbyDoesNotExist() {
        // When
        Optional<Lobby> result = lobbyService.joinLobby("NONEXISTENT", "Guest", 200L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void joinLobby_ShouldReturnEmpty_WhenLobbyIsFull() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);
        lobbyService.createLobby("Host", testQuizId, 1L);
        lobbyService.joinLobby(testCode, "Guest1", 200L);

        // When - Try to join again when lobby is full
        Optional<Lobby> result = lobbyService.joinLobby(testCode, "Guest2", 300L);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getLobby_ShouldReturnLobby_WhenExists() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);
        Lobby created = lobbyService.createLobby(testNickname, testQuizId, testUserId);

        // When
        Optional<Lobby> result = lobbyService.getLobby(testCode);

        // Then
        assertTrue(result.isPresent());
        assertEquals(created.getLobbyCode(), result.get().getLobbyCode());
        assertEquals(created.getHostName(), result.get().getHostName());
    }

    @Test
    void getLobby_ShouldReturnEmpty_WhenDoesNotExist() {
        // When
        Optional<Lobby> result = lobbyService.getLobby("NONEXISTENT");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void joinLobby_ShouldSetStatusToReady() {
        // Given
        when(randomCodeGenerator.generate()).thenReturn(testCode);
        Lobby lobby = lobbyService.createLobby("Host", testQuizId, 1L);

        assertEquals(Lobby.Status.WAITING, lobby.getStatus());
        assertTrue(lobby.isWaiting());
        assertFalse(lobby.isReady());

        // When
        Optional<Lobby> result = lobbyService.joinLobby(testCode, "Guest", 200L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(Lobby.Status.READY, result.get().getStatus());
        assertFalse(result.get().isWaiting());
        assertTrue(result.get().isReady());
    }
}
