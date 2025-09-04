package com.tapri.controller;

import com.tapri.entity.User;
import com.tapri.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @GetMapping("/profile/mobile/{mobile}")
    public ResponseEntity<?> getUserProfileByMobile(@PathVariable String mobile) {
        Optional<User> userOpt = userService.getUserByMobile(mobile);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        User updated = userService.updateProfile(userId, updatedUser);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    @PostMapping("/profile-picture/{userId}")
    public ResponseEntity<?> uploadProfilePicture(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.uploadProfilePicture(userId, file);
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Profile picture uploaded successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    @GetMapping("/profile-picture/{filename}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String filename) {
        try {
            byte[] imageBytes = userService.getProfilePicture(filename);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok().headers(headers).body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/login/{userId}")
    public ResponseEntity<?> updateLastLogin(@PathVariable Long userId) {
        userService.updateLastLogin(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Last login updated");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/rating/{userId}")
    public ResponseEntity<?> updateRating(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double rating = request.get("rating");
        if (rating != null) {
            userService.updateRating(userId, rating);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Rating updated");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rating is required");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/earnings/{userId}")
    public ResponseEntity<?> updateEarnings(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double earnings = request.get("earnings");
        if (earnings != null) {
            userService.updateEarnings(userId, earnings);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Earnings updated");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Earnings amount is required");
            return ResponseEntity.badRequest().body(error);
        }
    }
}