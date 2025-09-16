package com.tapri.dto;


public class CreatePostRequest {
    private String text;
    private String mediaUrl;
    private String mediaType;
    private String postType;
    private String audience;
    
    // Constructors
    public CreatePostRequest() {}
    
    public CreatePostRequest(String text, String mediaUrl, String mediaType, String postType, String audience) {
        this.text = text;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.postType = postType;
        this.audience = audience;
    }
    
    // Getters and Setters
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
    
    public String getMediaType() {
        return mediaType;
    }
    
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    public String getPostType() {
        return postType;
    }
    
    public void setPostType(String postType) {
        this.postType = postType;
    }
    
    public String getAudience() {
        return audience;
    }
    
    public void setAudience(String audience) {
        this.audience = audience;
    }
    
    @Override
    public String toString() {
        return "CreatePostRequest{" +
                "text='" + text + '\'' +
                ", mediaUrl='" + mediaUrl + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", postType='" + postType + '\'' +
                ", audience='" + audience + '\'' +
                '}';
    }
}
