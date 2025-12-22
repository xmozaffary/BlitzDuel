package se.examenarbete.blitzduel.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private Long testExpiration;
    private String testEmail;
    private Long testUserId;
    private String testName;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Set test values using ReflectionTestUtils to inject @Value fields
        testSecret = "mySecretKeyForJWTTokenGenerationThatNeedsToBeAtLeast256BitsLong";
        testExpiration = 3600000L; // 1 hour in milliseconds

        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        testEmail = "test@example.com";
        testUserId = 123L;
        testName = "Test User";
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // When
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        String extractedEmail = jwtUtil.extractEmail(token);

        // Then
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        Long extractedUserId = jwtUtil.extractUserId(token);

        // Then
        assertEquals(testUserId, extractedUserId);
    }

    @Test
    void extractName_ShouldReturnCorrectName() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        String extractedName = jwtUtil.extractName(token);

        // Then
        assertEquals(testName, extractedName);
    }

    @Test
    void extractClaims_ShouldReturnAllClaims() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        Claims claims = jwtUtil.extractClaims(token);

        // Then
        assertNotNull(claims);
        assertEquals(testEmail, claims.getSubject());
        assertEquals(testUserId, claims.get("userId", Long.class));
        assertEquals(testName, claims.get("name", String.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void isTokenExpired_ShouldReturnFalse_ForFreshToken() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ShouldReturnTrue_ForExpiredToken() {
        // Given - Create token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // Already expired
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // Reset to normal expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        // When
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Then
        assertTrue(isExpired);
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // When
        boolean isValid = jwtUtil.validateToken(token, testEmail);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForWrongEmail() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);
        String wrongEmail = "wrong@example.com";

        // When
        boolean isValid = jwtUtil.validateToken(token, wrongEmail);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() {
        // Given - Create token with very short expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L);
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // Reset to normal expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        // When
        boolean isValid = jwtUtil.validateToken(token, testEmail);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateToken_ShouldCreateDifferentTokens_ForDifferentUsers() {
        // Given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // When
        String token1 = jwtUtil.generateToken(email1, 1L, "User 1");
        String token2 = jwtUtil.generateToken(email2, 2L, "User 2");

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void generateToken_ShouldCreateDifferentTokens_ForSameUserAtDifferentTimes() throws InterruptedException {
        // Given
        Thread.sleep(10); // Small delay to ensure different timestamps

        // When
        String token1 = jwtUtil.generateToken(testEmail, testUserId, testName);
        Thread.sleep(10);
        String token2 = jwtUtil.generateToken(testEmail, testUserId, testName);

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void extractClaims_ShouldThrowException_ForInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtil.extractClaims(invalidToken);
        });
    }

    @Test
    void extractClaims_ShouldThrowException_ForTamperedToken() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtil.extractClaims(tamperedToken);
        });
    }

    @Test
    void token_ShouldNotBeValidatedWithWrongSecret() {
        // Given
        String token = jwtUtil.generateToken(testEmail, testUserId, testName);

        // Change the secret
        String differentSecret = "differentSecretKeyForJWTTokenGenerationThatIsAlsoAtLeast256BitsLong";
        ReflectionTestUtils.setField(jwtUtil, "secret", differentSecret);

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtUtil.extractClaims(token);
        });
    }
}
