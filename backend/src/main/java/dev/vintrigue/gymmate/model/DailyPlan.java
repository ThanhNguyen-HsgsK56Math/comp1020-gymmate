package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "dailyplan")
@Data // This annotation is used to generate getters and setters for the class
public class DailyPlan {
    @Id
    private String id;
    private String userId;
    private LocalDate date;
    private List<ExerciseDetails> exerciseSequence; // List of exercise details including name and description
    private int totalCalories; // Total calories burned
    private int totalTime; // Total time in minutes
}