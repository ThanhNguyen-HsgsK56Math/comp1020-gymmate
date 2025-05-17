package dev.vintrigue.gymmate.util;

import java.util.*;

import dev.vintrigue.gymmate.model.Exercise;
import dev.vintrigue.gymmate.model.ExerciseDetails;

public class PlanGenerator {

    public static class PlanResult {
        public List<ExerciseDetails> exerciseSequence;
        public int totalCalories;
        public int totalTime;

        public PlanResult(List<ExerciseDetails> exerciseSequence, int totalCalories, int totalTime) {
            this.exerciseSequence = exerciseSequence;
            this.totalCalories = totalCalories;
            this.totalTime = totalTime;
        }
    }

    public static PlanResult generatePlan(int goalCalories, List<Exercise> exercises) {
        // Distance map: state (calories burned) -> minimum time to reach that state
        Map<Integer, Integer> dist = new HashMap<>();
        // Previous state map: current state -> previous state
        Map<Integer, Integer> prevState = new HashMap<>();
        // Previous exercise map: current state -> exercise index that led here
        Map<Integer, Integer> prevExerciseIndex = new HashMap<>();
        // Priority queue to process states with minimal time
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.dist));

        // Initial state: 0 calories burned, 0 time
        dist.put(0, 0);
        pq.offer(new Node(0, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int state = current.state;
            int currentDist = current.dist;

            // If goal is reached or exceeded, reconstruct the plan
            if (state >= goalCalories) {
                List<ExerciseDetails> sequence = new ArrayList<>();
                int currentState = state;
                while (currentState != 0) {
                    int exerciseIndex = prevExerciseIndex.get(currentState);
                    Exercise exercise = exercises.get(exerciseIndex);
                    
                    // Create ExerciseDetails from the Exercise
                    ExerciseDetails details = ExerciseDetails.fromExercise(exercise);
                    sequence.add(details);
                    
                    currentState = prevState.get(currentState);
                }
                Collections.reverse(sequence);
                return new PlanResult(sequence, state, dist.get(state));
            }

            // Skip if this path is not the shortest to this state
            if (dist.getOrDefault(state, Integer.MAX_VALUE) < currentDist) continue;

            // Explore next states by adding each exercise
            for (int i = 0; i < exercises.size(); i++) { // Iterate through all exercises
                Exercise exercise = exercises.get(i); // Get the exercise at index i
                int nextState = state + exercise.getCaloriesBurned(); // Calculate the next state by adding the calories burned of the current exercise
                int nextDist = currentDist + exercise.getTimeRequired(); // Calculate the next distance by adding the time required of the current exercise

                if (nextDist < dist.getOrDefault(nextState, Integer.MAX_VALUE)) {
                    dist.put(nextState, nextDist);
                    prevState.put(nextState, state);
                    prevExerciseIndex.put(nextState, i);
                    pq.offer(new Node(nextState, nextDist));
                }
            }
        }
        return null; // No plan found (shouldn't happen with sufficient exercises)
    }

    static class Node {
        int state; // Cumulative calories burned
        int dist;  // Total time to reach this state

        Node(int state, int dist) {
            this.state = state;
            this.dist = dist;
        }
    }
}