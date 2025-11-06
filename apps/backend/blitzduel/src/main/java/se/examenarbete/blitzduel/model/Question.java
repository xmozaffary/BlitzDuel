package se.examenarbete.blitzduel.model;

import java.util.List;

public class Question {

    private Long id;
    private Long quizId;
    private final String text;
    private final List<String> answers;
    private final int correctAnswerIndex;
    private final int timeLimit;


    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<String> getAnswers() {
        return answers;
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

    public Question(Long id , String text, List<String> questionAlternatives, int correctAnswerIndex, int timeLimit, Long quizId) {
        this.id = id;
        this.text = text;
        this.answers = questionAlternatives;
        this.correctAnswerIndex = correctAnswerIndex;
        this.timeLimit = timeLimit;
        this.quizId = quizId;
    }
}
