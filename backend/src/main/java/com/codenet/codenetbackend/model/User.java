package com.codenet.codenetbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String clerkUserId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isActive = true;
}
