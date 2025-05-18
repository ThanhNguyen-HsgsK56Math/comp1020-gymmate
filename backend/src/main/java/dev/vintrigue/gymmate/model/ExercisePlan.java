package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "exercise_plans")
@Data
public class ExercisePlan {
    @Id
    private String id;
    private String userId;
    private LocalDate date;
    private List<Exercise> exercises;
    private int totalCaloriesBurned;
} 