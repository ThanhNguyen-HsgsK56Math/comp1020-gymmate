package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "mealplan")
@Data
public class MealPlan { // This class is used to store the meal plan for a user
    @Id
    private String id;
    private String userId;
    private LocalDate date;
    private List<MealDetails> mealSequence; // List of meal details including name and description
    private int totalCalories; // Total calories intake
    private int totalPrepTime; // Total preparation time in minutes
} 