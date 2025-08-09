package com.codenet.codenetbackend.service;

import com.codenet.codenetbackend.model.AppUser;
import com.codenet.codenetbackend.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.Instant;
import java.util.Optional;

@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser createUser(String clerkUserId, String email, String username) {
        AppUser newUser = new AppUser();
        newUser.setId(clerkUserId);
        newUser.setEmail(email);
        newUser.setUsername(username);
        newUser.setJoinedAt(Instant.now());
        newUser.setRole("Developer");
        return appUserRepository.save(newUser);
    }

    public Optional<AppUser> getUserById(String id) {
        return appUserRepository.findById(id);
    }

    public AppUser updateUser(AppUser user) {
        return appUserRepository.save(user);
    }

    public void deleteUser(String id) {
        appUserRepository.deleteById(id);
    }
    public List<AppUser> getAllActiveUsers() {
        // Placeholder: implement logic for active users
        return appUserRepository.findAll();
    }

    public void deactivateUser(String id) {
        // Placeholder: implement logic for deactivating user
    }

    public long getTotalActiveUsers() {
        // Placeholder: implement logic for counting active users
        return appUserRepository.count();
    }
}
