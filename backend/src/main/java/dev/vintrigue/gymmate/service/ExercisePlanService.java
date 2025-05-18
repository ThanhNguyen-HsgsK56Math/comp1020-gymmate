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

import java.time.LocalDate;
import java.util.List;

@Service
public class ExercisePlanService {

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
     * @param targetCaloriesBurned The target calories to burn (if 0, will use default based on TDEE)
     * @param weightLossPreference Importance of weight loss (1-10)
     * @param muscleBuildingPreference Importance of muscle building (1-10)
     * @param endurancePreference Importance of endurance building (1-10)
     * @param healthPreference Importance of general health (1-10)
     * @return The generated exercise plan
     */
    public ExercisePlan generateExercisePlan(
            String userId,
            int targetCaloriesBurned,
            int weightLossPreference,
            int muscleBuildingPreference,
            int endurancePreference,
            int healthPreference) {
        
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

        // Use default calorie target if not specified (20% of TDEE)
        if (targetCaloriesBurned <= 0) {
            targetCaloriesBurned = (int)(tdee * 0.2);
        }

        // Fetch all exercises
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            throw new RuntimeException("No exercises found in database. Please ensure MongoDB is properly configured and exercise data is loaded.");
        }

        // Generate exercise plan using the algorithm
        ExercisePlanGenerator.ExercisePlanResult result = ExercisePlanGenerator.generateExercisePlan(
                targetCaloriesBurned,
                exercises,
                weightLossPreference,
                muscleBuildingPreference,
                endurancePreference,
                healthPreference);

        if (result == null) {
            throw new RuntimeException("Cannot generate exercise plan to meet the target calories");
        }

        // Create and save the exercise plan
        ExercisePlan plan = new ExercisePlan();
        plan.setUserId(userId);
        plan.setDate(LocalDate.now());
        plan.setExercises(result.exerciseSequence);
        plan.setTotalCaloriesBurned(result.totalCaloriesBurned);

        return exercisePlanRepository.save(plan);
    }
} 