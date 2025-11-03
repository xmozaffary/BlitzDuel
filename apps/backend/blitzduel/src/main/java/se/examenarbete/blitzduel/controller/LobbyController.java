package se.examenarbete.blitzduel.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import se.examenarbete.blitzduel.dto.CreateLobbyRequest;
import se.examenarbete.blitzduel.dto.JoinLobbyRequest;
import se.examenarbete.blitzduel.dto.LobbyResponse;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.service.LobbyService;

@Controller
public class LobbyController {
    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService){
        this.lobbyService = lobbyService;
    }

    @MessageMapping("/lobby/create")
    @SendToUser("/queue/lobby")
    public LobbyResponse createLobby(CreateLobbyRequest request){
        System.out.println("Create lobby request from: " + request.getNickname());

        Lobby lobby = lobbyService.createLobby(request.getNickname());

        return new LobbyResponse(
                lobby.getCode(),
                lobby.getStatus().name(),
                lobby.getPlayer1Nickname(),
                null
        );
    }

    @MessageMapping("/lobby/{code}/join")
    @SendTo("/topic/lobby/{code}")
    public LobbyResponse joinLobby(
            @DestinationVariable String code,
            JoinLobbyRequest request
    ) {
        System.out.println("Join lobby request: " + code + " from: " + request.getNickname());

        Lobby lobby = lobbyService.joinLobby(code, request.getNickname())
                .orElseThrow(() -> new RuntimeException("Lobby full or not found: " + code));

        return new LobbyResponse(
                lobby.getCode(),
                lobby.getStatus().name(),
                lobby.getPlayer1Nickname(),
                lobby.getPlayer2Nickname()
        );
    }
}
