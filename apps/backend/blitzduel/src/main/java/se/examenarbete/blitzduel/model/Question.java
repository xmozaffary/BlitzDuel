package se.examenarbete.blitzduel.model;

import java.util.List;

public class Question {

    private final Long id;
    private final Long quizId;
    private final String questionText;
    private final List<String> options;
    private final int correctAnswerIndex;
    private final int timeLimit;


    public Long getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public Long getQuizId() {
        return quizId;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public Question(Long id , String questionText, List<String> options, int correctAnswerIndex, int timeLimit, Long quizId) {
        this.id = id;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.timeLimit = timeLimit;
        this.quizId = quizId;
    }
}
