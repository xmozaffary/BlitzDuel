package se.examenarbete.blitzduel.dto;

public class SubmitAnswerRequest {

    private String name;
    private Integer answerIndex;

    public SubmitAnswerRequest() {

    }

    public SubmitAnswerRequest(String name, Integer answerIndex) {
        this.name = name;
        this.answerIndex = answerIndex;
    }


    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getAnswerIndex(){
        return answerIndex;
    }

    public void setAnswerIndex(Integer answerIndex) {
        this.answerIndex = answerIndex;
    }
}
