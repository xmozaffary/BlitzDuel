package se.examenarbete.blitzduel.dto;

public class GameUpdateResponse {

    private String status;
    private Integer correctAnswer;
    private Boolean player1Correct;
    private Boolean player2Correct;
    private Integer player1Score;
    private Integer player2Score;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Boolean getPlayer1Correct() {
        return player1Correct;
    }

    public void setPlayer1Correct(Boolean player1Correct) {
        this.player1Correct = player1Correct;
    }

    public Boolean getPlayer2Correct() {
        return player2Correct;
    }

    public void setPlayer2Correct(Boolean player2Correct) {
        this.player2Correct = player2Correct;
    }

    public Integer getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(Integer player1Score) {
        this.player1Score = player1Score;
    }

    public Integer getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(Integer player2Score) {
        this.player2Score = player2Score;
    }

    public GameUpdateResponse() {

    }
}
