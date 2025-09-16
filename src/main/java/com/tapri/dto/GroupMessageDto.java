package com.tapri.dto;

import com.tapri.entity.GroupMessage;
import java.util.List;

public class GroupMessageDto {
    private Long id;
    private UserDto user;
    private String content;
    private String mediaUrl;
    private String mediaType;
    private boolean isEdited;
    private String createdAt;
    private String updatedAt;
    private List<MessageReactionDto> reactions;
    
    // Constructors
    public GroupMessageDto() {}
    
    public GroupMessageDto(GroupMessage message) {
        this.id = message.getId();
        this.user = new UserDto(message.getUser());
        this.content = message.getContent();
        this.mediaUrl = message.getMediaUrl();
        this.mediaType = message.getMediaType() != null ? message.getMediaType().toString() : null;
        this.isEdited = message.getIsEdited();
        this.createdAt = message.getCreatedAt() != null ? message.getCreatedAt().toString() : null;
        this.updatedAt = message.getUpdatedAt() != null ? message.getUpdatedAt().toString() : null;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserDto getUser() {
        return user;
    }
    
    public void setUser(UserDto user) {
        this.user = user;
    }
    
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
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public void setEdited(boolean edited) {
        isEdited = edited;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<MessageReactionDto> getReactions() {
        return reactions;
    }
    
    public void setReactions(List<MessageReactionDto> reactions) {
        this.reactions = reactions;
    }
}