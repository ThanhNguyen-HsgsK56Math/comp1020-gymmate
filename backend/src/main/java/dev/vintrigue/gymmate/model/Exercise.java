package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "exercises")
@Data
public class Exercise {
    @Id
    private String id;
    private String name; 
    private String description;
    private int caloriesBurned;
    private int timeRequired; // in minutes
    private Map<String, Integer> parameters; // e.g., {"weight_loss": 8, "muscle_gain": 3, "endurance": 9}

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

    public int getTimeRequired() {
        return timeRequired;
    }

    public void setTimeRequired(int timeRequired) {
        this.timeRequired = timeRequired;
    }

    public Map<String, Integer> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Integer> parameters) {
        this.parameters = parameters;
    }
}