package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.model.MealDetails;
import dev.vintrigue.gymmate.model.WeeklyMealPlan;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.MealPlanRepository;
import dev.vintrigue.gymmate.repository.MealRepository;
import dev.vintrigue.gymmate.repository.UserRepository;
import dev.vintrigue.gymmate.util.FitnessCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealPlanService {
    private static final Logger logger = LoggerFactory.getLogger(MealPlanService.class);
    private static final String[] DAYS_OF_WEEK = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final String[] MEAL_TYPES = {"breakfast", "lunch", "dinner"};

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealPlanRepository mealPlanRepository;

    /**
     * Generates a weekly meal plan for a user based on their goals and preferences
     * 
     * @param username The user's username
     * @return The generated weekly meal plan
     * @throws RuntimeException if user not found or meal plan cannot be generated
     */
    public WeeklyMealPlan generateMealPlan(String username) {
        logger.info("Starting meal plan generation for user: {}", username);
        
        // Fetch user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Calculate daily calorie needs
        double bmr = FitnessCalculator.calculateBMR(
            user.getWeight(),
            user.getHeight(),
            user.getAge(),
            user.getGender()
        );
        double tdee = FitnessCalculator.calculateTDEE(bmr, user.getActivityLevel());
        
        // Adjust calories based on goals
        int dailyCalories = calculateDailyCalories(tdee, user.getGoal());
        logger.info("Daily calorie target: {}", dailyCalories);

        // Fetch all meals
        List<Meal> allMeals = mealRepository.findAll();
        logger.info("Found {} total meals in database", allMeals.size());

        if (allMeals.isEmpty()) {
            throw new RuntimeException("No meals found in database");
        }

        // Create weekly plan
        WeeklyMealPlan weeklyPlan = new WeeklyMealPlan();
        weeklyPlan.setUserId(user.getId());
        Map<String, List<MealDetails>> dailyMeals = new HashMap<>();
        int totalWeeklyCalories = 0;
        int totalWeeklyPrepTime = 0;

        // Generate meals for each day
        for (String day : DAYS_OF_WEEK) {
            List<MealDetails> dayMeals = new ArrayList<>();
            int remainingCalories = dailyCalories;

            // Generate meals for each meal type
            for (String mealType : MEAL_TYPES) {
                // Calculate target calories for this meal
                int mealCalories = calculateMealCalories(mealType, remainingCalories);
                
                // Get suitable meals for this type
                List<Meal> suitableMeals = filterMealsByType(allMeals, mealType);
                
                // Select the best meal
                Meal selectedMeal = selectMeal(suitableMeals, mealCalories, user.getGoal());
                
                if (selectedMeal != null) {
                    MealDetails mealDetails = MealDetails.fromMeal(selectedMeal);
                    dayMeals.add(mealDetails);
                    remainingCalories -= mealDetails.getCalories();
                    totalWeeklyCalories += mealDetails.getCalories();
                    totalWeeklyPrepTime += mealDetails.getPrepTime();
                    
                    logger.info("Selected {} for {}: {} calories", mealType, day, mealDetails.getCalories());
                }
            }
            
            dailyMeals.put(day, dayMeals);
        }

        weeklyPlan.setDailyMeals(dailyMeals);
        weeklyPlan.setTotalWeeklyCalories(totalWeeklyCalories);
        weeklyPlan.setTotalWeeklyPrepTime(totalWeeklyPrepTime);

        logger.info("Meal plan generation completed. Total weekly calories: {}", totalWeeklyCalories);
        return weeklyPlan;
    }

    private int calculateDailyCalories(double tdee, List<String> goals) {
        double multiplier = 1.0;
        
        if (goals.contains("weight_loss")) {
            multiplier = 0.85; // 15% deficit
        } else if (goals.contains("muscle_gain")) {
            multiplier = 1.15; // 15% surplus
        } else if (goals.contains("endurance_building")) {
            multiplier = 1.10; // 10% surplus
        }
        
        return (int) (tdee * multiplier);
    }

    private int calculateMealCalories(String mealType, int remainingCalories) {
        switch (mealType) {
            case "breakfast":
                return (int) (remainingCalories * 0.3); // 30% of remaining calories
            case "lunch":
                return (int) (remainingCalories * 0.4); // 40% of remaining calories
            case "dinner":
                return (int) (remainingCalories * 0.3); // 30% of remaining calories
            default:
                return remainingCalories / 3;
        }
    }

    private List<Meal> filterMealsByType(List<Meal> meals, String type) {
        return meals.stream()
            .filter(meal -> meal.getType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }

    private Meal selectMeal(List<Meal> meals, int targetCalories, List<String> goals) {
        if (meals.isEmpty()) {
            return null;
        }

        // First try to find meals within 150 calories of target
        return meals.stream()
            .filter(meal -> Math.abs(meal.getCalories() - targetCalories) <= 150)
            .max(Comparator.comparingDouble(meal -> calculateMealScore(meal, goals)))
            .orElseGet(() -> meals.stream()
                .min(Comparator.comparingInt(meal -> Math.abs(meal.getCalories() - targetCalories)))
                .orElse(null));
    }

    private double calculateMealScore(Meal meal, List<String> goals) {
        double score = 0;
        for (String goal : goals) {
            switch (goal) {
                case "weight_loss":
                    score += meal.getSuitabilityWeightLoss();
                    break;
                case "muscle_gain":
                    score += meal.getSuitabilityMuscleGain();
                    break;
                case "endurance_building":
                    score += meal.getSuitabilityEnduranceBuilding();
                    break;
                case "healthy_lifestyle":
                    score += meal.getSuitabilityHealthyLifestyle();
                    break;
            }
        }
        return score;
    }
}
