package com.tapri.dto;

import com.tapri.entity.MediaType;
import com.tapri.entity.Post;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    private Long id;
    private UserDto user;
    private String text;
    private String mediaUrl;
    private MediaType mediaType;
    private int likesCount;
    private int commentsCount;
    private int shareCount;
    private boolean isLiked;
    private boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PostCommentDto> comments;
    
    // Constructors
    public PostDto() {}
    
    public PostDto(Post post, boolean isLiked) {
        this.id = post.getId();
        this.user = new UserDto(post.getUser());
        this.text = post.getText();
        this.mediaUrl = post.getMediaUrl();
        this.mediaType = post.getMediaType();
        this.likesCount = post.getLikesCount();
        this.commentsCount = post.getCommentsCount();
        this.shareCount = post.getShareCount() != null ? post.getShareCount() : 0;
        this.isLiked = isLiked;
        this.isSaved = false; // Will be set separately
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
    
    public PostDto(Post post, boolean isLiked, boolean isSaved) {
        this.id = post.getId();
        this.user = new UserDto(post.getUser());
        this.text = post.getText();
        this.mediaUrl = post.getMediaUrl();
        this.mediaType = post.getMediaType();
        this.likesCount = post.getLikesCount();
        this.commentsCount = post.getCommentsCount();
        this.shareCount = post.getShareCount() != null ? post.getShareCount() : 0;
        this.isLiked = isLiked;
        this.isSaved = isSaved;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
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
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
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
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public int getCommentsCount() {
        return commentsCount;
    }
    
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    
    public int getShareCount() {
        return shareCount;
    }
    
    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }
    
    public boolean isLiked() {
        return isLiked;
    }
    
    public void setLiked(boolean liked) {
        isLiked = liked;
    }
    
    public boolean isSaved() {
        return isSaved;
    }
    
    public void setSaved(boolean saved) {
        isSaved = saved;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<PostCommentDto> getComments() {
        return comments;
    }
    
    public void setComments(List<PostCommentDto> comments) {
        this.comments = comments;
    }
}
