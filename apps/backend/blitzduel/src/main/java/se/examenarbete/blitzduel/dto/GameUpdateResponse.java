package se.examenarbete.blitzduel.dto;

public class GameUpdateResponse {

    private String status;
    private Integer correctAnswerIndex;
    private Boolean hostCorrect;
    private Boolean guestCorrect;
    private Integer hostScore;
    private Integer guestScore;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(Integer correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public Boolean getHostCorrect() {
        return hostCorrect;
    }

    public void setHostCorrect(Boolean hostCorrect) {
        this.hostCorrect = hostCorrect;
    }

    public Boolean getGuestCorrect() {
        return guestCorrect;
    }

    public void setGuestCorrect(Boolean guestCorrect) {
        this.guestCorrect = guestCorrect;
    }

    public Integer getHostScore() {
        return hostScore;
    }

    public void setHostScore(Integer hostScore) {
        this.hostScore = hostScore;
    }

    public Integer getGuestScore() {
        return guestScore;
    }

    public void setGuestScore(Integer guestScore) {
        this.guestScore = guestScore;
    }

    public GameUpdateResponse() {

    }
}
