package dev.vintrigue.gymmate.util;

import java.util.*;

import dev.vintrigue.gymmate.model.Exercise;

public class ExercisePlanGenerator {

    public static class ExercisePlanResult {
        public List<Exercise> exerciseSequence;
        public int totalCaloriesBurned;
        public int totalDuration;

        public ExercisePlanResult(List<Exercise> exerciseSequence, int totalCaloriesBurned, int totalDuration) {
            this.exerciseSequence = exerciseSequence;
            this.totalCaloriesBurned = totalCaloriesBurned;
            this.totalDuration = totalDuration;
        }
    }

    /**
     * Generates an exercise plan based on calorie target and fitness preferences
     * 
     * @param targetCaloriesBurned The target calories to burn
     * @param exercises Available exercises to choose from
     * @param weightLossPreference A value from 1-10 indicating importance of weight loss
     * @param muscleBuildingPreference A value from 1-10 for muscle building importance
     * @param endurancePreference A value from 1-10 for endurance building importance
     * @param healthPreference A value from 1-10 for general health importance
     * @return An exercise plan meeting the calorie target and optimized for preferences
     */
    public static ExercisePlanResult generateExercisePlan(
            int targetCaloriesBurned, 
            List<Exercise> exercises,
            int weightLossPreference,
            int muscleBuildingPreference, 
            int endurancePreference,
            int healthPreference) {
        
        // Distance map: state (calories burned) -> minimum duration to reach that state
        Map<Integer, Integer> dist = new HashMap<>();
        // Previous state map: current state -> previous state
        Map<Integer, Integer> prevState = new HashMap<>();
        // Previous exercise map: current state -> exercise index that led here
        Map<Integer, Integer> prevExerciseIndex = new HashMap<>();
        // Priority queue to process states with minimum duration
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.dist));

        // Calculate preference scores for each exercise (higher = better)
        Map<Integer, Double> exerciseScores = new HashMap<>();
        for (int i = 0; i < exercises.size(); i++) {
            Exercise exercise = exercises.get(i);
            List<String> goals = new ArrayList<>();
            if (weightLossPreference > 0) goals.add("weight_loss");
            if (muscleBuildingPreference > 0) goals.add("muscle_gain");
            if (endurancePreference > 0) goals.add("endurance_building");
            if (healthPreference > 0) goals.add("healthy_lifestyle");
            
            double score = FitnessCalculator.calculateSuitabilityScore(exercise.getParameters(), goals);
            exerciseScores.put(i, score);
        }

        // Initial state: 0 calories burned, 0 duration
        dist.put(0, 0);
        pq.offer(new Node(0, 0));

        // Target range: we want to be within 50 calories of the target
        int lowerBound = targetCaloriesBurned - 50;
        int upperBound = targetCaloriesBurned + 50;

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
                int diff = Math.abs(state - targetCaloriesBurned);
                if (diff < bestDiff) {
                    bestState = state;
                    bestDiff = diff;
                }
            }

            // If we've exceeded the upper bound, skip this state
            if (state > upperBound) continue;

            // Skip if this path is not the shortest to this state
            if (dist.getOrDefault(state, Integer.MAX_VALUE) < currentDist) continue;

            // Explore next states by adding each exercise
            for (int i = 0; i < exercises.size(); i++) {
                Exercise exercise = exercises.get(i);
                
                // Calculate edge weight based on suitability and calories
                double exerciseScore = exerciseScores.get(i);
                double edgeWeight = FitnessCalculator.calculateExerciseWeight(
                    exerciseScore,
                    exercise.getCaloriesBurned(),
                    targetCaloriesBurned
                );
                
                // Use edge weight to adjust duration
                int adjustedDuration = (int) Math.round(exercise.getDuration() * edgeWeight);
                
                // Ensure minimum duration of 1 minute
                adjustedDuration = Math.max(1, adjustedDuration);
                
                int nextState = state + exercise.getCaloriesBurned();
                int nextDist = currentDist + adjustedDuration;

                if (nextDist < dist.getOrDefault(nextState, Integer.MAX_VALUE)) {
                    dist.put(nextState, nextDist);
                    prevState.put(nextState, state);
                    prevExerciseIndex.put(nextState, i);
                    pq.offer(new Node(nextState, nextDist));
                }
            }
        }

        // No plan found if bestState is still 0
        if (bestState == 0 || !prevExerciseIndex.containsKey(bestState)) {
            return null;
        }

        // Reconstruct the exercise plan
        List<Exercise> sequence = new ArrayList<>();
        int currentState = bestState;
        int actualDuration = 0; // Track actual duration (not adjusted)
        
        while (currentState != 0) {
            int exerciseIndex = prevExerciseIndex.get(currentState);
            Exercise exercise = exercises.get(exerciseIndex);
            sequence.add(exercise);
            
            // Add actual duration (not the adjusted one used for calculations)
            actualDuration += exercise.getDuration();
            
            currentState = prevState.get(currentState);
        }
        Collections.reverse(sequence);
        
        return new ExercisePlanResult(sequence, bestState, actualDuration);
    }

    static class Node {
        int state; // Cumulative calories burned
        int dist;  // Total duration to reach this state

        Node(int state, int dist) {
            this.state = state;
            this.dist = dist;
        }
    }
} 