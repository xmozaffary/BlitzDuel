package se.examenarbete.blitzduel.dto;

public class QuizResultResponse {
    private String lobbyCode;
    private String winner;
    private int hostNameScore;
    private int guestNameScore;
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

    public int getHostNameScore() {
        return hostNameScore;
    }

    public void setHostNameScore(int hostNameScore) {
        this.hostNameScore = hostNameScore;
    }

    public int getGuestNameScore() {
        return guestNameScore;
    }

    public void setGuestNameScore(int guestNameScore) {
        this.guestNameScore = guestNameScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public QuizResultResponse(String lobbyCode, String winner, int hostNameScore, int guestNameScore, String status) {
        this.lobbyCode = lobbyCode;
        this.winner = winner;
        this.hostNameScore = hostNameScore;
        this.guestNameScore = guestNameScore;
        this.status = status;
    }
}
