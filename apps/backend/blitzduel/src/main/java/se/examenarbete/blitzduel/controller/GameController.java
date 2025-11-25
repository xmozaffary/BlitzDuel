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
        gameService.cancelTimeout(lobbyCode);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                response
        );

        System.out.println("Sent reponse with status: " + response.getStatus());
        if (response.getStatus().equals("BOTH_ANSWERED")) {

            new Thread(() -> {

                try {
                    Thread.sleep(1000);
                    Question nextQuestion = gameService.getCurrentQuestion(lobbyCode);
                    GameSession session = gameService.getGameSession(lobbyCode).get();

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
                    gameService.scheduleTimeout(lobbyCode, () -> handleTimeout(lobbyCode));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void handleTimeout(String lobbyCode) {
        System.out.println("Timeout for lobby: " + lobbyCode);

        GameSession session = gameService.getGameSession(lobbyCode).orElse(null);
        if (session == null) return;

        Question currentQuestion = gameService.getCurrentQuestion(lobbyCode);
        int correctAnswer = currentQuestion.getCorrectAnswerIndex();

        boolean hostCorrect = session.getHostNameAnswer() != null &&
                session.getHostNameAnswer() == correctAnswer;

        boolean guestCorrect = session.getGuestNameAnswer() != null &&
                session.getGuestNameAnswer() == correctAnswer;

        if (hostCorrect) {
            session.setHostNameScore(session.getHostNameScore() + 1);
        }
        if (guestCorrect) {
            session.setGuestNameScore(session.getGuestNameScore() + 1);
        }

        session.setCurrentQuestionIndex(session.getCurrentQuestionIndex() + 1);
        session.resetAnswers();

        GameUpdateResponse response = new GameUpdateResponse();
        response.setCorrectAnswerIndex(correctAnswer);
        response.setHostCorrect(hostCorrect);
        response.setGuestCorrect(guestCorrect);
        response.setHostScore(session.getHostNameScore());
        response.setGuestScore(session.getGuestNameScore());
        response.setHostName(session.getHostName());
        response.setGuestName(session.getGuestName());

        if (session.isGameOver()) {
            response.setStatus("GAME_OVER");
        } else {
            response.setStatus("BOTH_ANSWERED");
        }

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

                gameService.scheduleTimeout(lobbyCode, () -> handleTimeout(lobbyCode));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}