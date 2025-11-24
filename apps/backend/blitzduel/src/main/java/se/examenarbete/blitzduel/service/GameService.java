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
    private final Map<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    private final QuizService quizService;

    public GameService(QuizService quizService) {
        this.quizService = quizService;
    }

    public GameSession startGame(String lobbyCode, Long quizId, String player1, String player2) {

        GameSession session = new GameSession(lobbyCode, quizId, player1, player2);
        session.setCurrentQuestionIndex(0);
        session.setQuestionStartTime(System.currentTimeMillis());
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

    public GameUpdateResponse submitAnswer(String lobbyCode, String playerNickname, Integer answerIndex) {
        GameSession session = getGameSession(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if(playerNickname.equals(session.getHostName())){
            session.setHostNameAnswer(answerIndex);
        } else if(playerNickname.equals(session.getGuestName())) {
            session.setGuestNameAnswer(answerIndex);
        }

        if (!session.bothAnswered()){
            GameUpdateResponse response = new GameUpdateResponse();
            response.setStatus("WAITING");
            return response;
        }

        Question currentQuestion = getCurrentQuestion(lobbyCode);
        int correctAnswer = currentQuestion.getCorrectAnswerIndex();

        boolean player1Correct = session.getHostNameAnswer() == correctAnswer;
        boolean player2Correct = session.getGuestNameAnswer() == correctAnswer;

        if (session.getHostNameAnswer() == correctAnswer){
            session.setHostNameScore(session.getHostNameScore() + 1);
        }

        if (session.getGuestNameAnswer() == correctAnswer){
            session.setGuestNameScore(session.getGuestNameScore() + 1);
        }

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        session.resetAnswers();
        // session.setQuestionStartTime(System.currentTimeMillis());


        GameUpdateResponse response =  new GameUpdateResponse();
        response.setStatus("BOTH_ANSWERED");
        response.setCorrectAnswerIndex(correctAnswer);
        response.setHostCorrect(player1Correct);
        response.setGuestCorrect(player2Correct);
        response.setHostScore(session.getHostNameScore());
        response.setGuestScore(session.getGuestNameScore());
        response.setHostName(session.getHostName());
        response.setGuestName(session.getGuestName());

        if (session.isGameOver()){
            response.setStatus("GAME_OVER");
        } else {
            response.setStatus("BOTH_ANSWERED");
        }

        return response;
    }

    public void scheduleTimeout(String lobbyCode, Runnable onTimeout){
        ScheduledFuture<?> existingTask = timeoutTasks.get(lobbyCode);
        if (existingTask != null && !existingTask.isDone()) {
            existingTask.cancel(false);
        }


        ScheduledFuture<?> task = scheduler.schedule(onTimeout, 5, TimeUnit.SECONDS);
        timeoutTasks.put(lobbyCode, task);
    }



            public void cancelTimeout(String lobbyCode) {
                ScheduledFuture<?> task = timeoutTasks.get(lobbyCode);
                if(task != null && !task.isDone()){
                    task.cancel(false);
                    timeoutTasks.remove(lobbyCode);
                }
            }
}