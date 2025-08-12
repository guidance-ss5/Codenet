package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.dto.StatsDTO;
import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.service.ProjectService;
import com.codenet.codenetbackend.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController { 

    private final ProjectService projectService;
    private final FileStorageService fileStorageService;

    public ProjectController(ProjectService projectService, FileStorageService fileStorageService) {
        this.projectService = projectService;
        this.fileStorageService = fileStorageService;
    }
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

        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<Project> createProjectJson(@RequestBody Project project,
        Project createdProject = projectService.createProject(project, clerkUserId);
        return new ResponseEntity<>(createdProject, HttpStatus.CREATED);
    }
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


    package com.codenet.codenetbackend.controller;

    import com.codenet.codenetbackend.dto.StatsDTO;
    import com.codenet.codenetbackend.model.Project;
    import com.codenet.codenetbackend.service.ProjectService;
    import com.codenet.codenetbackend.service.FileStorageService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.multipart.MultipartFile;
    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/projects")
    public class ProjectController {
        private final ProjectService projectService;
        private final FileStorageService fileStorageService;

        public ProjectController(ProjectService projectService, FileStorageService fileStorageService) {
            this.projectService = projectService;
            this.fileStorageService = fileStorageService;
        }

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
                                                return new ResponseEntity<>(projectService.createProject(project, clerkUserId), HttpStatus.CREATED);
        }

        @PostMapping(consumes = {"application/json"})
        public ResponseEntity<Project> createProjectJson(@RequestBody Project project,
        return new ResponseEntity<>(projectService.createProject(project, clerkUserId), HttpStatus.CREATED);
        }

        @GetMapping("/{id}")
        public ResponseEntity<Project> getProjectById(@PathVariable String id) {
            Optional<Project> project = projectService.getProjectById(id);
            return project.map(ResponseEntity::ok)
                          .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping
        public List<Project> getAllProjects(@RequestParam(required = false) String search) {
            if (search != null && !search.isEmpty()) {
                return projectService.searchProjects(search);
            }
            return projectService.getAllApprovedProjects();
        }

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
            return projectService.getProjectById(id)
                .filter(existingProject -> existingProject.getOwnerId().equals(clerkUserId))
                .flatMap(existingProject -> projectService.updateProject(id, project))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
        }

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

        @GetMapping("/my")
        public List<Project> getMyProjects(@RequestAttribute("clerkUserId") String clerkUserId) {
            return projectService.getProjectsByOwner(clerkUserId);
        }

        @GetMapping("/stats")
        public ResponseEntity<StatsDTO> getStats() {
            long totalProjects = projectService.getTotalProjects();
            long collaborators = projectService.getTotalCollaborators();
            long soldProjects = projectService.getSoldProjectsCount();
            long users = projectService.getUsersCount();
            StatsDTO stats = new StatsDTO(totalProjects, collaborators, soldProjects, users);
            return ResponseEntity.ok(stats);
        }
    }
    }
}
