package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.ExercisePlan;
import dev.vintrigue.gymmate.service.ExercisePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exerciseplans")
public class ExercisePlanController {

    @Autowired
    private ExercisePlanService exercisePlanService;

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
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
} 