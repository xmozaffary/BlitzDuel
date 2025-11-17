package se.examenarbete.blitzduel.dto;

public class CreateLobbyRequest {

    private String name;
    private Long quizId;

    public CreateLobbyRequest() {
    }

    public CreateLobbyRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
