package dev.vintrigue.gymmate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.vintrigue.gymmate.model.MealPlan;

public interface MealPlanRepository extends MongoRepository<MealPlan, String> {
} 