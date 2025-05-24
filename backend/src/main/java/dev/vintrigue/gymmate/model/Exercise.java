package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "exercise")
@Data
public class Exercise {
    @Id
    private String id;

    private String name;

    @Field("calories_burnt")
    private int caloriesBurned;

    @Field("suitability_weight_loss")
    private int suitabilityWeightLoss;

    @Field("suitability_muscle_gain")
    private int suitabilityMuscleGain;

    @Field("suitability_endurance_building")
    private int suitabilityEnduranceBuilding;

    @Field("suitability_healthy_lifestyle")
    private int suitabilityHealthyLifestyle;

    @Field("follow-up_exercises")
    private List<String> followUpExercises;

    // Default duration in minutes if not specified
    private int duration = 30;

    // Computed property for algorithm use
    public Map<String, Integer> getParameters() {
        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("suitability_weight_loss", suitabilityWeightLoss);
        parameters.put("suitability_muscle_gain", suitabilityMuscleGain);
        parameters.put("suitability_endurance_building", suitabilityEnduranceBuilding);
        parameters.put("suitability_healthy_lifestyle", suitabilityHealthyLifestyle);
        return parameters;
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

    public List<String> getFollowUpExercises() {
        return followUpExercises;
    }

    public void setFollowUpExercises(List<String> followUpExercises) {
        this.followUpExercises = followUpExercises;
    }
}