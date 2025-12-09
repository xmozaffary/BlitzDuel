package se.examenarbete.blitzduel.service;

import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Question;

@Service
public class AnswerService {

    private final QuizService quizService;

    public AnswerService(QuizService quizService) {
        this.quizService = quizService;
    }


    public GameUpdateResponse submitAnswer(String lobbyCode, GameSession session, String name, Integer answerIndex){
        if (name.equals(session.getHostName())){
            session.setHostNameAnswer(answerIndex);
        }else if (name.equals(session.getGuestName())){
            session.setGuestNameAnswer(answerIndex);
        }

        if (!session.bothAnswered()){
            GameUpdateResponse response = new GameUpdateResponse();
            response.setStatus("WAITING");
            return response;
        }

        return processBothAnswered(lobbyCode, session);
    }

    private GameUpdateResponse processBothAnswered(String lobbyCode,GameSession session) {
        Question currentQuestion = getCurrentQuestion(lobbyCode, session);
        int correctAnswer = currentQuestion.getCorrectAnswerIndex();

        boolean hostCorrect = session.getHostNameAnswer() == correctAnswer;
        boolean guestCorrect = session.getGuestNameAnswer() == correctAnswer;

        if (hostCorrect) {
            session.setHostNameScore(session.getHostNameScore() + 1);
        }
        if (guestCorrect) {
            session.setGuestNameScore(session.getGuestNameScore() + 1);
        }

        GameUpdateResponse response = buildGameUpdateResponse(session, correctAnswer, hostCorrect, guestCorrect);

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        session.resetAnswers();

        return response;
    }


    public GameUpdateResponse handleTimeout(String lobbyCode, GameSession session){
        Question currentQuestion = getCurrentQuestion(lobbyCode, session);
        int correctAnswer = currentQuestion.getCorrectAnswerIndex();

        boolean hostCorrect = session.getHostNameAnswer() != null &&
                session.getHostNameAnswer() == correctAnswer;

        boolean guestCorrect = session.getGuestNameAnswer() != null &&
                session.getGuestNameAnswer() == correctAnswer;

        if (hostCorrect){
            session.setHostNameScore(session.getHostNameScore() + 1);
        }

        if (guestCorrect){
            session.setGuestNameScore(session.getGuestNameScore() + 1);
        }

        GameUpdateResponse response = buildGameUpdateResponse(session, correctAnswer, hostCorrect, guestCorrect);

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1 );
        session.resetAnswers();

        return response;
    }

    private GameUpdateResponse buildGameUpdateResponse(GameSession session, int correctAnswer,
                                                       boolean hostCorrect, boolean guestCorrect){
        GameUpdateResponse response = new GameUpdateResponse();
        response.setCorrectAnswerIndex(correctAnswer);
        response.setHostCorrect(hostCorrect);
        response.setGuestCorrect(guestCorrect);
        response.setHostScore(session.getHostNameScore());
        response.setGuestScore(session.getGuestNameScore());
        response.setHostName(session.getHostName());
        response.setGuestName(session.getGuestName());
        response.setHostAnswerIndex(session.getHostNameAnswer());
        response.setGuestAnswerIndex(session.getGuestNameAnswer());

        if (session.isGameOver()){
            response.setStatus("GAME_OVER");
        } else {
            response.setStatus("BOTH_ANSWERED");
        }

        return response;
    }

    private Question getCurrentQuestion(String lobbyCode, GameSession session){
        return quizService.getQuestionsByQuizId(session.getQuizId()).get(session.getCurrentQuestionIndex());
    }
}
