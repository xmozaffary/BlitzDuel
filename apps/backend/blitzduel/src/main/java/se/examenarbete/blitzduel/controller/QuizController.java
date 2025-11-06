package se.examenarbete.blitzduel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import se.examenarbete.blitzduel.model.Quiz;

import se.examenarbete.blitzduel.service.QuizService;

import java.util.List;

@Controller
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;


    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> geAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Long id){
        return quizService.getQuizById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
