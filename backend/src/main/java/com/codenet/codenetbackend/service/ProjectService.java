





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

    public List<Project> getProjectsByStatus(String status) {
        return projectRepository.findByStatus(status);
    }

    public Optional<Project> approveProject(String id) {
        return projectRepository.findById(id).map(project -> {
            project.setStatus("APPROVED");
            return projectRepository.save(project);
        });
    }

    public Optional<Project> rejectProject(String id, String reason) {
        return projectRepository.findById(id).map(project -> {
            project.setStatus("REJECTED");
            // Optionally set rejection reason if field exists
            return projectRepository.save(project);
        });
    }

    public Optional<Project> featureProject(String id) {
        return projectRepository.findById(id).map(project -> {
            project.setFeatured(true);
            return projectRepository.save(project);
        });
    }

    public long getTotalApprovedProjects() {
        return getProjectsByStatus("APPROVED").size();
    }

    public long getTotalPendingProjects() {
        return getProjectsByStatus("PENDING").size();
    }

    public long getTotalLikes() {
        return projectRepository.findAll().stream().mapToLong(Project::getLikes).sum();
    }

    public long getTotalViews() {
        return projectRepository.findAll().stream().mapToLong(Project::getViews).sum();
    }

    public long getTotalCollaborators() {
        // Placeholder: implement if you have collaborators
        return 0;
    }

    public long getSoldProjectsCount() {
        // Placeholder: implement if you have sold projects
        return 0;
    }

    public long getUsersCount() {
        // Placeholder: implement if you have users
        return 0;
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

    public long getTotalProjects() {
        return projectRepository.count();
    }
}

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;


            return projectRepository.save(project);
            return projectRepository.save(project);
            return projectRepository.save(project);



    }
    }
    }
    }
        project.setStatus("PENDING"); // New projects are pending approval
    import com.codenet.codenetbackend.model.Project;
<<<<<<< HEAD
        return projectRepository.save(project);
                    project.setSubtitle(updatedProject.getSubtitle());
                    // project.setStatus(updatedProject.getStatus());
<<<<<<< HEAD
        projectRepository.deleteById(id);
            return true;
<<<<<<< HEAD
        return projectRepository.save(project);
        return projectRepository.findById(id)
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
<<<<<<< HEAD
    
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
=======
}
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
