package com.codenet.codenetbackend.model;

<<<<<<< HEAD
import jakarta.validation.constraints.*;
=======
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
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
<<<<<<< HEAD
    
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
=======

>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
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
<<<<<<< HEAD
    
    @Min(value = 0, message = "Views cannot be negative")
    private long views = 0;
    private Instant uploadDate;
    private String status = "PENDING";
    private boolean isFeatured = false;
=======

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public List<String> getMediaUrls() { return mediaUrls; }
    public void setMediaUrls(List<String> mediaUrls) { this.mediaUrls = mediaUrls; }
    public long getLikes() { return likes; }
    public void setLikes(long likes) { this.likes = likes; }
    public Instant getUploadDate() { return uploadDate; }
    public void setUploadDate(Instant uploadDate) { this.uploadDate = uploadDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    private Instant uploadDate;
    private String status = "PENDING";

    // No need for manual getters/setters or constructor here,
    // Lombok @Data generates them automatically.
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
}
