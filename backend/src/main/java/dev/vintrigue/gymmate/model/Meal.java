package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Document(collection = "meal")
@Data
public class Meal {
    @Id
    private String id;

    private String name;
    private String description;

    private String type;

    @Field("suitability_weight_loss")
    private int suitabilityWeightLoss;

    @Field("suitability_muscle_gain")
    private int suitabilityMuscleGain;

    @Field("suitability_endurance_building")
    private int suitabilityEnduranceBuilding;

    @Field("suitability_healthy_lifestyle")
    private int suitabilityHealthyLifestyle;

    private int calories;

    @Field("follow-up_meals")
    private List<String> followUpMeals;

    // Default prep time in minutes if not specified
    private int prepTime = 15;

    private Map<String, Integer> parameters;

    // Computed property for algorithm use
    public Map<String, Integer> getParameters() {
        if (parameters == null) {
            parameters = new HashMap<>();
            parameters.put("suitability_weight_loss", suitabilityWeightLoss);
            parameters.put("suitability_muscle_gain", suitabilityMuscleGain);
            parameters.put("suitability_endurance_building", suitabilityEnduranceBuilding);
            parameters.put("suitability_healthy_lifestyle", suitabilityHealthyLifestyle);
        }
        return parameters;
    }

    public void setParameters(Map<String, Integer> parameters) {
        this.parameters = parameters;
    }
}