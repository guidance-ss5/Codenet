package com.codenet.codenetbackend.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "projects")
public class Project {
    @Id
    private String id;
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;
    
    @Size(max = 200, message = "Subtitle must not exceed 200 characters")
    private String subtitle;
    
    @NotBlank(message = "Description is required")
    @Size(min = 50, max = 5000, message = "Description must be between 50 and 5000 characters")
    private String description;
    
    @NotBlank(message = "Owner ID is required")
    private String ownerId;
    
    @Size(max = 10, message = "Maximum 10 media files allowed")
    private List<String> mediaUrls = new ArrayList<>();
    
    @Min(value = 0, message = "Likes cannot be negative")
    private long likes = 0;
    
    @Min(value = 0, message = "Views cannot be negative")
    private long views = 0;
    private Instant uploadDate;
    private String status = "PENDING";
    private boolean isFeatured = false;
}
