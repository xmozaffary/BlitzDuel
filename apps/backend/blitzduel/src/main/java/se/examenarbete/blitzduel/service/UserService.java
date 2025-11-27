package se.examenarbete.blitzduel.service;

import org.springframework.stereotype.Service;
import se.examenarbete.blitzduel.model.User;
import se.examenarbete.blitzduel.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User createOrUpdateUser(String email, String name, String googleId, String profilePictureUrl) {
        Optional<User> existingUser = userRepository.findByGoogleId(googleId);

        if(existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setEmail(email);
            user.setProfilePictureUrl(profilePictureUrl);
            user.updateLastLogin();
            return userRepository.save(user);
        } else {
            User newUser = new User(email, name, googleId, profilePictureUrl);
            return userRepository.save(newUser);
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void updateGameStats(Long userId, boolean won, int score) {
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

        user.incrementGamesPlayed();
        if(won) {
            user.incrementGamesWon();
        }
        user.addScore(score);

        userRepository.save(user);
    }
}
