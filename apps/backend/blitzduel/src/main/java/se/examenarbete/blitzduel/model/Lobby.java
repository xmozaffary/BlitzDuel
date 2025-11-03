package se.examenarbete.blitzduel.model;


import java.time.LocalDateTime;

public class Lobby {

    public enum Status {
        WAITING,
        READY,
        IN_PROGRESS,
        FINISHED
    }

    private final String code;
    private final String player1Nickname;
    private String player2Nickname;
    private Status status;
    private final LocalDateTime createdAt;



    public Lobby(String code, String player1Nickname){
        this.code = code;
        this.player1Nickname = player1Nickname;
        this.status = Status.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public String getCode(){
        return code;
    }

    public String getPlayer1Nickname(){
        return player1Nickname;
    }

    public String getPlayer2Nickname(){
        return player2Nickname;
    }

    public Status getStatus(){
        return status;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setPlayer2Nickname(String player2Nickname){
        this.player2Nickname = player2Nickname;
    }

    public void setStatus(Status status ) {
        this.status = status;
    }

    public boolean isFull() {
        return player2Nickname != null;
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
                "code='" + code + '\'' +
                ", player1='" + player1Nickname + '\'' +
                ", player2='" + player2Nickname + '\'' +
                ", status='" + status + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}