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
     * @param targetCalories The target calorie intake (if 0, will use default of 2000)
     * @param weightLossPreference Importance of weight loss (1-10)
     * @param muscleBuildingPreference Importance of muscle building (1-10)
     * @param generalHealthPreference Importance of general health (1-10)
     * @return The generated meal plan
     */
    public MealPlan generateMealPlan(
            String userId, 
            int targetCalories,
            int weightLossPreference, 
            int muscleBuildingPreference, 
            int generalHealthPreference) {
        
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
                
        // Use default calorie target if not specified
        if (targetCalories <= 0) {
            targetCalories = 2000; // Default daily calorie intake
        }

        // Calculate BMR and TDEE
        double bmr = FitnessCalculator.calculateBMR(
            user.getWeight(),
            user.getHeight(),
            user.getAge(),
            user.getGender()
        );
        double tdee = FitnessCalculator.calculateTDEE(bmr, user.getActivityLevel());

        // Fetch all meals
        List<Meal> meals = mealRepository.findAll();
        if (meals.isEmpty()) {
            System.out.println("No meals found in database. Creating sample meals for this session.");
            //meals = createSampleMeals();
        }

        // Calculate suitability scores and weights for each meal
        List<String> userGoals = user.getGoal();
        for (Meal meal : meals) {
            double suitability = FitnessCalculator.calculateSuitabilityScore(meal.getParameters(), userGoals);
            double weight = FitnessCalculator.calculateMealWeight(suitability, meal.getCalories(), bmr);
            meal.getParameters().put("calculated_weight", (int)(weight * 100)); // Store as integer percentage
        }

        // Generate meal plan using the algorithm
        MealGenerator.MealPlanResult result = MealGenerator.generateMealPlan(
                targetCalories, 
                meals, 
                weightLossPreference, 
                muscleBuildingPreference,
                generalHealthPreference);
        
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
} 