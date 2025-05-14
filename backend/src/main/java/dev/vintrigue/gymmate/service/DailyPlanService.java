package dev.vintrigue.gymmate.service;

import dev.vintrigue.gymmate.model.DailyPlan;
import dev.vintrigue.gymmate.model.Exercise;
import dev.vintrigue.gymmate.model.User;
import dev.vintrigue.gymmate.repository.DailyPlanRepository;
import dev.vintrigue.gymmate.repository.ExerciseRepository;
import dev.vintrigue.gymmate.repository.UserRepository;
import dev.vintrigue.gymmate.util.PlanGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyPlanService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private DailyPlanRepository dailyPlanRepository;

    public DailyPlan generatePlan(String userId) {
        // Fetch user and parse goal
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String goalStr = user.getGoal(); // "burn_500_calories"
        int goalCalories = Integer.parseInt(goalStr.split("_")[1]); // 500

        // Fetch all exercises
        List<Exercise> exercises = exerciseRepository.findAll();
        if (exercises.isEmpty()) {
            throw new RuntimeException("No exercises available");
        }

        // Generate plan using Dijkstra's algorithm
        PlanGenerator.PlanResult result = PlanGenerator.generatePlan(goalCalories, exercises);
        if (result == null) {
            throw new RuntimeException("Cannot generate plan to meet the goal");
        }

        // Create and save the daily plan
        DailyPlan plan = new DailyPlan();
        plan.setUserId(userId);
        plan.setDate(LocalDate.now());
        plan.setExerciseSequence(result.exerciseSequence);
        plan.setTotalCalories(result.totalCalories);
        plan.setTotalTime(result.totalTime);

        return dailyPlanRepository.save(plan);
    }
}