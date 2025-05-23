package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.model.MealPlan;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.MealPlanRepository;
import dev.vintrigue.gymmate.repository.MealRepository;
import dev.vintrigue.gymmate.repository.UserRepository;
import dev.vintrigue.gymmate.util.FitnessCalculator;
import dev.vintrigue.gymmate.util.MealGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MealPlanService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    /**
     * Generates a meal plan for a user based on their goals and preferences
     * 
     * @param userId The user ID
     * @return The generated meal plan
     * @throws RuntimeException if user not found or meal plan cannot be generated
     */
    public MealPlan generateMealPlan(String userId) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        // Calculate BMR and TDEE
        double bmr = FitnessCalculator.calculateBMR(
            user.getWeight(),
            user.getHeight(),
            user.getAge(),
            user.getGender()
        );
        double tdee = FitnessCalculator.calculateTDEE(bmr, user.getActivityLevel());

        // Set target calories based on user's goals
        int targetCalories = calculateTargetCalories(user.getGoal(), tdee);

        // Calculate preference weights based on user's goals
        Map<String, Integer> preferences = calculatePreferences(user.getGoal());

        // Fetch all meals
        List<Meal> meals = mealRepository.findAll();
        if (meals.isEmpty()) {
            throw new RuntimeException("No meals found in database. Please ensure meals are properly configured.");
        }

        // Calculate suitability scores and weights for each meal
        List<String> userGoals = user.getGoal();
        if (userGoals == null || userGoals.isEmpty()) {
            throw new RuntimeException("User has no defined goals");
        }
        
        for (Meal meal : meals) {
            if (meal.getParameters() == null) {
                meal.setParameters(new HashMap<>());
            }
            double suitability = FitnessCalculator.calculateSuitabilityScore(meal.getParameters(), userGoals);
            double weight = FitnessCalculator.calculateMealWeight(suitability, meal.getCalories(), bmr);
            meal.getParameters().put("calculated_weight", (int)(weight * 100)); // Store as integer percentage
        }

        // Generate meal plan using the algorithm
        MealGenerator.MealPlanResult result = MealGenerator.generateMealPlan(
                targetCalories, 
                meals, 
                preferences.get("weightLoss"),
                preferences.get("muscleBuilding"),
                preferences.get("health"));
        
        if (result == null) {
            throw new RuntimeException("Cannot generate meal plan to meet the target calories");
        }

        // Create and save the meal plan
        MealPlan plan = new MealPlan();
        plan.setUserId(userId);
        plan.setDate(LocalDate.now());
        plan.setMealSequence(result.mealSequence);
        plan.setTotalCalories(result.totalCalories);
        plan.setTotalPrepTime(result.totalPrepTime);

        return mealPlanRepository.save(plan);
    }

    private int calculateTargetCalories(List<String> goals, double tdee) {
        // Default to maintenance calories
        double calorieMultiplier = 1.0;
        
        if (goals.contains("weight_loss")) {
            calorieMultiplier = 0.8; // 20% deficit for weight loss
        } else if (goals.contains("muscle_gain")) {
            calorieMultiplier = 1.1; // 10% surplus for muscle gain
        }
        
        return (int)(tdee * calorieMultiplier);
    }

    private Map<String, Integer> calculatePreferences(List<String> goals) {
        Map<String, Integer> preferences = new HashMap<>();
        preferences.put("weightLoss", 5);
        preferences.put("muscleBuilding", 5);
        preferences.put("health", 5);

        // Adjust preferences based on goals
        if (goals.contains("weight_loss")) {
            preferences.put("weightLoss", 8);
            preferences.put("health", 7);
        }
        if (goals.contains("muscle_gain")) {
            preferences.put("muscleBuilding", 8);
            preferences.put("health", 7);
        }
        if (goals.contains("healthy_lifestyle")) {
            preferences.put("health", 9);
        }

        return preferences;
    }
}