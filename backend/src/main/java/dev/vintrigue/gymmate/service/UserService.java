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
        
        // Set default values for new registration
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(java.time.LocalDateTime.now());
        user.setProfileCompleted(false);
        
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user.get();
        }
        return null;
    }

    public User completeProfile(String userId, User profileData) {
        User user = findById(userId);
        
        // Validate activity level
        if (profileData.getActivityLevel() == null || profileData.getActivityLevel().trim().isEmpty()) {
            throw new IllegalArgumentException("Please put in your activityLevel with one of the following: sedentary, lightly_active, moderately_active, very_active, super_active");
        }
        
        if (!isValidActivityLevel(profileData.getActivityLevel())) {
            throw new IllegalArgumentException("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active");
        }

        // Validate gender
        if (profileData.getGender() == null || profileData.getGender().trim().isEmpty()) {
            throw new IllegalArgumentException("Please specify your gender (male/female/other)");
        }
        
        if (!isValidGender(profileData.getGender())) {
            throw new IllegalArgumentException("Invalid gender. Must be one of: male, female, other");
        }

        // Validate goals
        if (profileData.getGoal() == null || profileData.getGoal().isEmpty()) {
            throw new IllegalArgumentException("Please select at least one goal");
        }
        
        if (profileData.getGoal().size() > 2) {
            throw new IllegalArgumentException("Maximum of 2 goals allowed");
        }
        
        // Validate each goal is valid
        for (String goal : profileData.getGoal()) {
            if (!isValidGoal(goal)) {
                throw new IllegalArgumentException("Invalid goal. Must be one of: weight_loss, muscle_gain, endurance_building, healthy_lifestyle");
            }
        }

        // Update user profile
        user.setGoal(profileData.getGoal());
        user.setActivityLevel(profileData.getActivityLevel());
        user.setGender(profileData.getGender());
        user.setWeight(profileData.getWeight());
        user.setHeight(profileData.getHeight());
        user.setAge(profileData.getAge());
        user.setProfileCompleted(true);
        
        return userRepository.save(user);
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateActivityLevel(String userId, String activityLevel) {
        User user = findById(userId);
        if (!isValidActivityLevel(activityLevel)) {
            throw new IllegalArgumentException("Invalid activity level. Must be one of: sedentary, lightly_active, moderately_active, very_active, super_active");
        }
        user.setActivityLevel(activityLevel);
        return userRepository.save(user);
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
            gender.equals("male") ||
            gender.equals("female") ||
            gender.equals("other")
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

    public static class UserAlreadyExistsException extends Exception {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    // private boolean isValidActivityLevel(String activityLevel) {
    //     return activityLevel != null && (
    //         activityLevel.equals("sedentary") ||
    //         activityLevel.equals("lightly_active") ||
    //         activityLevel.equals("moderately_active") ||
    //         activityLevel.equals("very_active") ||
    //         activityLevel.equals("super_active")
    //     );
    // }

    // private boolean isValidGender(String gender) {
    //     return gender != null && (
    //         gender.equalsIgnoreCase("male") ||
    //         gender.equalsIgnoreCase("female") ||
    //         gender.equalsIgnoreCase("other")
    //     );
    // }

    // private boolean isValidGoal(String goal) {
    //     return goal != null && (
    //         goal.equals("weight_loss") ||
    //         goal.equals("muscle_gain") ||
    //         goal.equals("endurance_building") ||
    //         goal.equals("healthy_lifestyle")
    //     );
    // }
}