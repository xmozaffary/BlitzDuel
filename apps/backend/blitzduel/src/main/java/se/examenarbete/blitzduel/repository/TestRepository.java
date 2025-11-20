package se.examenarbete.blitzduel.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.examenarbete.blitzduel.model.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
}