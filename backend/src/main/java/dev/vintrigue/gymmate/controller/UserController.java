// This will interact with the user model and the user service

package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                return ResponseEntity.ok().body("Login successful");
            }
            return ResponseEntity.badRequest().body("Invalid credentials");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/complete-profile")
    public ResponseEntity<?> completeProfile(@PathVariable String userId, @RequestBody User profileData) {
        try {
            User updatedUser = userService.completeProfile(userId, profileData);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}/activity-level")
    public ResponseEntity<?> updateActivityLevel(
            @PathVariable String userId,
            @RequestParam String activityLevel) {
        try {
            User updatedUser = userService.updateActivityLevel(userId, activityLevel);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
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