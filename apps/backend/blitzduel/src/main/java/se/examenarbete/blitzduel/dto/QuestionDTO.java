package se.examenarbete.blitzduel.dto;

import java.util.List;

public class QuestionDTO {

    private String type;
    private Integer questionIndex;
    private String questionText;
    private List<String> options;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(Integer questionIndex) {
        this.questionIndex = questionIndex;
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

    public QuestionDTO(String type, Integer questionIndex, String questionText, List<String> options) {
        this.type = type;
        this.questionIndex = questionIndex;
        this.questionText = questionText;
        this.options = options;
    }
}