package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "weeklymealplan")
@Data
public class WeeklyMealPlan {
    @Id
    private String id;
    private String userId;
    private Map<String, List<MealDetails>> dailyMeals; // Map of day to list of meals
    private int totalWeeklyCalories;
    private int totalWeeklyPrepTime;
} 