package se.examenarbete.blitzduel.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import se.examenarbete.blitzduel.model.TestEntity;
import se.examenarbete.blitzduel.repository.TestRepository;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:3000")
public class TestController {

    @Autowired
    private TestRepository testRepository;

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

    @GetMapping("/ch")
    public String getChange() {
        System.out.println("hello");
        return "Hello world from somewhere i helvate";

    }
}