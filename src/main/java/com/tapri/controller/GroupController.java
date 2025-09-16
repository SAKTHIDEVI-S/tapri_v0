package com.tapri.controller;

import com.tapri.dto.GroupDto;
import com.tapri.dto.GroupMemberDto;
import com.tapri.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {
    
    @Autowired
    private GroupService groupService;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("User not authenticated");
    }
    
    // Get groups user is a member of
    @GetMapping("/my")
    public ResponseEntity<List<GroupDto>> getGroups(HttpServletRequest request) {
        try {
            System.out.println("GroupController - getGroups called");
            System.out.println("GroupController - Request URL: " + request.getRequestURL());
            System.out.println("GroupController - Request method: " + request.getMethod());
            System.out.println("GroupController - Request attributes: " + request.getAttributeNames());
            System.out.println("GroupController - UserId attribute: " + request.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(request);
            System.out.println("GroupController - Extracted userId: " + userId);
            
            System.out.println("GroupController - Calling groupService.getGroupsByUser(" + userId + ")");
            List<GroupDto> groups = groupService.getGroupsByUser(userId);
            System.out.println("GroupController - Service returned " + groups.size() + " groups");
            System.out.println("GroupController - Groups data: " + groups);
            
            if (groups.isEmpty()) {
                System.out.println("GroupController - No groups found, returning empty list");
                return ResponseEntity.ok(new ArrayList<>());
            }
            
            System.out.println("GroupController - Returning " + groups.size() + " groups");
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            System.out.println("GroupController - Exception in getGroups: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
    
    // Create new group
    @PostMapping("/create")
    public ResponseEntity<GroupDto> createGroup(
            @RequestBody CreateGroupRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            System.out.println("GroupController - createGroup called");
            System.out.println("GroupController - Request: " + request);
            System.out.println("GroupController - Request attributes: " + httpRequest.getAttributeNames());
            System.out.println("GroupController - UserId attribute: " + httpRequest.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(httpRequest);
            System.out.println("GroupController - Extracted userId: " + userId);
            
            GroupDto group = groupService.createGroup(
                    userId, 
                    request.getName(), 
                    request.getDescription(), 
                    request.getPhotoUrl(),
                    request.getCategory()
            );
            System.out.println("GroupController - Created group: " + group);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            System.out.println("GroupController - Error creating group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Join group
    @PostMapping("/{id}/join")
    public ResponseEntity<GroupDto> joinGroup(@PathVariable Long id, HttpServletRequest request) {
        try {
            System.out.println("GroupController - joinGroup called for groupId: " + id);
            System.out.println("GroupController - Request attributes: " + request.getAttributeNames());
            System.out.println("GroupController - UserId attribute: " + request.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(request);
            System.out.println("GroupController - Extracted userId: " + userId);
            
            GroupDto group = groupService.joinGroup(id, userId);
            System.out.println("GroupController - Successfully joined group: " + group.getName() + " (ID: " + group.getId() + ")");
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            System.out.println("GroupController - Error joining group: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Leave group
    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            groupService.leaveGroup(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get group members
    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberDto>> getGroupMembers(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<GroupMemberDto> members = groupService.getGroupMembers(id, userId);
            return ResponseEntity.ok(members);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update group info
    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(
            @PathVariable Long id,
            @RequestBody UpdateGroupRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            GroupDto group = groupService.updateGroup(
                    id, 
                    userId, 
                    request.getName(), 
                    request.getDescription(), 
                    request.getPhotoUrl()
            );
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Delete group
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            groupService.deleteGroup(id, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Explore groups (groups user is not a member of)
    @GetMapping("/explore")
    public ResponseEntity<List<GroupDto>> exploreGroups(HttpServletRequest request) {
        try {
            System.out.println("GroupController - exploreGroups called");
            System.out.println("GroupController - Request attributes: " + request.getAttributeNames());
            System.out.println("GroupController - UserId attribute: " + request.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(request);
            System.out.println("GroupController - Extracted userId: " + userId);
            
            List<GroupDto> groups = groupService.exploreGroups(userId);
            System.out.println("GroupController - Found " + groups.size() + " explore groups for user " + userId);
            System.out.println("GroupController - Explore groups data: " + groups);
            
            return ResponseEntity.ok(groups);
        } catch (RuntimeException e) {
            System.out.println("GroupController - Error in exploreGroups: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
    
    // Test endpoint to verify authentication
    @GetMapping("/test")
    public ResponseEntity<String> testAuth(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            return ResponseEntity.ok("Authentication working! User ID: " + userId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }
    
    // Request DTOs
    public static class CreateGroupRequest {
        private String name;
        private String description;
        private String photoUrl;
        private String category;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getPhotoUrl() {
            return photoUrl;
        }
        
        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
    
    public static class UpdateGroupRequest {
        private String name;
        private String description;
        private String photoUrl;
        
        // Getters and Setters
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getPhotoUrl() {
            return photoUrl;
        }
        
        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }
}
