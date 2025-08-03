package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }
    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project, @RequestAttribute("clerkUserId") String clerkUserId) {
        Project createdProject = projectService.createProject(project, clerkUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        return projectService.getProjectById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping
    public List<Project> getAllProjects(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return projectService.searchProjects(search);
        }
        return projectService.getAllApprovedProjects();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody Project project, @RequestAttribute("clerkUserId") String clerkUserId) {
        return projectService.getProjectById(id)
            .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
            .flatMap(existingProject -> projectService.updateProject(id, project))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id, @RequestAttribute("clerkUserId") String clerkUserId) {
        return projectService.getProjectById(id)
            .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
            .map(existingProject -> {
                projectService.deleteProject(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
    @PutMapping("/{id}/like")
    public ResponseEntity<Project> likeProject(@PathVariable String id) {
        return projectService.likeProject(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/trending")
    public List<Project> getTrendingProjects() {
        return projectService.getTrendingProjects();
    }
    @GetMapping("/stats")
    public ResponseEntity<Object> getStats() {
        long totalProjects = projectService.getTotalProjects();
        // You can add more stats here as needed
        return ResponseEntity.ok(new StatsDTO(totalProjects, 0, 0, 0));
    }
}
