package se.examenarbete.blitzduel.dto;

public class SubmitAnswerRequest {

    private String playerNickname;
    private Integer answerIndex;

    public SubmitAnswerRequest() {

    }

    public SubmitAnswerRequest(String playerNickname, Integer answerIndex) {
        this.playerNickname = playerNickname;
        this.answerIndex = answerIndex;
    }


    public String getPlayerNickname(){
        return playerNickname;
    }

    public void setPlayerNickname(String playerNickname){
        this.playerNickname = playerNickname;
    }

    public int getAnswerIndex(){
        return answerIndex;
    }

    public void setAnswerIndex(Integer answerIndex) {
        this.answerIndex = answerIndex;
    }
}
