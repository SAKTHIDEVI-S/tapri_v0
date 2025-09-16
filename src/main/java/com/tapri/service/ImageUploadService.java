package com.tapri.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageUploadService {
    
    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;
    
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir + folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the URL path (relative to the server) - use the correct API endpoint
        return "/api/images/" + folder + "/" + filename;
    }
    
    public byte[] getImage(String imagePath) throws IOException {
        // Handle both old and new URL formats
        String cleanPath = imagePath.replace("/api/images/", "").replace("/uploads/", "");
        Path filePath = Paths.get(uploadDir + cleanPath);
        System.out.println("ImageUploadService: Looking for image at: " + filePath.toString());
        System.out.println("ImageUploadService: File exists: " + Files.exists(filePath));
        return Files.readAllBytes(filePath);
    }
    
    public boolean deleteImage(String imagePath) {
        try {
            // Handle both old and new URL formats
            String cleanPath = imagePath.replace("/api/images/", "").replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir + cleanPath);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    public File getFile(String imagePath) {
        try {
            // Handle both old and new URL formats
            String cleanPath = imagePath.replace("/api/images/", "").replace("/uploads/", "");
            Path filePath = Paths.get(uploadDir + cleanPath);
            File file = filePath.toFile();
            System.out.println("ImageUploadService: Looking for file at: " + filePath.toString());
            System.out.println("ImageUploadService: File exists: " + file.exists());
            return file.exists() ? file : null;
        } catch (Exception e) {
            System.err.println("Error getting file: " + e.getMessage());
            return null;
        }
    }
}
