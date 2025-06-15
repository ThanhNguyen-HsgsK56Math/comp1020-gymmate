package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "user")
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
    private boolean profileCompleted; // Tracks if user has completed their profile setup

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public List<String> getGoal() { return goal; }
    public String getActivityLevel() { return activityLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getGender() { return gender; }
    public double getWeight() { return weight; }
    public double getHeight() { return height; }
    public int getAge() { return age; }
    public boolean isProfileCompleted() { return profileCompleted; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }
    public void setGoal(List<String> goal) { this.goal = goal; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setGender(String gender) { this.gender = gender; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setHeight(double height) { this.height = height; }
    public void setAge(int age) { this.age = age; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
}