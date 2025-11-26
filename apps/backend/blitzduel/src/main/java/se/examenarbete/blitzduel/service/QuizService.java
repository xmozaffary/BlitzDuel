package se.examenarbete.blitzduel.service;


import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.model.Quiz;
import se.examenarbete.blitzduel.repository.QuestionRepository;
import se.examenarbete.blitzduel.repository.QuizRepository;

import java.util.List;
import java.util.Optional;


@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findByIdWithQuestions(id);
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }
}

