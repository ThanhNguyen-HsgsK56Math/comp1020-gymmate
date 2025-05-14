package dev.vintrigue.gymmate.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "user")
@Data
public class User {
    @Id
    private String id;
    private String username;
    private String password; // Hashed
    private String email;
    private String goal; // e.g., "burn_500_calories"
    private LocalDateTime createdAt;
}