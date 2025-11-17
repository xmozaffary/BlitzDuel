package se.examenarbete.blitzduel.model;


import java.time.LocalDateTime;

public class Lobby {

    public enum Status {
        WAITING,
        READY,
        IN_PROGRESS,
        FINISHED
    }

    private final String lobbyCode;
    private final Long quizId;
    private final String hostName;
    private String guestName;
    private Status status;
    private final LocalDateTime createdAt;



    public Lobby(String lobbyCode, Long quizId, String hostName){
        this.lobbyCode = lobbyCode;
        this.quizId = quizId;
        this.hostName = hostName;
        this.status = Status.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public String getLobbyCode(){
        return lobbyCode;
    }

    public String getHostName(){
        return hostName;
    }

    public String getGuestName(){
        return guestName;
    }

    public Status getStatus(){
        return status;
    }

    public Long getQuizId() {
        return quizId;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setGuestName(String guestName){
        this.guestName = guestName;
    }

    public void setStatus(Status status ) {
        this.status = status;
    }

    public boolean isFull() {
        return guestName != null;
    }

    public boolean isWaiting() {
        return status == Status.WAITING;
    }

    public boolean isReady() {
        return status == Status.READY;
    }


    @Override
    public String toString(){
        return "Lobby{" +
                "code='" + lobbyCode + '\'' +
                ", player1='" + hostName + '\'' +
                ", player2='" + guestName + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}