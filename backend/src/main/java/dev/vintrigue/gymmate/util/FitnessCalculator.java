package dev.vintrigue.gymmate.util;

import java.util.List;
import java.util.Map;

public class FitnessCalculator {
    
    /**
     * Calculate BMR (Basal Metabolic Rate) using the Mifflin-St Jeor Equation
     * @param weight in kg
     * @param height in cm
     * @param age in years
     * @param gender "male" or "female"
     * @return BMR value
     */
    public static double calculateBMR(double weight, double height, int age, String gender) {
        double bmr = 10 * weight + 6.25 * height - 5 * age;
        return gender.equalsIgnoreCase("male") ? bmr + 5 : bmr - 161;
    }

    /**
     * Calculate TDEE (Total Daily Energy Expenditure)
     * @param bmr Basal Metabolic Rate
     * @param activityLevel Activity factor based on user's activity level
     * @return TDEE value
     */
    public static double calculateTDEE(double bmr, String activityLevel) {
        double activityFactor = switch (activityLevel.toLowerCase()) {
            case "sedentary" -> 1.2;
            case "lightly_active" -> 1.375;
            case "moderately_active" -> 1.55;
            case "very_active" -> 1.725;
            case "super_active" -> 1.9;
            default -> 1.2;
        };
        return bmr * activityFactor;
    }

    /**
     * Calculate suitability score using the formula: α × w + β × m + θ × e + γ × l
     * where w, m, e, l are suitability values for weight loss, muscle gain, endurance building,
     * and healthy lifestyle maintenance respectively.
     * 
     * @param parameters Map containing suitability scores for different aspects
     * @param goals List of user's goals
     * @return weighted suitability score
     */
    public static double calculateSuitabilityScore(Map<String, Integer> parameters, List<String> goals) {
        // Initialize coefficients
        double alpha = 0.0; // weight loss coefficient
        double beta = 0.0;  // muscle gain coefficient
        double theta = 0.0; // endurance building coefficient
        double gamma = 0.0; // healthy lifestyle coefficient
        
        // Set coefficients based on goals
        if (goals.size() == 1) {
            switch (goals.get(0)) {
                case "weight_loss" -> alpha = 1.0;
                case "muscle_gain" -> beta = 1.0;
                case "endurance_building" -> theta = 1.0;
                case "healthy_lifestyle" -> gamma = 1.0;
            }
        } else if (goals.size() == 2) {
            // Equal weight (0.5) for both goals
            for (String goal : goals) {
                switch (goal) {
                    case "weight_loss" -> alpha = 0.5;
                    case "muscle_gain" -> beta = 0.5;
                    case "endurance_building" -> theta = 0.5;
                    case "healthy_lifestyle" -> gamma = 0.5;
                }
            }
        }

        // Get suitability values from parameters
        int w = parameters.getOrDefault("suitability_weight_loss", 0);
        int m = parameters.getOrDefault("suitability_muscle_gain", 0);
        int e = parameters.getOrDefault("suitability_endurance_building", 0);
        int l = parameters.getOrDefault("suitability_healthy_lifestyle", 0);

        // Calculate weighted sum using the formula: α × w + β × m + θ × e + γ × l
        return (alpha * w) + (beta * m) + (theta * e) + (gamma * l);
    }

    /**
     * Calculate edge weight for exercise using the formula: 1 / (suitability * (caloriesBurned/TDEE))
     * @param suitability Suitability score
     * @param caloriesBurned Calories burned by the exercise
     * @param tdee Total Daily Energy Expenditure
     * @return edge weight
     */
    public static double calculateExerciseWeight(double suitability, int caloriesBurned, double tdee) {
        if (suitability <= 0 || caloriesBurned <= 0 || tdee <= 0) {
            return Double.MAX_VALUE; // Return maximum value for invalid inputs
        }
        return 1.0 / (suitability * (caloriesBurned / tdee));
    }

    /**
     * Calculate edge weight for meal using the formula: 1 / (suitability * (caloriesIntake/BMR))
     * @param suitability Suitability score
     * @param caloriesIntake Calories in the meal
     * @param bmr Basal Metabolic Rate
     * @return edge weight
     */
    public static double calculateMealWeight(double suitability, int caloriesIntake, double bmr) {
        if (suitability <= 0 || caloriesIntake <= 0 || bmr <= 0) {
            return Double.MAX_VALUE; // Return maximum value for invalid inputs
        }
        return 1.0 / (suitability * (caloriesIntake / bmr));
    }
} 