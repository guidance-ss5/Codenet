package com.codenet.codenetbackend.repository;

import com.codenet.codenetbackend.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByClerkUserId(String clerkUserId);
    Optional<AppUser> findByEmail(String email);
    boolean existsByClerkUserId(String clerkUserId);
    boolean existsByEmail(String email);
}
