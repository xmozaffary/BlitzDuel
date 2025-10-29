package se.examenarbete.blitzduel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World from BlitzDuel Backend! 🚀";
    }

    @GetMapping("/health")
    public String health() {
        return "Backend is running! ✅";
    }
    @GetMapping("/hell")
    public String hell() {
        return "You are in the hell! ✅";
    }
}
}