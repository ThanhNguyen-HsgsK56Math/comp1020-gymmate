package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.model.MealPlan;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.MealPlanRepository;
import dev.vintrigue.gymmate.repository.MealRepository;
import dev.vintrigue.gymmate.repository.UserRepository;
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

        // Fetch all meals
        List<Meal> meals = mealRepository.findAll();
        if (meals.isEmpty()) {
            System.out.println("No meals found in database. Creating sample meals for this session.");
            meals = createSampleMeals();
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
    
    /**
     * Creates a list of sample meals for testing when no meals exist in the database
     */
    private List<Meal> createSampleMeals() {
        List<Meal> meals = new ArrayList<>();
        
        // Meal 1: Grilled Chicken Salad
        Meal chickenSalad = new Meal();
        chickenSalad.setName("Grilled Chicken Salad");
        chickenSalad.setDescription("Grilled chicken breast with mixed greens, cherry tomatoes, cucumber, and olive oil vinaigrette.");
        chickenSalad.setCalories(400);
        chickenSalad.setPrepTime(20);
        Map<String, Integer> chickenSaladParams = new HashMap<>();
        chickenSaladParams.put("weight_loss", 8);
        chickenSaladParams.put("muscle_gain", 7);
        chickenSaladParams.put("general_health", 8);
        chickenSalad.setParameters(chickenSaladParams);
        meals.add(chickenSalad);
        
        // Meal 2: Salmon Quinoa Bowl
        Meal salmonQuinoa = new Meal();
        salmonQuinoa.setName("Salmon Quinoa Bowl");
        salmonQuinoa.setDescription("Baked salmon with quinoa, steamed broccoli, and avocado slices, drizzled with lemon-tahini dressing.");
        salmonQuinoa.setCalories(550);
        salmonQuinoa.setPrepTime(30);
        Map<String, Integer> salmonQuinoaParams = new HashMap<>();
        salmonQuinoaParams.put("weight_loss", 6);
        salmonQuinoaParams.put("muscle_gain", 8);
        salmonQuinoaParams.put("general_health", 9);
        salmonQuinoa.setParameters(salmonQuinoaParams);
        meals.add(salmonQuinoa);
        
        // Meal 3: Greek Yogurt Parfait
        Meal yogurtParfait = new Meal();
        yogurtParfait.setName("Greek Yogurt Parfait");
        yogurtParfait.setDescription("Greek yogurt layered with fresh berries, granola, and a drizzle of honey for breakfast or a snack.");
        yogurtParfait.setCalories(300);
        yogurtParfait.setPrepTime(10);
        Map<String, Integer> yogurtParfaitParams = new HashMap<>();
        yogurtParfaitParams.put("weight_loss", 7);
        yogurtParfaitParams.put("muscle_gain", 6);
        yogurtParfaitParams.put("general_health", 8);
        yogurtParfait.setParameters(yogurtParfaitParams);
        meals.add(yogurtParfait);
        
        return meals;
    }
} 