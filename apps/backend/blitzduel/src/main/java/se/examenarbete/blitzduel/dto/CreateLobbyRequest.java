package se.examenarbete.blitzduel.dto;

public class CreateLobbyRequest {

    private String nickname;

    public CreateLobbyRequest() {
    }

    public CreateLobbyRequest(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
