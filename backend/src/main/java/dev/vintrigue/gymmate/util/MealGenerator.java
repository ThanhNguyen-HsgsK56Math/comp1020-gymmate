package dev.vintrigue.gymmate.util;

import java.util.*;

import dev.vintrigue.gymmate.model.Meal;
import dev.vintrigue.gymmate.model.MealDetails;

public class MealGenerator {

    public static class MealPlanResult {
        public List<MealDetails> mealSequence;
        public int totalCalories;
        public int totalPrepTime;

        public MealPlanResult(List<MealDetails> mealSequence, int totalCalories, int totalPrepTime) {
            this.mealSequence = mealSequence;
            this.totalCalories = totalCalories;
            this.totalPrepTime = totalPrepTime;
        }
    }

    /**
     * Generates a meal plan based on calorie target and dietary preferences
     * 
     * @param targetCalories The target calorie intake for the meal plan
     * @param meals Available meals to choose from
     * @param weightLossPreference A value from 1-10 indicating importance of weight loss
     * @param muscleBuildingPreference A value from 1-10 for muscle building importance
     * @param generalHealthPreference A value from 1-10 for general health importance
     * @return A meal plan meeting the calorie target and optimized for preferences
     */
    public static MealPlanResult generateMealPlan(
            int targetCalories, 
            List<Meal> meals,
            int weightLossPreference,
            int muscleBuildingPreference, 
            int generalHealthPreference) {
        
        // Distance map: state (calories) -> minimum prep time to reach that state
        Map<Integer, Integer> dist = new HashMap<>();
        // Previous state map: current state -> previous state
        Map<Integer, Integer> prevState = new HashMap<>();
        // Previous meal map: current state -> meal index that led here
        Map<Integer, Integer> prevMealIndex = new HashMap<>();
        // Priority queue to process states with minimum prep time
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.dist));

        // Calculate preference scores for each meal (higher = better)
        Map<Integer, Double> mealScores = new HashMap<>();
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            List<String> goals = new ArrayList<>();
            if (weightLossPreference > 0) goals.add("weight_loss");
            if (muscleBuildingPreference > 0) goals.add("muscle_gain");
            if (generalHealthPreference > 0) {
                goals.add("endurance_building");
                goals.add("healthy_lifestyle");
            }
            
            double score = FitnessCalculator.calculateSuitabilityScore(meal.getParameters(), goals);
            mealScores.put(i, score);
        }

        // Initial state: 0 calories, 0 prep time
        dist.put(0, 0);
        pq.offer(new Node(0, 0));

        // Target range: we want to be within 50 calories of the target
        int lowerBound = targetCalories - 50;
        int upperBound = targetCalories + 50;

        // Track the best state (closest to target calories)
        int bestState = 0;
        int bestDiff = Integer.MAX_VALUE;

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int state = current.state;
            int currentDist = current.dist;

            // If we've reached the target calorie range
            if (state >= lowerBound && state <= upperBound) {
                // Check if this is better than our current best
                int diff = Math.abs(state - targetCalories);
                if (diff < bestDiff) {
                    bestState = state;
                    bestDiff = diff;
                }
            }

            // If we've exceeded the upper bound, skip this state
            if (state > upperBound) continue;

            // Skip if this path is not the shortest to this state
            if (dist.getOrDefault(state, Integer.MAX_VALUE) < currentDist) continue;

            // Explore next states by adding each meal
            for (int i = 0; i < meals.size(); i++) {
                Meal meal = meals.get(i);
                
                // Factor in meal scores - adjust prep time based on preference match
                // (Better matches are "faster" to prepare in our algorithm)
                double mealScore = mealScores.get(i);
                double scoreAdjustment = 1.0 - (mealScore / 5.0); // 0.0 for perfect score, 0.8 for worst
                int adjustedPrepTime = (int) Math.round(meal.getPrepTime() * scoreAdjustment);
                
                // Ensure minimum prep time of 1 minute
                adjustedPrepTime = Math.max(1, adjustedPrepTime);
                
                int nextState = state + meal.getCalories();
                int nextDist = currentDist + adjustedPrepTime;

                if (nextDist < dist.getOrDefault(nextState, Integer.MAX_VALUE)) {
                    dist.put(nextState, nextDist);
                    prevState.put(nextState, state);
                    prevMealIndex.put(nextState, i);
                    pq.offer(new Node(nextState, nextDist));
                }
            }
        }

        // No plan found if bestState is still 0
        if (bestState == 0 || !prevMealIndex.containsKey(bestState)) {
            return null;
        }

        // Reconstruct the meal plan
        List<MealDetails> sequence = new ArrayList<>();
        int currentState = bestState;
        int actualPrepTime = 0; // Track actual prep time (not adjusted)
        
        while (currentState != 0) {
            int mealIndex = prevMealIndex.get(currentState);
            Meal meal = meals.get(mealIndex);
            
            // Create MealDetails from the Meal
            MealDetails details = MealDetails.fromMeal(meal);
            sequence.add(details);
            
            // Add actual prep time (not the adjusted one used for calculations)
            actualPrepTime += meal.getPrepTime();
            
            currentState = prevState.get(currentState);
        }
        Collections.reverse(sequence);
        
        return new MealPlanResult(sequence, bestState, actualPrepTime);
    }

    static class Node {
        int state; // Cumulative calories
        int dist;  // Total prep time to reach this state

        Node(int state, int dist) {
            this.state = state;
            this.dist = dist;
        }
    }
} 