package com.codenet.codenetbackend.repository;

import com.codenet.codenetbackend.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByOwnerId(String ownerId);
    List<Project> findByStatus(String status);
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Project> searchProjects(String searchTerm);
    List<Project> findTop10ByOrderByLikesDesc();
}
