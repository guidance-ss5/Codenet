package com.codenet.codenetbackend.service;

import com.codenet.codenetbackend.model.Project;
import com.codenet.codenetbackend.repository.ProjectRepository;
import com.codenet.codenetbackend.exception.ProjectNotFoundException;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
    public Project createProject(Project project, String ownerId) {
        project.setOwnerId(ownerId);
        project.setUploadDate(Instant.now());
        project.setStatus("PENDING");
        project.setLikes(0);
        return projectRepository.save(project);
    }
    public Project getProjectByIdOrThrow(String id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
    }

    public Optional<Project> getProjectById(String id) {
        return projectRepository.findById(id);
    }
    public List<Project> getAllApprovedProjects() {
        return projectRepository.findByStatus("APPROVED");
    }
    public List<Project> getProjectsByOwner(String ownerId) {
        return projectRepository.findByOwnerId(ownerId);
    }
    public List<Project> searchProjects(String query) {
        return projectRepository.searchProjects(query);
    }
    public Project updateProjectOrThrow(String id, Project updatedProject) {
        Project project = getProjectByIdOrThrow(id);
        project.setTitle(updatedProject.getTitle());
        project.setSubtitle(updatedProject.getSubtitle());
        project.setDescription(updatedProject.getDescription());
        project.setMediaUrls(updatedProject.getMediaUrls());
        return projectRepository.save(project);
    }

    public Optional<Project> updateProject(String id, Project updatedProject) {
        return projectRepository.findById(id)
            .map(project -> {
                project.setTitle(updatedProject.getTitle());
                project.setSubtitle(updatedProject.getSubtitle());
                project.setDescription(updatedProject.getDescription());
                project.setMediaUrls(updatedProject.getMediaUrls());
                return projectRepository.save(project);
            });
    }
    public void deleteProjectOrThrow(String id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }

    public boolean deleteProject(String id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public Project likeProjectOrThrow(String id) {
        Project project = getProjectByIdOrThrow(id);
        project.setLikes(project.getLikes() + 1);
        return projectRepository.save(project);
    }

    public Optional<Project> likeProject(String id) {
        return projectRepository.findById(id)
            .map(project -> {
                project.setLikes(project.getLikes() + 1);
                return projectRepository.save(project);
            });
    }
    public List<Project> getTrendingProjects() {
        return projectRepository.findTop10ByOrderByLikesDesc();
    }
    public long getTotalProjects() {
        return projectRepository.count();
    }
    
    // Admin methods
    public List<Project> getPendingProjects() {
        return projectRepository.findByStatus("PENDING");
    }
    
    public Optional<Project> approveProject(String id) {
        return projectRepository.findById(id)
            .map(project -> {
                project.setStatus("APPROVED");
                return projectRepository.save(project);
            });
    }
    
    public Optional<Project> rejectProject(String id, String reason) {
        return projectRepository.findById(id)
            .map(project -> {
                project.setStatus("REJECTED");
                // You could add a rejection reason field to the Project model
                return projectRepository.save(project);
            });
    }
    
    public long getPendingProjectsCount() {
        return projectRepository.findByStatus("PENDING").size();
    }
    
    public long getApprovedProjectsCount() {
        return projectRepository.findByStatus("APPROVED").size();
    }
    
    // Additional missing methods
    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }
    
    public Optional<Project> featureProject(String id) {
        return projectRepository.findById(id)
            .map(project -> {
                project.setFeatured(true);
                return projectRepository.save(project);
            });
    }
    
    public long getTotalApprovedProjects() {
        return projectRepository.findByStatus("APPROVED").size();
    }
    
    public long getTotalPendingProjects() {
        return projectRepository.findByStatus("PENDING").size();
    }
    
    public long getTotalLikes() {
        return projectRepository.findAll().stream()
            .mapToLong(Project::getLikes)
            .sum();
    }
    
    public long getTotalViews() {
        return projectRepository.findAll().stream()
            .mapToLong(Project::getViews)
            .sum();
    }
}
