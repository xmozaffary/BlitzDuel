package se.examenarbete.blitzduel.service;

import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Question;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();

    private final QuizService quizService;

    public GameService(QuizService quizService) {
        this.quizService = quizService;
    }

    public GameSession startGame(String lobbyCode, Long quizId, String player1, String player2) {

        GameSession session = new GameSession(lobbyCode, quizId, player1, player2);
        session.setCurrentQuestionIndex(0);
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

        if(playerNickname.equals(session.getPlayer1Nickname())){
            session.setPlayer1Answer(answerIndex);
        } else if(playerNickname.equals(session.getPlayer2Nickname())) {
            session.setPlayer2Answer(answerIndex);
        }

        if (!session.bothAnswered()){
            GameUpdateResponse response = new GameUpdateResponse();
            response.setStatus("WAITING");
            return response;
        }

        Question currentQuestion = getCurrentQuestion(lobbyCode);
        int correctAnswer = currentQuestion.getCorrectAnswerIndex();

        boolean player1Correct = session.getPlayer1Answer() == correctAnswer;
        boolean player2Correct = session.getPlayer2Answer() == correctAnswer;

        if (session.getPlayer1Answer() == correctAnswer){
            session.setPlayer1Score(session.getPlayer1Score() + 1);
        }

        if (session.getPlayer2Answer() == correctAnswer){
            session.setPlayer2Score(session.getPlayer2Score() + 1);
        }

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        session.resetAnswers();

        GameUpdateResponse response =  new GameUpdateResponse();
        response.setStatus("BOTH_ANSWERED");
        response.setCorrectAnswer(correctAnswer);
        response.setPlayer1Correct(player1Correct);
        response.setPlayer2Correct(player2Correct);
        response.setPlayer1Score(session.getPlayer1Score());
        response.setPlayer2Score(session.getPlayer2Score());

        if (session.isGameOver()){
            response.setStatus("GAME_OVER");
        } else {
            response.setStatus("BOTH_ANSWERED");
        }

        return response;
    }
}