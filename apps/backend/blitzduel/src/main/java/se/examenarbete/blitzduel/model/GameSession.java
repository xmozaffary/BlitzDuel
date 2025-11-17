package se.examenarbete.blitzduel.model;

public class GameSession {

    private final String lobbyCode;
    private final Long quizId;
    private final String hostName;
    private final String guestName;

    private int currentQuestionIndex;
    private int hostNameScore;
    private int guestNameScore;

    private Integer hostNameAnswer;
    private Integer guestNameAnswer;

    public GameSession(String lobbyCode, Long quizId, String hostName, String guestName) {
        this.lobbyCode = lobbyCode;
        this.quizId = quizId;
        this.hostName = hostName;
        this.guestName = guestName;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public void setHostNameScore(int hostNameScore) {
        this.hostNameScore = hostNameScore;
    }

    public void setGuestNameScore(int guestNameScore) {
        this.guestNameScore = guestNameScore;
    }

    public void setHostNameAnswer(Integer hostNameAnswer) {
        this.hostNameAnswer = hostNameAnswer;
    }

    public void setGuestNameAnswer(Integer guestNameAnswer) {
        this.guestNameAnswer = guestNameAnswer;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public Long getQuizId() {
        return quizId;
    }

    public String getHostName() {
        return hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getHostNameScore() {
        return hostNameScore;
    }

    public int getGuestNameScore() {
        return guestNameScore;
    }

    public Integer getHostNameAnswer() {
        return hostNameAnswer;
    }

    public Integer getGuestNameAnswer() {
        return guestNameAnswer;
    }

    public boolean bothAnswered() {
        return hostNameAnswer != null && guestNameAnswer != null;
    }


    public void resetAnswers() {
        this.hostNameAnswer = null;
        this.guestNameAnswer = null;
    }

    public boolean isGameOver() {
        return currentQuestionIndex >= 10;
    }
}
