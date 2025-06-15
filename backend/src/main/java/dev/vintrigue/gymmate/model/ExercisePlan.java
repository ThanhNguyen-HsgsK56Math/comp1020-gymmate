package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "dailyplan")
@Data
public class ExercisePlan {
    @Id
    private String id;
    private String userId;
    private Map<String, List<Exercise>> dailyExercises; // Map of day of week to exercises for that day
    private int totalCaloriesBurned;
    private int totalDuration;

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public Map<String, List<Exercise>> getDailyExercises() { return dailyExercises; }
    public int getTotalCaloriesBurned() { return totalCaloriesBurned; }
    public int getTotalDuration() { return totalDuration; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setDailyExercises(Map<String, List<Exercise>> dailyExercises) { this.dailyExercises = dailyExercises; }
    public void setTotalCaloriesBurned(int totalCaloriesBurned) { this.totalCaloriesBurned = totalCaloriesBurned; }
    public void setTotalDuration(int totalDuration) { this.totalDuration = totalDuration; }
} 