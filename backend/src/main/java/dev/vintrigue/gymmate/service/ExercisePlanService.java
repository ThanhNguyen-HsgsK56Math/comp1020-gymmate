package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.Exercise;
import dev.vintrigue.gymmate.model.ExercisePlan;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.ExercisePlanRepository;
import dev.vintrigue.gymmate.repository.ExerciseRepository;
import dev.vintrigue.gymmate.repository.UserRepository;
import dev.vintrigue.gymmate.util.ExercisePlanGenerator;
import dev.vintrigue.gymmate.util.FitnessCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExercisePlanService {
    private static final Logger logger = LoggerFactory.getLogger(ExercisePlanService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExercisePlanRepository exercisePlanRepository;

    /**
     * Generates an exercise plan for a user based on their goals and preferences
     * 
     * @param userId The user ID
     * @return The generated exercise plan
     * @throws RuntimeException if user not found or exercise plan cannot be generated
     */
    public ExercisePlan generateExercisePlan(String userId) {
        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        logger.info("Generating exercise plan for user: {}", userId);
        logger.info("User goals: {}", user.getGoal());
        logger.info("User activity level: {}", user.getActivityLevel());

        // Calculate BMR and TDEE
        double bmr = FitnessCalculator.calculateBMR(
            user.getWeight(),
            user.getHeight(),
            user.getAge(),
            user.getGender()
        );
        double tdee = FitnessCalculator.calculateTDEE(bmr, user.getActivityLevel());

        logger.info("Calculated BMR: {}, TDEE: {}", bmr, tdee);

        // Calculate target calories to burn based on user's goals
        int targetCaloriesBurned = calculateTargetCaloriesBurned(user.getGoal(), tdee);
        logger.info("Target calories to burn: {}", targetCaloriesBurned);

        // Calculate preference weights based on user's goals
        Map<String, Integer> preferences = calculatePreferences(user.getGoal());
        logger.info("Preference weights: {}", preferences);

        // Fetch all exercises
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            logger.error("No exercises found in database");
            throw new RuntimeException("No exercises found in database. Please ensure MongoDB is properly configured and exercise data is loaded.");
        }
        logger.info("Found {} exercises in database", exercises.size());

        // Generate exercise plan using the algorithm
        ExercisePlanGenerator.ExercisePlanResult result = ExercisePlanGenerator.generateExercisePlan(
                targetCaloriesBurned,
                exercises,
                preferences.get("weightLoss"),
                preferences.get("muscleBuilding"),
                preferences.get("endurance"),
                preferences.get("health"));

        if (result == null) {
            logger.error("Failed to generate exercise plan. Target calories: {}, Available exercises: {}", 
                targetCaloriesBurned, exercises.size());
            throw new RuntimeException("Cannot generate exercise plan to meet the target calories");
        }

        logger.info("Successfully generated exercise plan with {} exercises, total calories: {}", 
            result.exerciseSequence.size(), result.totalCaloriesBurned);

        // Create and save the exercise plan
        ExercisePlan plan = new ExercisePlan();
        plan.setUserId(userId);
        plan.setDate(LocalDate.now());
        plan.setExercises(result.exerciseSequence);
        plan.setTotalCaloriesBurned(result.totalCaloriesBurned);

        return exercisePlanRepository.save(plan);
    }

    private int calculateTargetCaloriesBurned(List<String> goals, double tdee) {
        // Default to 20% of TDEE
        double calorieMultiplier = 0.2;
        
        if (goals.contains("weight_loss")) {
            calorieMultiplier = 0.3; // 30% of TDEE for weight loss
        } else if (goals.contains("muscle_gain")) {
            calorieMultiplier = 0.25; // 25% of TDEE for muscle gain
        } else if (goals.contains("endurance_building")) {
            calorieMultiplier = 0.35; // 35% of TDEE for endurance
        }
        
        int targetCalories = (int)(tdee * calorieMultiplier);
        logger.info("Calculated target calories: {} (TDEE: {}, Multiplier: {})", 
            targetCalories, tdee, calorieMultiplier);
        return targetCalories;
    }

    private Map<String, Integer> calculatePreferences(List<String> goals) {
        Map<String, Integer> preferences = new HashMap<>();
        // Initialize all preferences to 1 (minimum)
        preferences.put("weightLoss", 1);
        preferences.put("muscleBuilding", 1);
        preferences.put("endurance", 1);
        preferences.put("health", 1);

        // Adjust preferences based on goals
        if (goals.contains("weight_loss")) {
            preferences.put("weightLoss", 5); // α = 1.0
            preferences.put("endurance", 3);  // θ = 0.6
        }
        if (goals.contains("muscle_gain")) {
            preferences.put("muscleBuilding", 5); // β = 1.0
            preferences.put("endurance", 3);      // θ = 0.6
        }
        if (goals.contains("endurance_building")) {
            preferences.put("endurance", 5);  // θ = 1.0
            preferences.put("health", 3);     // γ = 0.6
        }
        if (goals.contains("healthy_lifestyle")) {
            preferences.put("health", 5);     // γ = 1.0
            preferences.put("endurance", 3);  // θ = 0.6
        }

        return preferences;
    }
} 