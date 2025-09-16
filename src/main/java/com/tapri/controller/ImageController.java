package com.tapri.controller;

import com.tapri.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*")
public class ImageController {
    
    @Autowired
    private ImageUploadService imageUploadService;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "posts") String folder) {
        
        Map<String, String> response = new HashMap<>();
        System.out.println("ImageController: Received upload request for folder: " + folder);
        System.out.println("ImageController: File name: " + (file != null ? file.getOriginalFilename() : "null"));
        System.out.println("ImageController: File size: " + (file != null ? file.getSize() : "null"));
        System.out.println("ImageController: File content type: " + (file != null ? file.getContentType() : "null"));
        
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                response.put("error", "File must be an image or video");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Upload image
            String imageUrl = imageUploadService.uploadImage(file, folder);
            System.out.println("ImageController: Upload successful, URL: " + imageUrl);
            
            response.put("success", "true");
            response.put("imageUrl", imageUrl);
            response.put("message", "Image uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            response.put("error", "Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{folder}/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String folder, @PathVariable String filename) {
        try {
            String imagePath = folder + "/" + filename;
            System.out.println("ImageController: Requested image path: " + imagePath);
            byte[] imageBytes = imageUploadService.getImage(imagePath);
            System.out.println("ImageController: Image loaded successfully, size: " + imageBytes.length + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            
            // Determine content type based on file extension
            String contentType = "image/jpeg"; // default
            String lowerFilename = filename.toLowerCase();
            
            if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                contentType = "image/webp";
            } else if (lowerFilename.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (lowerFilename.endsWith(".webm")) {
                contentType = "video/webm";
            } else if (lowerFilename.endsWith(".avi")) {
                contentType = "video/avi";
            } else if (lowerFilename.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (lowerFilename.endsWith(".wmv")) {
                contentType = "video/x-ms-wmv";
            } else if (lowerFilename.endsWith(".flv")) {
                contentType = "video/x-flv";
            } else if (lowerFilename.endsWith(".mkv")) {
                contentType = "video/x-matroska";
            }
            
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(imageBytes.length);
            
            // Add headers for better video streaming
            if (contentType.startsWith("video/")) {
                headers.set("Accept-Ranges", "bytes");
                headers.set("Cache-Control", "public, max-age=31536000"); // Cache for 1 year
                headers.set("Content-Disposition", "inline; filename=\"" + filename + "\"");
            }
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    // Streaming endpoint for videos (better for large files)
    @GetMapping("/stream/{folder}/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String folder, @PathVariable String filename) {
        try {
            String imagePath = folder + "/" + filename;
            System.out.println("ImageController: Streaming video path: " + imagePath);
            
            // Get the file from the service
            File videoFile = imageUploadService.getFile(imagePath);
            if (videoFile == null || !videoFile.exists()) {
                System.out.println("ImageController: Video file not found: " + imagePath);
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(videoFile);
            System.out.println("ImageController: Video file found, size: " + videoFile.length() + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            
            // Determine content type based on file extension
            String contentType = "video/mp4"; // default for videos
            String lowerFilename = filename.toLowerCase();
            
            if (lowerFilename.endsWith(".mp4")) {
                contentType = "video/mp4";
            } else if (lowerFilename.endsWith(".webm")) {
                contentType = "video/webm";
            } else if (lowerFilename.endsWith(".avi")) {
                contentType = "video/avi";
            } else if (lowerFilename.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (lowerFilename.endsWith(".wmv")) {
                contentType = "video/x-ms-wmv";
            } else if (lowerFilename.endsWith(".flv")) {
                contentType = "video/x-flv";
            } else if (lowerFilename.endsWith(".mkv")) {
                contentType = "video/x-matroska";
            }
            
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(videoFile.length());
            headers.set("Accept-Ranges", "bytes");
            headers.set("Cache-Control", "public, max-age=31536000"); // Cache for 1 year
            headers.set("Content-Disposition", "inline; filename=\"" + filename + "\"");
            
            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Error streaming video: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
