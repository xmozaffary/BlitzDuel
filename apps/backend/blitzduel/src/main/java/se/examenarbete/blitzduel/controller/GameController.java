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
    public void startGame(@DestinationVariable String lobbyCode){
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
                question.getOptions()
        );

        simpMessagingTemplate.convertAndSend(
                "/topic/lobby/" + lobbyCode + "/start",
                "GAME_STARTING"
        );

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                dto
        );
    }


    @MessageMapping("/game/{lobbyCode}/answer")
    public void submitAnswer(@DestinationVariable String lobbyCode, SubmitAnswerRequest request){
        System.out.println("Answer form: " + request.getName() +
                ": " + request.getAnswerIndex());


        GameUpdateResponse response = gameService.submitAnswer(
                lobbyCode,
                request.getName(),
                request.getAnswerIndex()
        );

        if(response.getStatus().equals("WAITING")) {
            System.out.println("Waiting for other player...");
            return;
        }




        simpMessagingTemplate.convertAndSend(
                "/topic/game/" + lobbyCode,
                response
        );

        if (response.getStatus().equals("BOTH_ANSWERED")){
            Question nextQuestion = gameService.getCurrentQuestion(lobbyCode);
            GameSession session = gameService.getGameSession(lobbyCode).get();

            QuestionDTO nextDto = new QuestionDTO(
                    "QUESTION",
                    session.getCurrentQuestionIndex(),
                    nextQuestion.getQuestionText(),
                    nextQuestion.getOptions()
            );

            simpMessagingTemplate.convertAndSend(
                    "/topic/game/" + lobbyCode,
                    nextDto
            );
        }
        System.out.println("Sent reponse with status: " + response.getStatus());
    }
}
