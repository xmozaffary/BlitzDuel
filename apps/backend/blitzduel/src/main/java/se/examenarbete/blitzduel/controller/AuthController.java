package se.examenarbete.blitzduel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.examenarbete.blitzduel.model.User;
import se.examenarbete.blitzduel.security.JwtUtil;
import se.examenarbete.blitzduel.service.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long userId = jwtUtil.extractUserId(token);

            User user = userService.getUserById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("name", user.getName());
            response.put("profilePictureUrl", user.getProfilePictureUrl());
            response.put("gamesPlayed", user.getGamesPlayed());
            response.put("gamesWon", user.getGamesWon());
            response.put("totalScore", user.getTotalScore());

            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.status(401).build();
        }
    }
}
