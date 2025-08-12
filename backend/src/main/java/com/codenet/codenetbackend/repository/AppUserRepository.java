package com.codenet.codenetbackend.repository;

import com.codenet.codenetbackend.model.AppUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByUsername(String username);
    List<AppUser> findByIsActive(boolean isActive);
}
