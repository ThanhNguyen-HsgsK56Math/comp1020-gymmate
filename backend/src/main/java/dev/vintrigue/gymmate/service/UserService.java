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
        
        // Validate activity level
        if (user.getActivityLevel() == null || user.getActivityLevel().trim().isEmpty()) {
            throw new IllegalArgumentException("Please put in your activityLevel with one of the following: sedentary, lightly_active, moderately_active, very_active, super_active");
        }
        
        if (!isValidActivityLevel(user.getActivityLevel())) {
            throw new IllegalArgumentException("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active");
        }

        // Validate gender
        if (user.getGender() == null || user.getGender().trim().isEmpty()) {
            throw new IllegalArgumentException("Please specify your gender (male/female/other)");
        }
        
        if (!isValidGender(user.getGender())) {
            throw new IllegalArgumentException("Invalid gender. Must be one of: male, female, other");
        }

        // Validate goals
        if (user.getGoal() == null || user.getGoal().isEmpty()) {
            throw new IllegalArgumentException("Please select at least one goal");
        }
        
        if (user.getGoal().size() > 2) {
            throw new IllegalArgumentException("Maximum of 2 goals allowed. Please select your top 2 priorities.");
        }
        
        // Validate each goal is valid
        for (String goal : user.getGoal()) {
            if (!isValidGoal(goal)) {
                throw new IllegalArgumentException("Invalid goal. Must be one of: weight_loss, muscle_gain, endurance_building, healthy_lifestyle");
            }
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean login(String username, String password) {
        System.out.println("Log in"+username);
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
    
    public User updateActivityLevel(String userId, String activityLevel) {
        if (!isValidActivityLevel(activityLevel)) {
            throw new IllegalArgumentException("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active");
        }
        
        User user = findById(userId);
        user.setActivityLevel(activityLevel);
        return userRepository.save(user);
    }
    
    // Custom exception for user existence violations
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    private boolean isValidActivityLevel(String activityLevel) {
        return activityLevel != null && (
            activityLevel.equals("sedentary") ||
            activityLevel.equals("lightly_active") ||
            activityLevel.equals("moderately_active") ||
            activityLevel.equals("very_active") ||
            activityLevel.equals("super_active")
        );
    }

    private boolean isValidGender(String gender) {
        return gender != null && (
            gender.equalsIgnoreCase("male") ||
            gender.equalsIgnoreCase("female") ||
            gender.equalsIgnoreCase("other")
        );
    }

    private boolean isValidGoal(String goal) {
        return goal != null && (
            goal.equals("weight_loss") ||
            goal.equals("muscle_gain") ||
            goal.equals("endurance_building") ||
            goal.equals("healthy_lifestyle")
        );
    }
}