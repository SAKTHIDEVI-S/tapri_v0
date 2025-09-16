package com.tapri.dto;

import com.tapri.entity.MessageReaction;
import java.time.LocalDateTime;

public class MessageReactionDto {
    private Long id;
    private UserDto user;
    private String emoji;
    private LocalDateTime createdAt;
    
    // Constructors
    public MessageReactionDto() {}
    
    public MessageReactionDto(MessageReaction reaction) {
        this.id = reaction.getId();
        this.user = new UserDto(reaction.getUser());
        this.emoji = reaction.getEmoji();
        this.createdAt = reaction.getCreatedAt();
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
    
    public String getEmoji() {
        return emoji;
    }
    
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
