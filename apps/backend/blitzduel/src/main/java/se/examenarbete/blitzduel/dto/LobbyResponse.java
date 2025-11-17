package se.examenarbete.blitzduel.dto;

public class LobbyResponse {

    private String lobbyCode;
    private String status;
    private Long quizId;
    private String hostName;
    private String guestName;

    public LobbyResponse() {
    }

    public LobbyResponse(String lobbyCode, String status, Long quizId, String hostName, String guestName){
        this.lobbyCode = lobbyCode;
        this.status = status;
        this.quizId = quizId;
        this.hostName = hostName;
        this.guestName = guestName;
    }


    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
