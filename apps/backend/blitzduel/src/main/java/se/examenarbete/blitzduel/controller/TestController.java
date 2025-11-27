package se.examenarbete.blitzduel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.examenarbete.blitzduel.model.Quiz;
import se.examenarbete.blitzduel.model.TestEntity;
import se.examenarbete.blitzduel.repository.QuizRepository;
import se.examenarbete.blitzduel.repository.TestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private QuizRepository quizRepository;

    @PostMapping
    public TestEntity createTest(@RequestBody String message) {
        return testRepository.save(new TestEntity(message));
    }

    @GetMapping
    public List<TestEntity> getAllTests() {
        return testRepository.findAll();
    }

    @GetMapping("/hello")
    public String getHello() {
        return "Hello world";
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getQuizCount() {
        long count = quizRepository.count();
        List<Quiz> allQuizzes = quizRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("quizzes", allQuizzes.stream()
                .map(q -> Map.of(
                        "id", q.getId(),
                        "title", q.getTitle(),
                        "category", q.getCategory()
                ))
                .collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }
}