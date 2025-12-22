package se.examenarbete.blitzduel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.TimerUpdateMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimerServiceTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private TimerService timerService;

    private String testLobbyCode;
    private GameSession testSession;

    @BeforeEach
    void setUp() {
        testLobbyCode = "ABC123";
        testSession = new GameSession(testLobbyCode, 1L, "Host", "Guest");
        testSession.setCurrentQuestionIndex(0);
        testSession.setQuestionStartTime(System.currentTimeMillis());
    }

    @Test
    void scheduleTimeout_ShouldScheduleTask() throws InterruptedException {
        // Given
        final boolean[] executed = {false};
        Runnable task = () -> executed[0] = true;

        // When
        timerService.scheduleTimeout(testLobbyCode, task);

        // Wait for task to execute (timeout is 5 seconds)
        Thread.sleep(5500);

        // Then
        assertTrue(executed[0], "Timeout task should have been executed");
    }

    @Test
    void scheduleTimeout_ShouldCancelPreviousTask_WhenScheduledTwice() throws InterruptedException {
        // Given
        final int[] executionCount = {0};
        Runnable task1 = () -> executionCount[0]++;
        Runnable task2 = () -> executionCount[0] += 10;

        // When
        timerService.scheduleTimeout(testLobbyCode, task1);
        Thread.sleep(100); // Small delay
        timerService.scheduleTimeout(testLobbyCode, task2); // Should cancel task1

        Thread.sleep(5500);

        // Then
        // Only task2 should execute (adding 10), task1 should be cancelled
        assertEquals(10, executionCount[0], "Only second task should execute");
    }

    @Test
    void cancelTimeout_ShouldCancelScheduledTask() throws InterruptedException {
        // Given
        final boolean[] executed = {false};
        Runnable task = () -> executed[0] = true;

        timerService.scheduleTimeout(testLobbyCode, task);

        // When
        timerService.cancelTimeout(testLobbyCode);

        // Wait to ensure task would have executed
        Thread.sleep(5500);

        // Then
        assertFalse(executed[0], "Timeout task should have been cancelled");
    }

    @Test
    void cancelTimeout_ShouldDoNothing_WhenNoTaskScheduled() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> timerService.cancelTimeout("NONEXISTENT"));
    }

    @Test
    void startTimerBroadcast_ShouldSendTimerUpdates() throws InterruptedException {
        // Given
        int timeLimit = 30;

        // When
        timerService.startTimerBroadcast(testLobbyCode, testSession, timeLimit);

        // Wait for at least one broadcast
        Thread.sleep(1500);

        // Then
        ArgumentCaptor<TimerUpdateMessage> messageCaptor = ArgumentCaptor.forClass(TimerUpdateMessage.class);
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);

        verify(simpMessagingTemplate, atLeastOnce()).convertAndSend(
                destinationCaptor.capture(),
                messageCaptor.capture()
        );

        // Verify destination
        String destination = destinationCaptor.getValue();
        assertEquals("/topic/game/" + testLobbyCode, destination);

        // Verify message content
        TimerUpdateMessage message = messageCaptor.getValue();
        assertNotNull(message);
        assertEquals(0, message.getCurrentQuestionIndex());
        assertTrue(message.getRemainingTime() <= timeLimit && message.getRemainingTime() >= 0);

        // Cleanup
        timerService.cancelTimerBroadcast(testLobbyCode);
    }

    @Test
    void cancelTimerBroadcast_ShouldStopBroadcasting() throws InterruptedException {
        // Given
        int timeLimit = 30;
        timerService.startTimerBroadcast(testLobbyCode, testSession, timeLimit);

        // Wait for some broadcasts
        Thread.sleep(1500);

        // When
        timerService.cancelTimerBroadcast(testLobbyCode);

        // Clear invocations after cancellation
        clearInvocations(simpMessagingTemplate);

        // Wait to see if more broadcasts happen
        Thread.sleep(1500);

        // Then - No more broadcasts should occur
        verifyNoInteractions(simpMessagingTemplate);
    }

    @Test
    void cancelTimerBroadcast_ShouldDoNothing_WhenNoTimerRunning() {
        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> timerService.cancelTimerBroadcast("NONEXISTENT"));
    }

    @Test
    void startTimerBroadcast_ShouldCancelPreviousTimer_WhenCalledTwice() throws InterruptedException {
        // Given
        GameSession session1 = new GameSession(testLobbyCode, 1L, "Host", "Guest");
        session1.setCurrentQuestionIndex(0);
        session1.setQuestionStartTime(System.currentTimeMillis());

        GameSession session2 = new GameSession(testLobbyCode, 1L, "Host", "Guest");
        session2.setCurrentQuestionIndex(1);
        session2.setQuestionStartTime(System.currentTimeMillis());

        // When
        timerService.startTimerBroadcast(testLobbyCode, session1, 30);
        Thread.sleep(500);
        timerService.startTimerBroadcast(testLobbyCode, session2, 30);

        Thread.sleep(1500);

        // Then
        ArgumentCaptor<TimerUpdateMessage> messageCaptor = ArgumentCaptor.forClass(TimerUpdateMessage.class);
        verify(simpMessagingTemplate, atLeastOnce()).convertAndSend(
                anyString(),
                messageCaptor.capture()
        );

        // Latest messages should be from session2 (question index 1)
        TimerUpdateMessage lastMessage = messageCaptor.getValue();
        assertEquals(1, lastMessage.getCurrentQuestionIndex());

        // Cleanup
        timerService.cancelTimerBroadcast(testLobbyCode);
    }

    @Test
    void cancelAllTimers_ShouldCancelBothTimerAndTimeout() throws InterruptedException {
        // Given
        final boolean[] timeoutExecuted = {false};
        Runnable timeoutTask = () -> timeoutExecuted[0] = true;

        timerService.startTimerBroadcast(testLobbyCode, testSession, 30);
        timerService.scheduleTimeout(testLobbyCode, timeoutTask);

        Thread.sleep(500);

        // When
        timerService.cancelAllTimers(testLobbyCode);

        clearInvocations(simpMessagingTemplate);

        // Wait to ensure both are cancelled
        Thread.sleep(6000);

        // Then
        verifyNoInteractions(simpMessagingTemplate);
        assertFalse(timeoutExecuted[0], "Timeout should be cancelled");
    }

    @Test
    void startTimerBroadcast_ShouldStopAutomatically_WhenTimeExpires() throws InterruptedException {
        // Given
        testSession.setQuestionStartTime(System.currentTimeMillis() - 29000); // Started 29 seconds ago
        int timeLimit = 30; // Will expire in ~1 second

        // When
        timerService.startTimerBroadcast(testLobbyCode, testSession, timeLimit);

        // Wait for timer to expire naturally
        Thread.sleep(2000);

        clearInvocations(simpMessagingTemplate);

        // Wait more to ensure it stopped
        Thread.sleep(2000);

        // Then - No more broadcasts should occur
        verifyNoInteractions(simpMessagingTemplate);
    }
}
