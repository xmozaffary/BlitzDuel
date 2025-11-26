package se.examenarbete.blitzduel.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonBackReference
    private Quiz quiz;


    @Column(nullable = false, length = 500)
    private  String questionText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text")
    @OrderColumn(name  = "option_order")
    private List<String> options = new ArrayList<>();

    @Column(nullable = false)
    private int correctAnswerIndex;

    @Column(nullable = false)
    private int timeLimit = 5;

    public Question(){}

    public Question(String questionText, List<String> options, int correctAnswerIndex, int timeLimit) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.timeLimit = timeLimit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public Long getQuizId() {
        return quiz != null ? quiz.getId() : null;
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

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

}
