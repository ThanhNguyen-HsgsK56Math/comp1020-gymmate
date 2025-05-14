package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "dailyplan")
@Data
public class DailyPlan {
    @Id
    private String id;
    private String userId;
    private LocalDate date;
    private List<String> exerciseSequence; // List of exercise IDs
    private int totalCalories; // Total calories burned
    private int totalTime; // Total time in minutes
}