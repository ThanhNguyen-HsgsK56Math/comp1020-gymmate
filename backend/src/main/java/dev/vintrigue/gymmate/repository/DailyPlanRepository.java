package dev.vintrigue.gymmate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.vintrigue.gymmate.model.DailyPlan;

public interface DailyPlanRepository extends MongoRepository<DailyPlan, String> {
}