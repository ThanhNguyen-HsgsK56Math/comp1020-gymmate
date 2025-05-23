package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.model.MealPlan;
import dev.vintrigue.gymmate.service.MealPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/mealplans")
public class MealPlanController {

    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Test endpoint to check available meals
     */
    @GetMapping("/test-meals")
    public ResponseEntity<?> testMeals() {
        List<Meal> meals = mongoTemplate.findAll(Meal.class);
        return ResponseEntity.ok(meals);
    }

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