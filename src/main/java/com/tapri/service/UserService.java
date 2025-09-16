package com.tapri.service;

import com.tapri.entity.User;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    private static final String UPLOAD_DIR = "uploads/profile-pictures/";
    
    // Basic CRUD methods
    public Optional<User> findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }
    
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Methods required by UserController
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByMobile(String mobile) {
        return userRepository.findByPhone(mobile);
    }
    
    public User updateProfile(Long userId, User updatedUser) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
            }
            if (updatedUser.getCity() != null) {
                user.setCity(updatedUser.getCity());
            }
            return userRepository.save(user);
        }
        return null;
    }
    
    public String uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = userId + "_" + System.currentTimeMillis() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // Update user with image URL
        User user = userOpt.get();
        user.setProfilePicture(filename);
        userRepository.save(user);
        
        return filename;
    }
    
    public byte[] getProfilePicture(String filename) throws IOException {
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        return Files.readAllBytes(filePath);
    }
    
    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    public void updateRating(Long userId, Double rating) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRating(rating);
            userRepository.save(user);
        }
    }
    
    public void updateEarnings(Long userId, Double earnings) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEarnings(earnings);
            userRepository.save(user);
        }
    }
}
