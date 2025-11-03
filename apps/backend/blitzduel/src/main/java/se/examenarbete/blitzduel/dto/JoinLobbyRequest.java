package se.examenarbete.blitzduel.dto;

public class JoinLobbyRequest {
    private String nickname;
    public JoinLobbyRequest(){}

    public JoinLobbyRequest(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
}
