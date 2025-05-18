package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String password; // Hashed
    private String email;
    private List<String> goal;
    private String activityLevel; // e.g., "sedentary", "lightly_active", "moderately_active", "very_active", "super_active"
    private LocalDateTime createdAt;
    private String gender; 
    private double weight; // in kg
    private double height; // in cm
    private int age;
}