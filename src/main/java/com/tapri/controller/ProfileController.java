package com.tapri.controller;

import com.tapri.dto.UserDto;
import com.tapri.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {
    
    @Autowired
    private ProfileService profileService;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("User not authenticated");
    }
    
    // Get user profile
    @GetMapping
    public ResponseEntity<UserDto> getUserProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            UserDto profile = profileService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Update user profile
    @PutMapping
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            UserDto profile = profileService.updateUserProfile(
                    userId, 
                    request.getName(), 
                    request.getBio(), 
                    request.getProfilePhotoUrl(), 
                    request.getLastSeenVisible()
            );
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update last seen
    @PostMapping("/last-seen")
    public ResponseEntity<Void> updateLastSeen(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            profileService.updateLastSeen(userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> searchUsers(@RequestParam String query) {
        List<UserDto> users = profileService.searchUsers(query);
        return ResponseEntity.ok(users);
    }
    
    // Get users with visible last seen
    @GetMapping("/online")
    public ResponseEntity<List<UserDto>> getOnlineUsers() {
        List<UserDto> users = profileService.getUsersWithVisibleLastSeen();
        return ResponseEntity.ok(users);
    }
    
    // Request DTO
    public static class UpdateProfileRequest {
        private String name;
        private String bio;
        private String profilePhotoUrl;
        private Boolean lastSeenVisible;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getBio() {
            return bio;
        }
        
        public void setBio(String bio) {
            this.bio = bio;
        }
        
        public String getProfilePhotoUrl() {
            return profilePhotoUrl;
        }
        
        public void setProfilePhotoUrl(String profilePhotoUrl) {
            this.profilePhotoUrl = profilePhotoUrl;
        }
        
        public Boolean getLastSeenVisible() {
            return lastSeenVisible;
        }
        
        public void setLastSeenVisible(Boolean lastSeenVisible) {
            this.lastSeenVisible = lastSeenVisible;
        }
    }
}