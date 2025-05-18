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
     * Generates an exercise plan for a user with specified preferences
     *
     * @param userId The user ID
     * @param targetCaloriesBurned Target calories to burn (optional, default is 20% of TDEE)
     * @param weightLoss Preference for weight loss exercises (1-10 scale)
     * @param muscleBuilding Preference for muscle building exercises (1-10 scale)
     * @param endurance Preference for endurance building exercises (1-10 scale)
     * @param health Preference for general health exercises (1-10 scale)
     * @return The generated exercise plan
     */
    @GetMapping
    public ResponseEntity<?> getExercisePlan(
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "0") int targetCaloriesBurned,
            @RequestParam(required = false, defaultValue = "5") int weightLoss,
            @RequestParam(required = false, defaultValue = "5") int muscleBuilding,
            @RequestParam(required = false, defaultValue = "5") int endurance,
            @RequestParam(required = false, defaultValue = "5") int health) {
        
        try {
            // Validate preference values (1-10 scale)
            weightLoss = Math.min(10, Math.max(1, weightLoss));
            muscleBuilding = Math.min(10, Math.max(1, muscleBuilding));
            endurance = Math.min(10, Math.max(1, endurance));
            health = Math.min(10, Math.max(1, health));
            
            ExercisePlan plan = exercisePlanService.generateExercisePlan(
                    userId, 
                    targetCaloriesBurned,
                    weightLoss, 
                    muscleBuilding, 
                    endurance,
                    health);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
} 