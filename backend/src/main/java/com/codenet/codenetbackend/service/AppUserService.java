package com.codenet.codenetbackend.service;

import com.codenet.codenetbackend.model.AppUser;
import com.codenet.codenetbackend.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.Instant;
import java.util.List;
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
<<<<<<< HEAD
    
    // Additional missing methods
    public List<AppUser> getAllActiveUsers() {
        return appUserRepository.findByIsActive(true);
    }
    
    public void deactivateUser(String id) {
        Optional<AppUser> userOpt = appUserRepository.findById(id);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            user.setActive(false);
            appUserRepository.save(user);
        }
    }
    
    public long getTotalActiveUsers() {
        return appUserRepository.findByIsActive(true).size();
=======
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
>>>>>>> 11e31b094d35e41ab12c0ca0f6c664d80fe3b4b6
    }
}
