package com.codenet.codenetbackend.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsDTO {
    private long totalProjects;
    private long totalPendingProjects;
    private long totalUsers;
    private long totalLikes;
    private long totalViews;
}
