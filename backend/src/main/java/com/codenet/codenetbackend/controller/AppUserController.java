package com.codenet.codenetbackend.controller;

import com.codenet.codenetbackend.dto.ClerkUserSyncDTO;
import com.codenet.codenetbackend.model.AppUser;
import com.codenet.codenetbackend.service.AppUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class AppUserController {
    private static final Logger log = LoggerFactory.getLogger(AppUserController.class);
    private final AppUserService appUserService;
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/sync")
    public ResponseEntity<AppUser> syncClerkUser(@RequestBody ClerkUserSyncDTO userSyncDTO) {
        log.info("Received user sync request for Clerk ID: {}", userSyncDTO.getId());
        return appUserService.getUserById(userSyncDTO.getId())
            .map(existingUser -> {
                existingUser.setEmail(userSyncDTO.getEmail());
                existingUser.setUsername(userSyncDTO.getUsername());
                log.info("Updating existing user: {}", existingUser.getId());
                return ResponseEntity.ok(appUserService.updateUser(existingUser));
            })
            .orElseGet(() -> {
                log.info("Creating new user with Clerk ID: {}", userSyncDTO.getId());
                AppUser newUser = appUserService.createUser(userSyncDTO.getId(), userSyncDTO.getEmail(), userSyncDTO.getUsername());
                return ResponseEntity.ok(newUser);
            });
    }

    @DeleteMapping("/sync/{clerkUserId}")
    public ResponseEntity<Void> deleteClerkUser(@PathVariable String clerkUserId) {
        log.info("Received user delete request for Clerk ID: {}", clerkUserId);
        appUserService.deleteUser(clerkUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AppUser> getCurrentUserDetails(@RequestAttribute("clerkUserId") String clerkUserId) {
        return appUserService.getUserById(clerkUserId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
