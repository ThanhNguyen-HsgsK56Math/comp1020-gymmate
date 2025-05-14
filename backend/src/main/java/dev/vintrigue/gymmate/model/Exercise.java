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
}