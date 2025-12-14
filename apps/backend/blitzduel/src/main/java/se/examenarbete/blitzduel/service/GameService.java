package se.examenarbete.blitzduel.service;

import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Question;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class GameService {

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();

    private final QuizService quizService;
    private final TimerService timerService;
    private final AnswerService answerService;

    public GameService(QuizService quizService, TimerService timerService, AnswerService answerService) {
        this.quizService = quizService;
        this.timerService = timerService;
        this.answerService = answerService;
    }

    public GameSession startGame(String lobbyCode, Long quizId, String player1, String player2, Long hostUserId, Long guestUserId) {

        GameSession session = new GameSession(lobbyCode, quizId, player1, player2);
        session.setCurrentQuestionIndex(0);
        session.setQuestionStartTime(System.currentTimeMillis());
        session.setHostUserId(hostUserId);
        session.setGuestUserId(guestUserId);
        gameSessions.put(lobbyCode, session);
        return session;
    }

    public Optional<GameSession> getGameSession(String lobbyCode) {
        return Optional.ofNullable(gameSessions.get(lobbyCode));
    }

    public Question getCurrentQuestion(String lobbyCode){
        GameSession session = getGameSession(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        List<Question> questions = quizService.getQuestionsByQuizId(session.getQuizId());

        return questions.get(session.getCurrentQuestionIndex());
    }

    public GameUpdateResponse submitAnswer(String lobbyCode, String name, Integer answerIndex) {
        GameSession session = getGameSession(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        return answerService.submitAnswer(lobbyCode, session, name, answerIndex);
    }
    
    public void scheduleTimeout(String lobbyCode, Runnable onTimeout) {
        timerService.scheduleTimeout(lobbyCode, onTimeout);
    }
    public void cancelTimeout(String lobbyCode) {
        timerService.cancelTimeout(lobbyCode);
    }


    public void startTimerBroadcast(String lobbyCode, int timeLimit){
        GameSession session = getGameSession(lobbyCode).orElse(null);
        if (session != null){
            timerService.startTimerBroadcast(lobbyCode, session, timeLimit);
        }
    }

    public void cancelTimerBroadcast(String lobbyCode) {
        timerService.cancelTimerBroadcast(lobbyCode);
    }

    public GameUpdateResponse handleTimeout(String lobbyCode) {
        GameSession session = getGameSession(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        return answerService.handleTimeout(lobbyCode, session);
    }
}


