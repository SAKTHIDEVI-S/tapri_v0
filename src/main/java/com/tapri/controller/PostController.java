package com.tapri.controller;

import com.tapri.dto.PostCommentDto;
import com.tapri.dto.PostDto;
import com.tapri.dto.PostFeedDto;
import com.tapri.dto.CreatePostRequest;
import com.tapri.entity.MediaType;
import com.tapri.service.PostService;
import com.tapri.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private ImageUploadService imageUploadService;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("User not authenticated");
    }
    
    // Get all posts with pagination
    @GetMapping
    public ResponseEntity<Page<PostDto>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<PostDto> posts = postService.getAllPosts(pageable, userId);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get all posts without pagination
    @GetMapping("/all")
    public ResponseEntity<List<PostDto>> getAllPosts(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<PostDto> posts = postService.getAllPosts(userId);
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get posts for feed (frontend format)
    @GetMapping("/feed")
    public ResponseEntity<List<PostFeedDto>> getPostsForFeed(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<PostFeedDto> posts = postService.getPostsForFeed(userId);
        return ResponseEntity.ok(posts);
    }
    
    // Get post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            PostDto post = postService.getPostById(id, userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Create new post
    @PostMapping("/create")
    public ResponseEntity<PostDto> createPost(
            @RequestBody CreatePostRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            
            // Log the request for debugging
            System.out.println("Creating post for user: " + userId);
            System.out.println("Post text: " + request.getText());
            System.out.println("Media URL: " + request.getMediaUrl());
            System.out.println("Media Type: " + request.getMediaType());
            System.out.println("Post Type: " + request.getPostType());
            System.out.println("Audience: " + request.getAudience());
            System.out.println("Request object: " + request.toString());
            
            PostDto post = postService.createPost(userId, request);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            System.err.println("Error creating post: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Create new post with image upload
    @PostMapping("/with-image")
    public ResponseEntity<Map<String, Object>> createPostWithImage(
            @RequestParam("text") String text,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromRequest(request);
            String mediaUrl = null;
            MediaType mediaType = MediaType.IMAGE;
            
            // Upload image if provided
            if (image != null && !image.isEmpty()) {
                mediaUrl = imageUploadService.uploadImage(image, "posts");
            }
            
            // Create post
            PostDto post = postService.createPost(userId, text, mediaUrl, mediaType);
            
            response.put("success", true);
            response.put("post", post);
            response.put("message", "Post created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", "Failed to create post: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Like/Unlike post
    @PostMapping("/{id}/like")
    public ResponseEntity<PostDto> toggleLike(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            PostDto post = postService.toggleLike(id, userId);
            return ResponseEntity.ok(post);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Add comment to post
    @PostMapping("/{id}/comment")
    public ResponseEntity<PostCommentDto> addComment(
            @PathVariable Long id,
            @RequestBody AddCommentRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            PostCommentDto comment = postService.addComment(id, userId, request.getContent());
            return ResponseEntity.ok(comment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get comments for a post with pagination
    @GetMapping("/{id}/comments")
    public ResponseEntity<Map<String, Object>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            Map<String, Object> response = postService.getComments(id, page, size, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Delete post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            postService.deletePost(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Delete comment
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            postService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    // Toggle save/unsave post
    @PostMapping("/{id}/save")
    public ResponseEntity<Map<String, Object>> toggleSavePost(
            @PathVariable Long id, 
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Long userId = getUserIdFromRequest(request);
            boolean isSaved = postService.toggleSavePost(id, userId);
            response.put("success", true);
            response.put("isSaved", isSaved);
            response.put("message", isSaved ? "Post saved" : "Post unsaved");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Share post
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> sharePost(
            @PathVariable Long id, 
            HttpServletRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromRequest(request);
            int newShareCount = postService.sharePost(id, userId);
            response.put("success", true);
            response.put("shareCount", newShareCount);
            response.put("message", "Post shared successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Get saved posts for current user
    @GetMapping("/me/saved")
    public ResponseEntity<List<PostDto>> getSavedPosts(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<PostDto> savedPosts = postService.getSavedPosts(userId);
            return ResponseEntity.ok(savedPosts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get posts by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Long currentUserId = getUserIdFromRequest(request);
            List<PostDto> userPosts = postService.getPostsByUser(userId, currentUserId);
            return ResponseEntity.ok(userPosts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Share post to group
    @PostMapping("/{id}/share-to-group")
    public ResponseEntity<Map<String, Object>> sharePostToGroup(
            @PathVariable Long id,
            @RequestBody ShareToGroupRequest request,
            HttpServletRequest httpRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            boolean success = postService.sharePostToGroup(id, request.getGroupId(), userId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Post shared to group successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Failed to share post to group");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    public static class AddCommentRequest {
        private String content;
        
        // Getters and Setters
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class ShareToGroupRequest {
        private Long groupId;
        
        // Getters and Setters
        public Long getGroupId() {
            return groupId;
        }
        
        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }
    }
}
