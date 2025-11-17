package se.examenarbete.blitzduel.dto;

public class JoinLobbyRequest {
    private String name;

    public JoinLobbyRequest(){}

    public JoinLobbyRequest(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
