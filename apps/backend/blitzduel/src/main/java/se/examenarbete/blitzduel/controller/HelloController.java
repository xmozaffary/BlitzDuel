package se.examenarbete.blitzduel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.examenarbete.blitzduel.util.RandomCodeGenerator;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final RandomCodeGenerator codeGenerator;

    public HelloController(RandomCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    @GetMapping("/code")
    public String code() {
        return codeGenerator.generate();
    }
}