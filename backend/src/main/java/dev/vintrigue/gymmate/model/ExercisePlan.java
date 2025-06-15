package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Document(collection = "dailyplan")
@Data
public class ExercisePlan {
    @Id
    private String id;
    private String userId;
    private LocalDate startDate; // Start date of the week
    private Map<LocalDate, List<Exercise>> dailyExercises; // Map of date to exercises for that day
    private int totalCaloriesBurned;
    private int totalDuration;
} 