package se.examenarbete.blitzduel.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private String testToken;
    private String testEmail;

    @BeforeEach
    void setUp() {
        testToken = "validToken123";
        testEmail = "test@example.com";

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ShouldAuthenticateUser_WithValidToken() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + testToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail(testToken)).thenReturn(testEmail);
        when(jwtUtil.validateToken(testToken, testEmail)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(testEmail, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());

        verify(jwtUtil).extractEmail(testToken);
        verify(jwtUtil).validateToken(testToken, testEmail);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenNoAuthHeader() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil, never()).extractEmail(anyString());
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenAuthHeaderDoesNotStartWithBearer() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil, never()).extractEmail(anyString());
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenTokenIsInvalid() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + testToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail(testToken)).thenReturn(testEmail);
        when(jwtUtil.validateToken(testToken, testEmail)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil).extractEmail(testToken);
        verify(jwtUtil).validateToken(testToken, testEmail);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenEmailIsNull() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + testToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail(testToken)).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(jwtUtil).extractEmail(testToken);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldNotAuthenticate_WhenAlreadyAuthenticated() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + testToken;

        // Set up existing authentication
        Authentication existingAuth = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail(testToken)).thenReturn(testEmail);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertEquals(existingAuth, authentication); // Should remain the existing auth

        verify(jwtUtil).extractEmail(testToken);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldContinueFilterChain_WhenExceptionOccurs() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer " + testToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail(testToken)).thenThrow(new RuntimeException("Invalid token"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ShouldExtractTokenCorrectly_FromBearerHeader() throws ServletException, IOException {
        // Given
        String authHeader = "Bearer myTestToken123";

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtil.extractEmail("myTestToken123")).thenReturn(testEmail);
        when(jwtUtil.validateToken("myTestToken123", testEmail)).thenReturn(true);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(jwtUtil).extractEmail("myTestToken123");
        verify(jwtUtil).validateToken("myTestToken123", testEmail);
    }

    @Test
    void doFilterInternal_ShouldAlwaysCallFilterChain() throws ServletException, IOException {
        // Test 1: With valid token
        when(request.getHeader("Authorization")).thenReturn("Bearer " + testToken);
        when(jwtUtil.extractEmail(testToken)).thenReturn(testEmail);
        when(jwtUtil.validateToken(testToken, testEmail)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);

        // Test 2: Without token
        reset(filterChain, request);
        SecurityContextHolder.clearContext();
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
