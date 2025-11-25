package se.examenarbete.blitzduel.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.TimerUpdateMessage;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class TimerService {

    private final Map<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> timerTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final SimpMessagingTemplate simpMessagingTemplate;

    public TimerService(SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void startTimerBroadcast(String lobbyCode, GameSession session, int timeLimit){
        cancelTimerBroadcast(lobbyCode);

        int currentQuestionIndex = session.getCurrentQuestionIndex();

        ScheduledFuture<?> timerTask = scheduler.scheduleAtFixedRate(() -> {
            try{
                long startTime = session.getQuestionStartTime();
                long now = System.currentTimeMillis();
                long elapsed = (now - startTime) / 1000;
                int remainingTime = Math.max(0, timeLimit - (int) elapsed);

                TimerUpdateMessage timerMessage = new TimerUpdateMessage(
                        remainingTime,
                        currentQuestionIndex
                );

                simpMessagingTemplate.convertAndSend(
                        "/topic/game/" + lobbyCode,
                        timerMessage
                );

                System.out.println("Timer broadcast - Lobby: " + lobbyCode +
                        ", Remaining: " + remainingTime + "s");

                if (remainingTime <= 0){
                    cancelTimerBroadcast(lobbyCode);
                }

            } catch (Exception e) {
                System.err.println("Error in timer broadcast: " + e.getMessage());
                cancelTimerBroadcast(lobbyCode);
            }
        }, 0, 1, TimeUnit.SECONDS);
        timerTasks.put(lobbyCode, timerTask);
    }

    public void cancelTimerBroadcast(String lobbyCode){
        ScheduledFuture<?> task = timerTasks.get(lobbyCode); // ← FIX: Använd timerTasks
        if (task != null && !task.isDone()){
            task.cancel(false);
            timerTasks.remove(lobbyCode); // ← FIX: Använd timerTasks
            System.out.println("Timer broadcast cancelled for lobby: " + lobbyCode);
        }
    }

    public void scheduleTimeout(String lobbyCode, Runnable onTimeout) {
        ScheduledFuture<?> existingTask = timeoutTasks.get(lobbyCode);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }
        ScheduledFuture<?> task = scheduler.schedule(onTimeout, 5, TimeUnit.SECONDS);
        timeoutTasks.put(lobbyCode, task);
    }

    public void cancelTimeout(String lobbyCode){
        ScheduledFuture<?> task = timeoutTasks.get(lobbyCode);
        if (task != null && !task.isDone()){
            task.cancel(false);
            timeoutTasks.remove(lobbyCode);
        }
    }

    public void cancelAllTimers(String lobbyCode) {
        cancelTimerBroadcast(lobbyCode);
        cancelTimeout(lobbyCode);
    }
}