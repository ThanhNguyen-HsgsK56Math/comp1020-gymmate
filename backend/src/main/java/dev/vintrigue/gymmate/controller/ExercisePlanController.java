package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.Exercise;
import dev.vintrigue.gymmate.model.ExercisePlan;
import dev.vintrigue.gymmate.service.ExercisePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/exerciseplans")
public class ExercisePlanController {

    @Autowired
    private ExercisePlanService exercisePlanService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Test endpoint to check available exercises
     */
    @GetMapping("/test-exercises")
    public ResponseEntity<?> testExercises() {
        List<Exercise> exercises = mongoTemplate.findAll(Exercise.class);
        return ResponseEntity.ok(exercises);
    }

    /**
     * Generates an exercise plan for a user based on their profile and goals
     *
     * @param userId The user ID
     * @return The generated exercise plan
     */
    @GetMapping
    public ResponseEntity<?> getExercisePlan(@RequestParam String userId) {
        try {
            ExercisePlan plan = exercisePlanService.generateExercisePlan(userId);
            if (plan == null) {
                return ResponseEntity.badRequest().body("No suitable exercise plan could be generated. Please ensure you have completed your profile and have valid goals set.");
            }
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
} 