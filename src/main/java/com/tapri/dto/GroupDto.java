package com.tapri.dto;

import com.tapri.entity.Group;
import com.tapri.entity.MemberRole;
import java.time.LocalDateTime;
import java.util.List;

public class GroupDto {
    private Long id;
    private String name;
    private String description;
    private String photoUrl;
    private UserDto createdBy;
    private int membersCount;
    private MemberRole userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupMemberDto> members;
    
    // Additional fields for frontend
    private String lastMessage;
    private String lastMessageTime;
    private Integer unreadCount;
    private String category;
    private Boolean isJoined;
    
    // Constructors
    public GroupDto() {}
    
    public GroupDto(Group group, MemberRole userRole) {
        this.id = group.getId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.photoUrl = group.getPhotoUrl();
        this.createdBy = new UserDto(group.getCreatedBy());
        this.membersCount = group.getMembersCount();
        this.userRole = userRole;
        this.createdAt = group.getCreatedAt();
        this.updatedAt = group.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public UserDto getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }
    
    public int getMembersCount() {
        return membersCount;
    }
    
    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }
    
    public MemberRole getUserRole() {
        return userRole;
    }
    
    public void setUserRole(MemberRole userRole) {
        this.userRole = userRole;
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
    
    public List<GroupMemberDto> getMembers() {
        return members;
    }
    
    public void setMembers(List<GroupMemberDto> members) {
        this.members = members;
    }
    
    public String getLastMessage() {
        return lastMessage;
    }
    
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
    
    public String getLastMessageTime() {
        return lastMessageTime;
    }
    
    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    
    public Integer getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(Integer unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Boolean getIsJoined() {
        return isJoined;
    }
    
    public void setIsJoined(Boolean isJoined) {
        this.isJoined = isJoined;
    }
}