package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) throws UserAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        
        if (existingUser.isPresent()) {
            // Check if the password matches
            if (passwordEncoder.matches(user.getPassword(), existingUser.get().getPassword())) {
                throw new UserAlreadyExistsException("User with this username and password already exists");
            }
            // If username exists but password is different, allow registration
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean login(String username, String password) {
        return authenticateUser(username, password);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return passwordEncoder.matches(password, user.get().getPassword());
        }
        return false;
    }
    
    // Custom exception for user existence violations
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }
}