package com.codenet.codenetbackend.service;

import com.codenet.codenetbackend.model.Project; 
import com.codenet.codenetbackend.repository.ProjectRepository; 
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
project.setStatus("PENDING"); // New projects are pending approval 
project.setLikes(0); 
return projectRepository.save(project); 
} 
public Optional<Project> getProjectById(String id) { 
return projectRepository.findById(id); 
} 
public List<Project> getAllApprovedProjects() { 
// Only return approved projects for public viewing 
return projectRepository.findByStatus("APPROVED"); 
} 
public List<Project> getProjectsByOwner(String ownerId) { 
return projectRepository.findByOwnerId(ownerId); 
} 
    public List<Project> searchProjects(String query) { 
        return projectRepository.searchProjects(query); 
    } 
 
    public Optional<Project> updateProject(String id, Project updatedProject) { 
        return projectRepository.findById(id) 
                .map(project -> { 
                    project.setTitle(updatedProject.getTitle()); 
                    project.setSubtitle(updatedProject.getSubtitle()); 
                    project.setDescription(updatedProject.getDescription()); 
                    project.setMediaUrls(updatedProject.getMediaUrls()); 
                    // Admin can change status, user cannot directly 
                    // project.setStatus(updatedProject.getStatus()); 
                    return projectRepository.save(project); 
                }); 
    } 
 
    public boolean deleteProject(String id) { 
        if (projectRepository.existsById(id)) { 
            projectRepository.deleteById(id); 
            return true; 
        } 
        return false; 
    } 
 
    public Optional<Project> likeProject(String id) { 
        return projectRepository.findById(id) 
                .map(project -> { 
                    project.setLikes(project.getLikes() + 1); 
                    return projectRepository.save(project); 
                }); 
    } 
 
    public List<Project> getTrendingProjects() { 
        // Example: top 10 most liked projects 
        return projectRepository.findTop10ByOrderByLikesDesc(); 
    } 
 
    // You'll need other methods for stats, e.g., getTotalProjects(), getTotalUsers() 
public long getTotalProjects() { 
return projectRepository.count(); 
} 
public long getTotalUsers() { 
// This count should come from your AppUser repository, not projects 
return 0; // Placeholder, implement in AppUserService or a dedicated StatService 
} 
public long getSoldProjects() { 
// This assumes a 'sold' flag or status in Project model 
return 0; // Placeholder 
} 
} 