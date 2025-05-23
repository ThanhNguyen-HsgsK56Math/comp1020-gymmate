package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "exercise")
@Data
public class Exercise {
    @Id
    private String id;
    private String name;
    private String description;
    private int caloriesBurned; // Calories burned per session
    private int duration; // Duration in minutes
    private Map<String, Integer> parameters; // e.g., {"suitability_weight_loss": 5, "suitability_muscle_gain": 2, ...}
    private List<String> followUpExercises; // List of recommended follow-up exercises

    public Exercise() {
        this.parameters = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Map<String, Integer> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Integer> parameters) {
        this.parameters = parameters;
    }

    public List<String> getFollowUpExercises() {
        return followUpExercises;
    }

    public void setFollowUpExercises(List<String> followUpExercises) {
        this.followUpExercises = followUpExercises;
    }
}