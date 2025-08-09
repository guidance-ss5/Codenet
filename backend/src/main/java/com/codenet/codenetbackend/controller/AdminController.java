package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.model.AppUser;
import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.service.AppUserService;
import com.codenet.codenetbackend.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final ProjectService projectService;
    private final AppUserService appUserService;

    public AdminController(ProjectService projectService, AppUserService appUserService) {
        this.projectService = projectService;
        this.appUserService = appUserService;
    }

    // Project Management
    @GetMapping("/projects/pending")
    public List<Project> getPendingProjects() {
        return projectService.getProjectsByStatus("PENDING");
    }

    @GetMapping("/projects/rejected")
    public List<Project> getRejectedProjects() {
        return projectService.getProjectsByStatus("REJECTED");
    }

    @PutMapping("/projects/{id}/approve")
    public ResponseEntity<Project> approveProject(@PathVariable String id) {
        return projectService.approveProject(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}/reject")
    public ResponseEntity<Project> rejectProject(@PathVariable String id) {
        return projectService.rejectProject(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/projects/{id}/feature")
    public ResponseEntity<Project> featureProject(@PathVariable String id) {
        return projectService.featureProject(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // User Management
    @GetMapping("/users")
    public List<AppUser> getAllUsers() {
        return appUserService.getAllActiveUsers();
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable String id) {
        appUserService.deactivateUser(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        appUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Statistics
    @GetMapping("/stats")
    public ResponseEntity<Object> getAdminStats() {
        long totalProjects = projectService.getTotalProjects();
        long approvedProjects = projectService.getTotalApprovedProjects();
        long pendingProjects = projectService.getTotalPendingProjects();
        long totalUsers = appUserService.getTotalActiveUsers();
        long totalLikes = projectService.getTotalLikes();
        long totalViews = projectService.getTotalViews();

        return ResponseEntity.ok(Map.of(
            "totalProjects", totalProjects,
            "approvedProjects", approvedProjects,
            "pendingProjects", pendingProjects,
            "totalUsers", totalUsers,
            "totalLikes", totalLikes,
            "totalViews", totalViews
        ));
    }
} 