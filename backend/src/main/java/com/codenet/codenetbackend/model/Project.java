package com.codenet.codenetbackend.model;
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
    private String title;
    private String subtitle;
    private String description;
    private String ownerId;
    private List<String> mediaUrls = new ArrayList<>();
    private long likes = 0;
    private Instant uploadDate;
    private String status = "PENDING";
}
