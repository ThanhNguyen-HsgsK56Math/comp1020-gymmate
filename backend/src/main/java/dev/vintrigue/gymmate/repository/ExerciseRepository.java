package dev.vintrigue.gymmate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.vintrigue.gymmate.model.Exercise;

public interface ExerciseRepository extends MongoRepository<Exercise, String> {
}