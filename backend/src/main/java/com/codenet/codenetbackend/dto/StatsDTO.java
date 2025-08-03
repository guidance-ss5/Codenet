package com.codenet.codenetbackend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDTO {
    private long projectsUploaded;
    private long users;
    private long soldProjects;
    private long collaborators;
}
