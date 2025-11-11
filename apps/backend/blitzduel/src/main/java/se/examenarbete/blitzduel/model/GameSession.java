package se.examenarbete.blitzduel.model;

public class GameSession {

    private final String lobbyCode;
    private final Long quizId;
    private final String player1Nickname;
    private final String player2Nickname;

    private int currentQuestionIndex;
    private int player1Score;
    private int player2Score;

    private Integer player1Answer;
    private Integer player2Answer;

    public GameSession(String lobbyCode, Long quizId, String player1Nickname, String player2Nickname) {
        this.lobbyCode = lobbyCode;
        this.quizId = quizId;
        this.player1Nickname = player1Nickname;
        this.player2Nickname = player2Nickname;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public void setPlayer1Answer(Integer player1Answer) {
        this.player1Answer = player1Answer;
    }

    public void setPlayer2Answer(Integer player2Answer) {
        this.player2Answer = player2Answer;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public Long getQuizId() {
        return quizId;
    }

    public String getPlayer1Nickname() {
        return player1Nickname;
    }

    public String getPlayer2Nickname() {
        return player2Nickname;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public Integer getPlayer1Answer() {
        return player1Answer;
    }

    public Integer getPlayer2Answer() {
        return player2Answer;
    }

    public boolean bothAnswered() {
        return player1Answer != null && player2Answer != null;
    }


    public void resetAnswers() {
        this.player1Answer = null;
        this.player2Answer = null;
    }

    public boolean isGameOver() {
        return currentQuestionIndex >= 10;
    }
}
