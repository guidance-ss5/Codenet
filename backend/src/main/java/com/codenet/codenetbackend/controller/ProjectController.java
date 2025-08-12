package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.dto.StatsDTO;
import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.service.ProjectService;
import com.codenet.codenetbackend.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
<<<<<<< HEAD
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
=======

>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
<<<<<<< HEAD
    private final FileStorageService fileStorageService;
    
    public ProjectController(ProjectService projectService, FileStorageService fileStorageService) {
=======

    public ProjectController(ProjectService projectService) {
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
        this.projectService = projectService;
        this.fileStorageService = fileStorageService;
    }
<<<<<<< HEAD
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Project> createProject(@RequestPart("project") Project project,
                                                @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles,
                                                @RequestAttribute("clerkUserId") String clerkUserId) throws IOException {
        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            List<String> mediaUrls = new ArrayList<>();
            for (MultipartFile file : mediaFiles) {
                mediaUrls.add(fileStorageService.storeFile(file));
            }
            project.setMediaUrls(mediaUrls);
        }
        Project createdProject = projectService.createProject(project, clerkUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }
    
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Project> createProjectJson(@RequestBody Project project,
                                                    @RequestAttribute("clerkUserId") String clerkUserId) {
=======

    /**
     * Create a new project.
     */
    @PostMapping
    public ResponseEntity<Project> createProject(
        @RequestBody Project project,
        @RequestAttribute("clerkUserId") String clerkUserId) {

>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
        Project createdProject = projectService.createProject(project, clerkUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    /**
     * Get project by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable String id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all approved projects or search projects by query.
     */
    @GetMapping
    public List<Project> getAllProjects(@RequestParam(required = false) String search) {
        if (search != null && !search.isEmpty()) {
            return projectService.searchProjects(search);
        }
        return projectService.getAllApprovedProjects();
    }
<<<<<<< HEAD
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Project> updateProject(@PathVariable String id, 
                                                @RequestPart("project") Project project,
                                                @RequestPart(value = "media", required = false) List<MultipartFile> mediaFiles,
                                                @RequestAttribute("clerkUserId") String clerkUserId) throws IOException {
        return projectService.getProjectById(id)
            .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
            .map(existingProject -> {
                try {
                    if (mediaFiles != null && !mediaFiles.isEmpty()) {
                        List<String> mediaUrls = new ArrayList<>();
                        for (MultipartFile file : mediaFiles) {
                            mediaUrls.add(fileStorageService.storeFile(file));
                        }
                        project.setMediaUrls(mediaUrls);
                    }
                    return projectService.updateProject(id, project)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
                } catch (IOException e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).<Project>build();
                }
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
    
    @PutMapping(value = "/{id}", consumes = {"application/json"})
    public ResponseEntity<Project> updateProjectJson(@PathVariable String id, 
                                                    @RequestBody Project project,
                                                    @RequestAttribute("clerkUserId") String clerkUserId) {
=======

    /**
     * Update a project if the logged-in user is the owner.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(
        @PathVariable String id,
        @RequestBody Project project,
        @RequestAttribute("clerkUserId") String clerkUserId) {

>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
        return projectService.getProjectById(id)
            .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
            .flatMap(existingProject -> projectService.updateProject(id, project))
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    /**
     * Delete a project if the logged-in user is the owner.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
        @PathVariable String id,
        @RequestAttribute("clerkUserId") String clerkUserId) {

        return projectService.getProjectById(id)
            .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
            .map(existingProject -> {
                projectService.deleteProject(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    /**
     * Like a project (increment likes count).
     */
    @PutMapping("/{id}/like")
    public ResponseEntity<Project> likeProject(@PathVariable String id) {
        return projectService.likeProject(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get trending projects.
     */
    @GetMapping("/trending")
    public List<Project> getTrendingProjects() {
        return projectService.getTrendingProjects();
    }
<<<<<<< HEAD
    
    @GetMapping("/my")
    public List<Project> getMyProjects(@RequestAttribute("clerkUserId") String clerkUserId) {
        return projectService.getProjectsByOwner(clerkUserId);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        long totalProjects = projectService.getTotalProjects();
        // You can add more stats here as needed
        return ResponseEntity.ok(new StatsDTO(totalProjects, 0, 0, 0, 0));
=======

    /**
     * Get basic statistics for the app.
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        long totalProjects = projectService.getTotalProjects();
        long collaborators = projectService.getTotalCollaborators();
        long soldProjects = projectService.getSoldProjectsCount();
        long users = projectService.getUsersCount();

        StatsDTO stats = new StatsDTO(totalProjects, collaborators, soldProjects, users);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get projects owned by the currently logged-in user.
     */
    @GetMapping("/my")
    public List<Project> getMyProjects(@RequestAttribute("clerkUserId") String clerkUserId) {
        return projectService.getProjectsByOwner(clerkUserId);
    }

    // You may need to add your StatsDTO class or import it if external
    public static class StatsDTO {
        private long projectsUploaded;
        private long collaborators;
        private long soldProjects;
        private long users;

        public StatsDTO(long projectsUploaded, long collaborators, long soldProjects, long users) {
            this.projectsUploaded = projectsUploaded;
            this.collaborators = collaborators;
            this.soldProjects = soldProjects;
            this.users = users;
        }

        // Getters and setters (or use Lombok @Data to generate)
        public long getProjectsUploaded() {
            return projectsUploaded;
        }

        public void setProjectsUploaded(long projectsUploaded) {
            this.projectsUploaded = projectsUploaded;
        }

        public long getCollaborators() {
            return collaborators;
        }

        public void setCollaborators(long collaborators) {
            this.collaborators = collaborators;
        }

        public long getSoldProjects() {
            return soldProjects;
        }

        public void setSoldProjects(long soldProjects) {
            this.soldProjects = soldProjects;
        }

        public long getUsers() {
            return users;
        }

        public void setUsers(long users) {
            this.users = users;
        }
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
    }
}
