package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.MealPlan;
import dev.vintrigue.gymmate.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {

    @Autowired
    private MealPlanService mealPlanService;

    /**
     * Generates a meal plan for a user with specified preferences
     *
     * @param userId The user ID
     * @param targetCalories Target calorie intake (optional, default is 2000)
     * @param weightLoss Preference for weight loss meals (1-10 scale)
     * @param muscleBuilding Preference for muscle building meals (1-10 scale)
     * @param generalHealth Preference for general health meals (1-10 scale)
     * @return The generated meal plan
     */
    @GetMapping
    public ResponseEntity<?> getMealPlan(
            @RequestParam String userId,
            @RequestParam(required = false, defaultValue = "0") int targetCalories,
            @RequestParam(required = false, defaultValue = "5") int weightLoss,
            @RequestParam(required = false, defaultValue = "5") int muscleBuilding,
            @RequestParam(required = false, defaultValue = "5") int generalHealth) {
        
        try {
            // Validate preference values (1-10 scale)
            weightLoss = Math.min(10, Math.max(1, weightLoss));
            muscleBuilding = Math.min(10, Math.max(1, muscleBuilding));
            generalHealth = Math.min(10, Math.max(1, generalHealth));
            
            MealPlan plan = mealPlanService.generateMealPlan(
                    userId, 
                    targetCalories,
                    weightLoss, 
                    muscleBuilding, 
                    generalHealth);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
} 