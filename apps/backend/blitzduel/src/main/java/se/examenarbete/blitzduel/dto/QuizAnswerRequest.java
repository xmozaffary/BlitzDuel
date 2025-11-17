package se.examenarbete.blitzduel.dto;

import java.util.List;

public class QuizAnswerRequest {
    private String lobbyCode;
    protected String name;
    private List<Integer> answers;

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public QuizAnswerRequest(String lobbyCode, String name, List<Integer> answers) {
        this.lobbyCode = lobbyCode;
        this.name = name;
        this.answers = answers;
    }
}

