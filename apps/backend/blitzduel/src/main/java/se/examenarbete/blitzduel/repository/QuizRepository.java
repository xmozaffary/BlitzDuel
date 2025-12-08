package se.examenarbete.blitzduel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import se.examenarbete.blitzduel.model.Quiz;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    @Query("SELECT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestions (Long id);

    List<Quiz> findByCategory(String category);

    Optional<Quiz> findByTitle(String title);

    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions")
    List<Quiz> findAllWithQuestions();
}
