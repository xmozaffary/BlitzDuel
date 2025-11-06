package se.examenarbete.blitzduel.dto;

public class QuizResultResponse {
    private String lobbyCode;
    private String winner;
    private int player1Score;
    private int player2Score;
    private String status;

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public QuizResultResponse(String lobbyCode, String winner, int player1Score, int player2Score, String status) {
        this.lobbyCode = lobbyCode;
        this.winner = winner;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
        this.status = status;
    }
}
