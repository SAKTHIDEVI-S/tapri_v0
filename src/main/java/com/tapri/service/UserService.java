package com.tapri.service;

import com.tapri.entity.User;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
    
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";
    
    public UserService() {
        // Create upload directory if it doesn't exist
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }
    
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }
    
    public Optional<User> getUserByMobile(String mobile) {
        return userRepo.findByMobile(mobile);
    }
    
    public User updateProfile(Long userId, User updatedUser) {
        Optional<User> existingUserOpt = userRepo.findById(userId);
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            
            // Update fields
            if (updatedUser.getName() != null) {
                existingUser.setName(updatedUser.getName());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getCity() != null) {
                existingUser.setCity(updatedUser.getCity());
            }
            if (updatedUser.getState() != null) {
                existingUser.setState(updatedUser.getState());
            }
            if (updatedUser.getVehicleType() != null) {
                existingUser.setVehicleType(updatedUser.getVehicleType());
            }
            if (updatedUser.getVehicleNumber() != null) {
                existingUser.setVehicleNumber(updatedUser.getVehicleNumber());
            }
            
            return userRepo.save(existingUser);
        }
        return null;
    }
    
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.copy(file.getInputStream(), filePath);
        
        // Update user profile picture URL
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setProfilePictureUrl("/api/users/profile-picture/" + filename);
            userRepo.save(user);
        }
        
        return "/api/users/profile-picture/" + filename;
    }
    
    public byte[] getProfilePicture(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        return Files.readAllBytes(filePath);
    }
    
    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLast_login(new Timestamp(System.currentTimeMillis()));
            userRepo.save(user);
        }
    }
    
    public void updateRating(Long userId, Double rating) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRating(rating);
            userRepo.save(user);
        }
    }
    
    public void updateEarnings(Long userId, Double earnings) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTotalEarnings(user.getTotalEarnings() + earnings);
            userRepo.save(user);
        }
    }
}