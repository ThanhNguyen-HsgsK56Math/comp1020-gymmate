package dev.vintrigue.gymmate.repository;

import dev.vintrigue.gymmate.model.ExercisePlan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExercisePlanRepository extends MongoRepository<ExercisePlan, String> {
} 