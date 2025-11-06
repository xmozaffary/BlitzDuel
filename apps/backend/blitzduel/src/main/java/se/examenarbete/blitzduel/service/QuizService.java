package se.examenarbete.blitzduel.service;


import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.model.Question;
import se.examenarbete.blitzduel.model.Quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final List<Quiz> quizzes;
    private final List<Question> questions;

    public QuizService(){
        this.quizzes = createHardcodedQuizzes();
        this.questions = createHardcodedQuestions();
    }

    private List<Quiz> createHardcodedQuizzes(){
        List<Quiz> list = new ArrayList<>();
        list.add(new Quiz(
                1L,
                "Allmän kunskap",
                "Testa din allmänna kunskap",
                "Allmänt",
                10
                ));
        return list;
    }




    private List<Question> createHardcodedQuestions(){
        List<Question> list = new ArrayList<>();
        list.add(new Question(
                1L,
                "Vad är huvudstaden i Sverige?",
                Arrays.asList("Stockholm", "Göteborg", "Malmö", "Uppsala"),
                0,
                15,
                1L
        ));

        list.add(new Question(
                2L,
                "Vilket år landade människan på månen?",
                Arrays.asList("1965", "1969", "1972", "1975"),
                1,
                15,
                1L
        ));

        list.add(new Question(
                3L,
                "Vad heter den längsta floden i världen?",
                Arrays.asList("Nilen", "Amazonas", "Mississippi", "Yangtze"),
                0,
                15,
                1L
        ));

        list.add(new Question(
                4L,
                "Vilket programmeringsspråk använder Spring Boot?",
                Arrays.asList("Python", "JavaScript", "Java", "C++"),
                2,
                15,
                1L
        ));

        list.add(new Question(
                5L,
                "Hur många sekunder är det på en minut?",
                Arrays.asList("30", "45", "60", "90"),
                2,
                15,
                1L
        ));

        list.add(new Question(
                6L,
                "Vad står HTTP för?",
                Arrays.asList("HyperText Transfer Protocol", "High Transfer Text Protocol",
                        "HyperText Technical Protocol", "Home Text Transfer Protocol"),
                0,
                15,
                1L
        ));

        list.add(new Question(
                7L,
                "Vilket djur är känt som 'djungelns kung'?",
                Arrays.asList("Tiger", "Elefant", "Lejon", "Gorilla"),
                2,
                15,
                1L
        ));

        list.add(new Question(
                8L,
                "Hur många planeter finns det i solsystemet?",
                Arrays.asList("7", "8", "9", "10"),
                1,
                15,
                1L
        ));

        list.add(new Question(
                9L,
                "Vad är 5 × 8?",
                Arrays.asList("35", "40", "45", "50"),
                1,
                15,
                1L
        ));

        list.add(new Question(
                10L,
                "Vilket land är känt för pizza?",
                Arrays.asList("Frankrike", "Spanien", "Italien", "Grekland"),
                2,
                15,
                1L
        ));
        return list;
    }

    public List<Quiz> getAllQuizzes(){
        return quizzes;
    }


    public Optional<Quiz> getQuizById(Long id) {
        return quizzes.stream()
                .filter(q -> q.getId().equals(id))
                .findFirst();
    }




     public List<Question> getQuestionsByQuizId(Long quizId){
        return questions.stream()
                .filter(q -> q.getQuizId().equals(quizId))
                .toList();
     }
}
