package se.examenarbete.blitzduel.dto;

public class JoinLobbyRequest {
    private String nickname;
    private Long quizId;

    public JoinLobbyRequest(){}

    public JoinLobbyRequest(String nickname){
        this.nickname = nickname;
    }

    public String getNickname(){
        return nickname;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
    }
}
