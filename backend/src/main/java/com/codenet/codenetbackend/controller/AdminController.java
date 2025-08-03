package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {
    
    private final ProjectService projectService;
    
    public AdminController(ProjectService projectService) {
        this.projectService = projectService;
    }
    
    @GetMapping("/projects/pending")
    public ResponseEntity<List<Project>> getPendingProjects() {
        List<Project> pendingProjects = projectService.getPendingProjects();
        return ResponseEntity.ok(pendingProjects);
    }
    
    @PutMapping("/projects/{id}/approve")
    public ResponseEntity<Project> approveProject(@PathVariable String id) {
        return projectService.approveProject(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PutMapping("/projects/{id}/reject")
    public ResponseEntity<Project> rejectProject(@PathVariable String id, @RequestBody String reason) {
        return projectService.rejectProject(id, reason)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Object> getAdminStats() {
        long totalProjects = projectService.getTotalProjects();
        long pendingProjects = projectService.getPendingProjectsCount();
        long approvedProjects = projectService.getApprovedProjectsCount();
        
        return ResponseEntity.ok(new AdminStatsDTO(totalProjects, pendingProjects, approvedProjects));
    }
    
    // DTO for admin stats
    public static class AdminStatsDTO {
        private long totalProjects;
        private long pendingProjects;
        private long approvedProjects;
        
        public AdminStatsDTO(long totalProjects, long pendingProjects, long approvedProjects) {
            this.totalProjects = totalProjects;
            this.pendingProjects = pendingProjects;
            this.approvedProjects = approvedProjects;
        }
        
        // Getters
        public long getTotalProjects() { return totalProjects; }
        public long getPendingProjects() { return pendingProjects; }
        public long getApprovedProjects() { return approvedProjects; }
    }
} 