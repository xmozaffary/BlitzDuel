package se.examenarbete.blitzduel.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import se.examenarbete.blitzduel.dto.GameUpdateResponse;
import se.examenarbete.blitzduel.dto.QuestionDTO;
import se.examenarbete.blitzduel.dto.SubmitAnswerRequest;
import se.examenarbete.blitzduel.model.GameSession;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.service.GameService;
import se.examenarbete.blitzduel.service.LobbyService;

@Controller
public class GameController {

    private final GameService gameService;
    private final LobbyService lobbyService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public GameController(GameService gameService, LobbyService lobbyService, SimpMessagingTemplate simpMessagingTemplate) {
        this.gameService = gameService;
        this.lobbyService = lobbyService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/game/{lobbyCode}/start")
    public void startGame(@DestinationVariable String lobbyCode) {
        System.out.println("Starting game for lobby: " + lobbyCode);

        Lobby lobby = lobbyService.getLobby(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Lobby not found"));

        GameSession session = gameService.startGame(
                lobbyCode,
                lobby.getQuizId(),
                lobby.getHostName(),
                lobby.getGuestName()
        );

        Question question = gameService.getCurrentQuestion(lobbyCode);

        QuestionDTO dto = new QuestionDTO(
                "QUESTION",
                session.getCurrentQuestionIndex(),
                question.getQuestionText(),
                question.getOptions(),
                lobby.getHostName(),
                lobby.getGuestName()
        );

        dto.setTimeLimit(5);

        simpMessagingTemplate.convertAndSend(
                "/topic/lobby/" + lobbyCode + "/start",
                "GAME_STARTING"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sätt startTime PRECIS innan send
        long startTime = System.currentTimeMillis();
        session.setQuestionStartTime(startTime);
        dto.setStartTime(startTime);

        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                dto
        );

        // ← NYTT: Starta timer broadcast
        gameService.startTimerBroadcast(lobbyCode, 5);

        gameService.scheduleTimeout(lobbyCode, () -> handleTimeout(lobbyCode));
    }

    @MessageMapping("/game/{lobbyCode}/answer")
    public void submitAnswer(@DestinationVariable String lobbyCode, SubmitAnswerRequest request) {
        System.out.println("Answer form: " + request.getName() +
                ": " + request.getAnswerIndex());

        GameUpdateResponse response = gameService.submitAnswer(
                lobbyCode,
                request.getName(),
                request.getAnswerIndex()
        );

        if (response.getStatus().equals("WAITING")) {
            System.out.println("Waiting for other player...");
            return;
        }

        // ← ÄNDRAT: Stoppa både timeout OCH timer broadcast
        gameService.cancelTimeout(lobbyCode);
        gameService.cancelTimerBroadcast(lobbyCode);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                response
        );

        System.out.println("Sent response with status: " + response.getStatus());

        if (response.getStatus().equals("BOTH_ANSWERED")) {
            sendNextQuestion(lobbyCode);
        }
    }

    private void handleTimeout(String lobbyCode) {
        System.out.println("Timeout for lobby: " + lobbyCode);

        // ← NYTT: Stoppa timer broadcast vid timeout
        gameService.cancelTimerBroadcast(lobbyCode);

        // ← ÄNDRAT: Använd GameService.handleTimeout istället för duplikerad logik
        GameUpdateResponse response = gameService.handleTimeout(lobbyCode);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                response
        );

        if (response.getStatus().equals("BOTH_ANSWERED")) {
            sendNextQuestion(lobbyCode);
        }
    }

    private void sendNextQuestion(String lobbyCode) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);

                GameSession session = gameService.getGameSession(lobbyCode).get();

                if(session.isGameOver()) {
                    System.out.println("Game is over, sending final score");
                    sendFinalScore(lobbyCode);
                    return;
                }

                Question nextQuestion = gameService.getCurrentQuestion(lobbyCode);

                QuestionDTO nextDto = new QuestionDTO(
                        "QUESTION",
                        session.getCurrentQuestionIndex(),
                        nextQuestion.getQuestionText(),
                        nextQuestion.getOptions(),
                        session.getHostName(),
                        session.getGuestName()
                );

                nextDto.setTimeLimit(5);

                // Sätt startTime PRECIS innan send
                long startTime = System.currentTimeMillis();
                session.setQuestionStartTime(startTime);
                nextDto.setStartTime(startTime);

                simpMessagingTemplate.convertAndSend(
                        "/topic/game/" + lobbyCode,
                        nextDto
                );

                System.out.println("Sent next question after delay");

                // ← NYTT: Starta timer broadcast för nästa fråga
                gameService.startTimerBroadcast(lobbyCode, 5);

                gameService.scheduleTimeout(lobbyCode, () -> handleTimeout(lobbyCode));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendFinalScore(String lobbyCode) {
        GameSession session = gameService.getGameSession(lobbyCode)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        GameUpdateResponse finalScore = new GameUpdateResponse();
        finalScore.setStatus("GAME_OVER");
        finalScore.setHostScore(session.getHostNameScore());
        finalScore.setGuestScore(session.getGuestNameScore());
        finalScore.setHostName(session.getHostName());
        finalScore.setGuestName(session.getGuestName());

        System.out.println("Sending GAME_OVER: " + session.getHostName() + " " + session.getHostNameScore() + " - " +
                session.getGuestNameScore() + " " + session.getGuestName());

        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                finalScore
        );
    }
}