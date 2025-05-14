package dev.vintrigue.gymmate.util;

import java.util.*;

import dev.vintrigue.gymmate.model.Exercise;

public class PlanGenerator {

    public static class PlanResult {
        public List<String> exerciseSequence;
        public int totalCalories;
        public int totalTime;

        public PlanResult(List<String> exerciseSequence, int totalCalories, int totalTime) {
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
        // Previous exercise map: current state -> exercise ID that led here
        Map<Integer, String> prevExercise = new HashMap<>();
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
                List<String> sequence = new ArrayList<>();
                int currentState = state;
                while (currentState != 0) {
                    String exerciseId = prevExercise.get(currentState);
                    sequence.add(exerciseId);
                    currentState = prevState.get(currentState);
                }
                Collections.reverse(sequence);
                return new PlanResult(sequence, state, dist.get(state));
            }

            // Skip if this path is not the shortest to this state
            if (dist.getOrDefault(state, Integer.MAX_VALUE) < currentDist) continue;

            // Explore next states by adding each exercise
            for (Exercise exercise : exercises) {
                int nextState = state + exercise.getCaloriesBurned();
                int nextDist = currentDist + exercise.getTimeRequired();

                if (nextDist < dist.getOrDefault(nextState, Integer.MAX_VALUE)) {
                    dist.put(nextState, nextDist);
                    prevState.put(nextState, state);
                    prevExercise.put(nextState, exercise.getId());
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