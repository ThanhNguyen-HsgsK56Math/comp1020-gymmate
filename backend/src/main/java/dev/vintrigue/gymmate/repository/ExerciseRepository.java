package dev.vintrigue.gymmate.repository;

import dev.vintrigue.gymmate.model.Exercise;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {
}