package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "meals")
@Data
public class Meal {
    @Id
    private String id;
    private String name;
    private String description;
    private int calories; // Calorie intake
    private int prepTime; // in minutes
    private Map<String, Integer> parameters; // e.g., {"weight_loss": 8, "muscle_gain": 7, "general_health": 8}
}