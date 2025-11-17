package se.examenarbete.blitzduel.dto;

import java.util.List;

public class QuestionDTO {

    private String type;
    private Integer currentQuestionIndex;
    private String questionText;
    private List<String> options;

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

    public QuestionDTO() {

    }

    public QuestionDTO(String type, Integer currentQuestionIndex, String questionText, List<String> options) {
        this.type = type;
        this.currentQuestionIndex = currentQuestionIndex;
        this.questionText = questionText;
        this.options = options;
    }
}