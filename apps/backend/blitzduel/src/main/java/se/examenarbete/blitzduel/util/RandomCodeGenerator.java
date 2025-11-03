package se.examenarbete.blitzduel.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCodeGenerator {

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGHT = 6;
    private static final Random RANDOM = new Random();

    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGHT);
        for (int i = 0; i < CODE_LENGHT; i++){
            int randomIndex = RANDOM.nextInt(CHARS.length());
            code.append(CHARS.charAt(randomIndex));
        }
        return code.toString();
    }
}
