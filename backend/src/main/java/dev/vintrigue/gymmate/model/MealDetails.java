package dev.vintrigue.gymmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds detailed information about a meal for the meal plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealDetails {
    private String id;
    private String name;
    private String description;
    private int calories;
    private int prepTime;
    
    /**
     * Creates MealDetails from a Meal object
     */
    public static MealDetails fromMeal(Meal meal) { // This method is used to convert a Meal object to a MealDetails object
        MealDetails details = new MealDetails();
        details.setId(meal.getId());
        details.setName(meal.getName());
        details.setDescription(meal.getDescription());
        details.setCalories(meal.getCalories());
        details.setPrepTime(meal.getPrepTime());
        return details;
    }
} 