package com.tapri.dto;

import com.tapri.entity.User;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String phoneNumber;
    private String name;
    private String city;
    private String state;
    private String bio;
    private String profilePhotoUrl;
    private String profilePicture;
    private LocalDateTime lastSeen;
    private LocalDateTime lastLogin;
    private Boolean lastSeenVisible;
    private Double rating;
    private Double earnings;
    private LocalDateTime createdAt;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(User user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.name = user.getName();
        this.city = user.getCity();
        this.state = user.getState();
        this.bio = user.getBio();
        this.profilePhotoUrl = user.getProfilePhotoUrl();
        this.profilePicture = user.getProfilePicture();
        this.lastSeen = user.getLastSeen();
        this.lastLogin = user.getLastLogin();
        this.lastSeenVisible = user.getLastSeenVisible();
        this.rating = user.getRating();
        this.earnings = user.getEarnings();
        this.createdAt = user.getCreatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }
    
    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }
    
    public String getProfilePicture() {
        return profilePicture;
    }
    
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
    
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public Boolean getLastSeenVisible() {
        return lastSeenVisible;
    }
    
    public void setLastSeenVisible(Boolean lastSeenVisible) {
        this.lastSeenVisible = lastSeenVisible;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Double getEarnings() {
        return earnings;
    }
    
    public void setEarnings(Double earnings) {
        this.earnings = earnings;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
