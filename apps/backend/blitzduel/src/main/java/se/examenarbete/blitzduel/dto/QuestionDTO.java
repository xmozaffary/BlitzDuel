package se.examenarbete.blitzduel.dto;

import java.util.List;

public class QuestionDTO {

    private String type;
    private Integer currentQuestionIndex;
    private String questionText;
    private List<String> options;
    private String hostName;
    private String guestName;
    private int timeLimit = 5;
    private long startTime;

    public QuestionDTO() {

    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public QuestionDTO(String type, Integer currentQuestionIndex, String questionText, List<String> options, String hostName, String guestName) {
        this.type = type;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questionText = questionText;
        this.options = options;
        this.hostName = hostName;
        this.guestName = guestName;
        //this.timeLimit = timeLimit;
        //this.startTime = startTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(Integer currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getHostName() { return hostName; }
    public String getGuestName() { return guestName; }

    public void setHostName(String hostName) { this.hostName = hostName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

}