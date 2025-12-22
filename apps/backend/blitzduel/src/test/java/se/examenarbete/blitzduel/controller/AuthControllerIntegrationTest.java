package se.examenarbete.blitzduel.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import se.examenarbete.blitzduel.model.User;
import se.examenarbete.blitzduel.security.JwtUtil;
import se.examenarbete.blitzduel.service.UserService;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;
    private String testToken;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testToken = "validToken123";

        testUser = new User("test@example.com", "Test User", "google123", "http://example.com/pic.jpg");
        testUser.setId(testUserId);
        testUser.setGamesPlayed(10);
        testUser.setGamesWon(5);
        testUser.setTotalScore(500);
    }

    @Test
    void getCurrentUser_ShouldReturnUserInfo_WithValidToken() throws Exception {
        // Given
        when(jwtUtil.extractUserId(testToken)).thenReturn(testUserId);
        when(userService.getUserById(testUserId)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testUserId))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.profilePictureUrl").value("http://example.com/pic.jpg"))
                .andExpect(jsonPath("$.gamesPlayed").value(10))
                .andExpect(jsonPath("$.gamesWon").value(5))
                .andExpect(jsonPath("$.totalScore").value(500));
    }

    @Test
    void getCurrentUser_ShouldReturn401_WhenNoAuthorizationHeader() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_ShouldReturn401_WhenInvalidToken() throws Exception {
        // Given
        String invalidToken = "invalidToken";
        when(jwtUtil.extractUserId(invalidToken)).thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_ShouldReturn401_WhenUserNotFound() throws Exception {
        // Given
        when(jwtUtil.extractUserId(testToken)).thenReturn(testUserId);
        when(userService.getUserById(testUserId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_ShouldReturn401_WhenAuthorizationHeaderInvalid() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "InvalidFormat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_ShouldIncludeAllUserFields() throws Exception {
        // Given
        User userWithAllFields = new User(
                "complete@example.com",
                "Complete User",
                "google456",
                "http://example.com/complete.jpg"
        );
        userWithAllFields.setId(2L);
        userWithAllFields.setGamesPlayed(100);
        userWithAllFields.setGamesWon(75);
        userWithAllFields.setTotalScore(10000);

        String token = "completeToken";
        when(jwtUtil.extractUserId(token)).thenReturn(2L);
        when(userService.getUserById(2L)).thenReturn(Optional.of(userWithAllFields));

        // When & Then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.email").value("complete@example.com"))
                .andExpect(jsonPath("$.name").value("Complete User"))
                .andExpect(jsonPath("$.profilePictureUrl").value("http://example.com/complete.jpg"))
                .andExpect(jsonPath("$.gamesPlayed").value(100))
                .andExpect(jsonPath("$.gamesWon").value(75))
                .andExpect(jsonPath("$.totalScore").value(10000));
    }
}
