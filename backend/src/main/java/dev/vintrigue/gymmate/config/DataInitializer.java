package dev.vintrigue.gymmate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vintrigue.gymmate.model.Exercise;
import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.repository.ExerciseRepository;
import dev.vintrigue.gymmate.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Load exercises
            if (exerciseRepository.count() == 0) {
                logger.info("Loading exercise data from JSON...");
                ClassPathResource exerciseResource = new ClassPathResource("exercise_plan.json");
                Map<String, Map<String, Object>> exerciseData = objectMapper.readValue(
                    exerciseResource.getInputStream(),
                    Map.class
                );

                for (Map.Entry<String, Map<String, Object>> entry : exerciseData.entrySet()) {
                    Exercise exercise = new Exercise();
                    exercise.setName(entry.getKey());
                    
                    Map<String, Object> data = entry.getValue();
                    exercise.setCaloriesBurned((Integer) data.get("calories_burnt"));
                    exercise.setSuitabilityWeightLoss((Integer) data.get("suitability_weight_loss"));
                    exercise.setSuitabilityMuscleGain((Integer) data.get("suitability_muscle_gain"));
                    exercise.setSuitabilityEnduranceBuilding((Integer) data.get("suitability_endurance_building"));
                    exercise.setSuitabilityHealthyLifestyle((Integer) data.get("suitability_healthy_lifestyle"));
                    exercise.setFollowUpExercises((List<String>) data.get("follow-up_exercises"));
                    
                    exerciseRepository.save(exercise);
                }
                logger.info("Successfully loaded {} exercises", exerciseRepository.count());
            }

            // Load meals
            if (mealRepository.count() == 0) {
                logger.info("Loading meal data from JSON...");
                ClassPathResource mealResource = new ClassPathResource("meal_plan.json");
                Map<String, Map<String, Object>> mealData = objectMapper.readValue(
                    mealResource.getInputStream(),
                    Map.class
                );

                for (Map.Entry<String, Map<String, Object>> entry : mealData.entrySet()) {
                    Meal meal = new Meal();
                    meal.setName(entry.getKey());
                    
                    Map<String, Object> data = entry.getValue();
                    meal.setType((String) data.get("type"));
                    meal.setCalories((Integer) data.get("calories"));
                    meal.setSuitabilityWeightLoss((Integer) data.get("suitability_weight_loss"));
                    meal.setSuitabilityMuscleGain((Integer) data.get("suitability_muscle_gain"));
                    meal.setSuitabilityEnduranceBuilding((Integer) data.get("suitability_endurance_building"));
                    meal.setSuitabilityHealthyLifestyle((Integer) data.get("suitability_healthy_lifestyle"));
                    meal.setFollowUpMeals((List<String>) data.get("follow-up_meals"));
                    
                    mealRepository.save(meal);
                }
                logger.info("Successfully loaded {} meals", mealRepository.count());
            }
        } catch (IOException e) {
            logger.error("Error loading data from JSON files", e);
            throw e;
        }
    }
} 