package dev.vintrigue.gymmate.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import dev.vintrigue.gymmate.model.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
}