package se.examenarbete.blitzduel.dto;

import java.util.List;

public class QuizAnswerRequest {
    private String lobbyCode;
    protected String nickname;
    private List<Integer> answers;

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

    public QuizAnswerRequest(String lobbyCode, String nickname, List<Integer> answers) {
        this.lobbyCode = lobbyCode;
        this.nickname = nickname;
        this.answers = answers;
    }
}

