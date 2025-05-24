// This will interact with the user model and the user service

package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok("User API is running");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("register");
        try {
            // Only require username and password for initial registration
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            
            User registeredUser = userService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (UserService.UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (user != null) {
                if (!user.isProfileCompleted()) {
                    return ResponseEntity.ok().body("Profile setup required");
                }
                return ResponseEntity.ok().body(user.getUsername());
            }
            return ResponseEntity.badRequest().body("Invalid credentials");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{username}/complete-profile")
    public ResponseEntity<?> completeProfile(@PathVariable String username, @RequestBody User profileData) {
        try {
            User updatedUser = userService.completeProfile(username, profileData);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{username}/activity-level")
    public ResponseEntity<?> updateActivityLevel(
            @PathVariable String username,
            @RequestParam String activityLevel) {
        try {
            User updatedUser = userService.updateActivityLevel(username, activityLevel);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{username}/test-profile")
    public ResponseEntity<?> testUserProfile(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            Map<String, Object> profileInfo = new HashMap<>();
            profileInfo.put("username", user.getUsername());
            profileInfo.put("goals", user.getGoal());
            profileInfo.put("activityLevel", user.getActivityLevel());
            profileInfo.put("weight", user.getWeight());
            profileInfo.put("height", user.getHeight());
            profileInfo.put("age", user.getAge());
            profileInfo.put("gender", user.getGender());
            profileInfo.put("profileCompleted", user.isProfileCompleted());
            
            return ResponseEntity.ok(profileInfo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Error response class
    private static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}