package se.examenarbete.blitzduel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.examenarbete.blitzduel.model.Question;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId")
    List<Question> findByQuizId(Long quizId);

}
