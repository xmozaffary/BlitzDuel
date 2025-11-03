package se.examenarbete.blitzduel.dto;

public class LobbyResponse {

    private String code;
    private String status;
    private String player1;
    private String player2;

    public LobbyResponse() {
    }

    public LobbyResponse(String code, String status, String player1, String player2){
        this.code = code;
        this.status = status;
        this.player1 = player1;
        this.player2 = player2;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
