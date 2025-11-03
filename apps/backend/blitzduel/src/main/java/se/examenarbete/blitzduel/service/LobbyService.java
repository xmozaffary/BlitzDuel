package se.examenarbete.blitzduel.service;


import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.model.Lobby;
import se.examenarbete.blitzduel.util.RandomCodeGenerator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyService {

    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();
    private final RandomCodeGenerator randomCodeGenerator;

    public LobbyService(RandomCodeGenerator randomCodeGenerator){
        this.randomCodeGenerator = randomCodeGenerator;
    }

    public Lobby createLobby(String nickname){
        String code = randomCodeGenerator.generate();
        while (lobbies.containsKey(code)){
            code = randomCodeGenerator.generate();
        }
        Lobby lobby = new Lobby(code, nickname);
        lobbies.put(code, lobby);
        System.out.println("Lobby created: " + lobby);
        return lobby;
    }


    public Optional<Lobby> joinLobby(String code, String nickname) {
        Lobby lobby = lobbies.get(code);

        if(lobby == null) {
            System.out.println("Lobby not found: " + code);
            return Optional.empty();
        }
        if(lobby.isFull()) {
            System.out.println("Lobby is full: " + code);
            return Optional.empty();
        }

        lobby.setPlayer2Nickname(nickname);
        lobby.setStatus(Lobby.Status.READY);

        System.out.println("Player joined: " + lobby);
        return Optional.of(lobby);

    }
    public Optional<Lobby> getLobby(String code) {
        return Optional.ofNullable(lobbies.get(code));
    }

}




