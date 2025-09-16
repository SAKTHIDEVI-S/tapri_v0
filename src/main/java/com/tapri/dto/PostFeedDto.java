package com.tapri.dto;

import com.tapri.entity.MediaType;

import java.time.LocalDateTime;

public class PostFeedDto {
    private Long id;
    private String userName;
    private String userAvatar;
    private String postTime;
    private String caption;
    private String mediaUrl;
    private MediaType mediaType;
    private String postType;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Boolean isLiked;
    private Boolean isSaved;
    
    // Constructors
    public PostFeedDto() {}
    
    public PostFeedDto(Long id, String userName, String userAvatar, String postTime, 
                      String caption, String mediaUrl, MediaType mediaType, String postType,
                      Integer likeCount, Integer commentCount, Integer shareCount,
                      Boolean isLiked, Boolean isSaved) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.postTime = postTime;
        this.caption = caption;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.postType = postType;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.isLiked = isLiked;
        this.isSaved = isSaved;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public String getPostTime() {
        return postTime;
    }
    
    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
    
    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
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
    
    public String getPostType() {
        return postType;
    }
    
    public void setPostType(String postType) {
        this.postType = postType;
    }
    
    public Integer getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
    
    public Integer getShareCount() {
        return shareCount;
    }
    
    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }
    
    public Boolean getIsLiked() {
        return isLiked;
    }
    
    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }
    
    public Boolean getIsSaved() {
        return isSaved;
    }
    
    public void setIsSaved(Boolean isSaved) {
        this.isSaved = isSaved;
    }
}
