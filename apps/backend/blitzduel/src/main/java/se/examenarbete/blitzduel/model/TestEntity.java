package se.examenarbete.blitzduel.model;


import jakarta.persistence.*;

@Entity
@Table(name = "test_connection")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    // Constructors, getters, setters
    public TestEntity() {}

    public TestEntity(String message) {
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}