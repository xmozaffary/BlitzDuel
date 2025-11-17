package se.examenarbete.blitzduel.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import se.examenarbete.blitzduel.dto.CreateLobbyRequest;
import se.examenarbete.blitzduel.dto.JoinLobbyRequest;
import se.examenarbete.blitzduel.dto.LobbyResponse;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.service.LobbyService;

import java.security.Principal;
import java.util.Optional;

@Controller
public class LobbyController {
    private final LobbyService lobbyService;
    private final SimpMessagingTemplate messagingTemplate;

    public LobbyController(LobbyService lobbyService, SimpMessagingTemplate messagingTemplate){
        this.lobbyService = lobbyService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/lobby/create")
    @SendToUser("/queue/lobby")
    public LobbyResponse createLobby(CreateLobbyRequest request){
        System.out.println("Create lobby request from: " + request.getName());

        Lobby lobby = lobbyService.createLobby(request.getName(), request.getQuizId());


        return new LobbyResponse(
                lobby.getLobbyCode(),
                lobby.getStatus().name(),
                lobby.getQuizId(),
                lobby.getHostName(),
                null
        );
    }

    @MessageMapping("/lobby/{code}/join")
    public void joinLobby(
            @DestinationVariable String code,
            JoinLobbyRequest request,
            Principal principal
    ) {
        System.out.println("Join lobby request: " + code + " from: " + request.getName());

        Optional<Lobby> lobbyOpt = lobbyService.joinLobby(code, request.getName());

        if(lobbyOpt.isEmpty()){
            System.out.println("=== SENDING FULL MESSAGE ===");
            System.out.println("Principal name: " + principal.getName());


            LobbyResponse errorResponse = new LobbyResponse(code, "FULL", null, null, null);

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/lobby",
                    errorResponse
            );
            System.out.println("Sent FULL message to " + request.getName());
            return;
        }

        Lobby lobby = lobbyOpt.get();

        LobbyResponse successResponse = new LobbyResponse(
                lobby.getLobbyCode(),
                lobby.getStatus().name(),
                lobby.getQuizId(),
                lobby.getHostName(),
                lobby.getGuestName()
        );
        messagingTemplate.convertAndSend(
                "/topic/lobby/" + code,
                successResponse
        );
        System.out.println("Sent SUCCESS message to /topic/lobby/" + code);
    }
}
