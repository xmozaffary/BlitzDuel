package se.examenarbete.blitzduel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.examenarbete.blitzduel.model.User;
import se.examenarbete.blitzduel.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private String testEmail;
    private String testName;
    private String testGoogleId;
    private String testProfilePictureUrl;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testName = "Test User";
        testGoogleId = "google123";
        testProfilePictureUrl = "https://example.com/picture.jpg";

        testUser = new User(testEmail, testName, testGoogleId, testProfilePictureUrl);
        testUser.setId(1L);
    }

    @Test
    void createOrUpdateUser_ShouldCreateNewUser_WhenUserDoesNotExist() {
        // Given
        when(userRepository.findByGoogleId(testGoogleId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createOrUpdateUser(testEmail, testName, testGoogleId, testProfilePictureUrl);

        // Then
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertEquals(testName, result.getName());
        assertEquals(testGoogleId, result.getGoogleId());
        assertEquals(testProfilePictureUrl, result.getProfilePictureUrl());

        verify(userRepository).findByGoogleId(testGoogleId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createOrUpdateUser_ShouldUpdateExistingUser_WhenUserExists() {
        // Given
        User existingUser = new User("old@example.com", "Old Name", testGoogleId, "old-url");
        existingUser.setId(1L);

        when(userRepository.findByGoogleId(testGoogleId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userService.createOrUpdateUser(testEmail, testName, testGoogleId, testProfilePictureUrl);

        // Then
        assertNotNull(result);
        assertEquals(testEmail, result.getEmail());
        assertEquals(testName, result.getName());
        assertEquals(testGoogleId, result.getGoogleId());
        assertEquals(testProfilePictureUrl, result.getProfilePictureUrl());

        verify(userRepository).findByGoogleId(testGoogleId);
        verify(userRepository).save(existingUser);
    }

    @Test
    void createOrUpdateUser_ShouldUpdateLastLogin_WhenUserExists() {
        // Given
        User existingUser = new User(testEmail, testName, testGoogleId, testProfilePictureUrl);
        when(userRepository.findByGoogleId(testGoogleId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        userService.createOrUpdateUser(testEmail, testName, testGoogleId, testProfilePictureUrl);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getLastLogin());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenExists() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail(testEmail);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEmail, result.get().getEmail());
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void getUserByEmail_ShouldReturnEmpty_WhenDoesNotExist() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail(testEmail);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findByEmail(testEmail);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenDoesNotExist() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findById(userId);
    }

    @Test
    void updateGameStats_ShouldIncrementGamesPlayedAndWon_WhenWon() {
        // Given
        Long userId = 1L;
        int score = 100;
        testUser.setGamesPlayed(5);
        testUser.setGamesWon(3);
        testUser.setTotalScore(500);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateGameStats(userId, true, score);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(6, savedUser.getGamesPlayed());
        assertEquals(4, savedUser.getGamesWon());
        assertEquals(600, savedUser.getTotalScore());
    }

    @Test
    void updateGameStats_ShouldIncrementGamesPlayedOnly_WhenLost() {
        // Given
        Long userId = 1L;
        int score = 50;
        testUser.setGamesPlayed(5);
        testUser.setGamesWon(3);
        testUser.setTotalScore(500);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateGameStats(userId, false, score);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(6, savedUser.getGamesPlayed());
        assertEquals(3, savedUser.getGamesWon()); // Should not increment
        assertEquals(550, savedUser.getTotalScore());
    }

    @Test
    void updateGameStats_ShouldThrowException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateGameStats(userId, true, 100);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateGameStats_ShouldAddScoreCorrectly() {
        // Given
        Long userId = 1L;
        testUser.setTotalScore(0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateGameStats(userId, false, 250);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(250, userCaptor.getValue().getTotalScore());
    }
}
