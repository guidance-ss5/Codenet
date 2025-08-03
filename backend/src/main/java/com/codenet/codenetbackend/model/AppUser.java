package com.codenet.codenetbackend.model;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "users")
public class AppUser {
    @Id
    private String id; // Clerk's user ID
    private String email;
    private String username;
    private Instant joinedAt;
    private String role;
}
