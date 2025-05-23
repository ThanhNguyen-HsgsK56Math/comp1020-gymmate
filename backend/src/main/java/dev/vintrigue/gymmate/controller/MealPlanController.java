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
     * Generates a meal plan for a user based on their profile and goals
     *
     * @param userId The user ID
     * @return The generated meal plan
     */
    @GetMapping
    public ResponseEntity<?> getMealPlan(@RequestParam String userId) {
        try {
            MealPlan plan = mealPlanService.generateMealPlan(userId);
            if (plan == null) {
                return ResponseEntity.badRequest().body("No suitable meal plan could be generated. Please ensure you have completed your profile and have valid goals set.");
            }
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
} 