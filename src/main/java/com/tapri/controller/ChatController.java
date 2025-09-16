package com.tapri.controller;

import com.tapri.dto.GroupMessageDto;
import com.tapri.dto.MessageReactionDto;
import com.tapri.entity.MediaType;
import com.tapri.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("userId");
        if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        }
        throw new RuntimeException("User not authenticated");
    }
    
    // Get group messages
    @GetMapping("/groups/{groupId}/messages")
    public ResponseEntity<List<GroupMessageDto>> getGroupMessages(
            @PathVariable Long groupId, 
            HttpServletRequest request) {
        
        try {
            Long userId = getUserIdFromRequest(request);
            List<GroupMessageDto> messages = chatService.getGroupMessages(groupId, userId);
            return ResponseEntity.ok(messages);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Send message to group
    @PostMapping("/groups/{groupId}/send")
    public ResponseEntity<GroupMessageDto> sendMessage(
            @PathVariable Long groupId,
            @RequestBody SendMessageRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            GroupMessageDto message = chatService.sendMessage(
                    groupId, 
                    userId, 
                    request.getContent(), 
                    request.getMediaUrl(), 
                    request.getMediaType()
            );
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Add reaction to message
    @PostMapping("/messages/{messageId}/reaction")
    public ResponseEntity<MessageReactionDto> addReaction(
            @PathVariable Long messageId,
            @RequestBody AddReactionRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            MessageReactionDto reaction = chatService.addReaction(messageId, userId, request.getEmoji());
            return ResponseEntity.ok(reaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Remove reaction from message
    @DeleteMapping("/messages/{messageId}/reaction")
    public ResponseEntity<Void> removeReaction(@PathVariable Long messageId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            chatService.removeReaction(messageId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Edit message
    @PutMapping("/messages/{messageId}")
    public ResponseEntity<GroupMessageDto> editMessage(
            @PathVariable Long messageId,
            @RequestBody EditMessageRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            GroupMessageDto message = chatService.editMessage(messageId, userId, request.getContent());
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Delete message
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long messageId, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            chatService.deleteMessage(messageId, userId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Send typing indicator
    @PostMapping("/groups/{groupId}/typing")
    public ResponseEntity<Void> sendTypingIndicator(
            @PathVariable Long groupId,
            @RequestBody TypingIndicatorRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            chatService.sendTypingIndicator(groupId, userId, request.isTyping());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    // Request DTOs
    public static class SendMessageRequest {
        private String content;
        private String mediaUrl;
        private MediaType mediaType;
        
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
        
        public MediaType getMediaType() {
            return mediaType;
        }
        
        public void setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
        }
    }
    
    public static class AddReactionRequest {
        private String emoji;
        
        // Getters and Setters
        public String getEmoji() {
            return emoji;
        }
        
        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }
    }
    
    public static class EditMessageRequest {
        private String content;
        
        // Getters and Setters
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
    }
    
    public static class TypingIndicatorRequest {
        private boolean isTyping;
        
        // Getters and Setters
        public boolean isTyping() {
            return isTyping;
        }
        
        public void setTyping(boolean typing) {
            isTyping = typing;
        }
    }
}
