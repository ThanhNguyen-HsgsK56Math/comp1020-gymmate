package dev.vintrigue.gymmate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds details about an exercise for the daily plan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDetails {
    private String id;
    private String name;
    private String description;
    private int caloriesBurned;
    private int timeRequired;
    
    /**
     * Creates ExerciseDetails from an Exercise object
     */
    public static ExerciseDetails fromExercise(Exercise exercise) {
        ExerciseDetails details = new ExerciseDetails();
        details.setId(exercise.getId());
        details.setName(exercise.getName());
        details.setDescription(exercise.getDescription());
        details.setCaloriesBurned(exercise.getCaloriesBurned());
        details.setTimeRequired(exercise.getTimeRequired());
        return details;
    }
} 