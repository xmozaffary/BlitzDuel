package se.examenarbete.blitzduel.model;

public class TimerUpdateMessage {
    private String type = "TIMER_UPDATE";
    private int remainingTime;
    private int currentQuestionIndex;

    public TimerUpdateMessage(){};


    public TimerUpdateMessage(int remainingTime, int currentQuestionIndex ){
        this.remainingTime = remainingTime;
        this.currentQuestionIndex = currentQuestionIndex;
    };

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public String getType() {
        return type;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setType(String type) {
        this.type = type;
    }
}
