package dev.vintrigue.gymmate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.vintrigue.gymmate.model.Meal;

public interface MealRepository extends MongoRepository<Meal, String> {
}