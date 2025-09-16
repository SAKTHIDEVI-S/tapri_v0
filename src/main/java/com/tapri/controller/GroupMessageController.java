package com.tapri.controller;

import com.tapri.dto.GroupMessageDto;
import com.tapri.service.GroupMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupMessageController {
    
    @Autowired
    private GroupMessageService groupMessageService;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("User not authenticated");
    }
    
    // Get group messages
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessageService.GroupMessagesResponse> getGroupMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {
        
        try {
            System.out.println("GroupMessageController - getGroupMessages called for groupId: " + groupId);
            System.out.println("GroupMessageController - Request attributes: " + request.getAttributeNames());
            System.out.println("GroupMessageController - UserId attribute: " + request.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(request);
            System.out.println("GroupMessageController - Extracted userId: " + userId);
            
            GroupMessageService.GroupMessagesResponse response = groupMessageService.getGroupMessages(groupId, userId, page, size);
            System.out.println("GroupMessageController - Found " + response.getMessages().size() + " messages for group " + groupId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.out.println("GroupMessageController - Error getting messages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
    
    // Send message to group
    @PostMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessageDto> sendGroupMessage(
            @PathVariable Long groupId,
            @RequestBody SendGroupMessageRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            System.out.println("GroupMessageController - sendGroupMessage called for groupId: " + groupId);
            System.out.println("GroupMessageController - Request attributes: " + httpRequest.getAttributeNames());
            System.out.println("GroupMessageController - UserId attribute: " + httpRequest.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(httpRequest);
            System.out.println("GroupMessageController - Extracted userId: " + userId);
            
            GroupMessageDto message = groupMessageService.sendGroupMessage(
                    groupId, 
                    userId, 
                    request.getContent(), 
                    request.getMediaUrl(), 
                    request.getMediaType()
            );
            System.out.println("GroupMessageController - Message sent successfully: " + message.getId());
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            System.out.println("GroupMessageController - Error sending message: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
    
    // Mark messages as read
    @PostMapping("/{groupId}/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long groupId,
            HttpServletRequest request) {
        
        try {
            System.out.println("GroupMessageController - markMessagesAsRead called for groupId: " + groupId);
            System.out.println("GroupMessageController - Request attributes: " + request.getAttributeNames());
            System.out.println("GroupMessageController - UserId attribute: " + request.getAttribute("userId"));
            
            Long userId = getUserIdFromRequest(request);
            System.out.println("GroupMessageController - Extracted userId: " + userId);
            
            groupMessageService.markMessagesAsRead(groupId, userId);
            System.out.println("GroupMessageController - Messages marked as read successfully");
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            System.out.println("GroupMessageController - Error marking messages as read: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }
    }
    
    // Request DTOs
    public static class SendGroupMessageRequest {
        private String content;
        private String mediaUrl;
        private String mediaType;
        
        // Getters and Setters
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public String getMediaUrl() {
            return mediaUrl;
        }
        
        public void setMediaUrl(String mediaUrl) {
            this.mediaUrl = mediaUrl;
        }
        
        public String getMediaType() {
            return mediaType;
        }
        
        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }
    }
    
}
