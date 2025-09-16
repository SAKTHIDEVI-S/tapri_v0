package com.tapri.dto;

import com.tapri.entity.GroupMember;
import com.tapri.entity.MemberRole;
import java.time.LocalDateTime;

public class GroupMemberDto {
    private Long id;
    private UserDto user;
    private MemberRole role;
    private LocalDateTime joinedAt;
    
    // Constructors
    public GroupMemberDto() {}
    
    public GroupMemberDto(GroupMember member) {
        this.id = member.getId();
        this.user = new UserDto(member.getUser());
        this.role = member.getRole();
        this.joinedAt = member.getJoinedAt();
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
    
    public MemberRole getRole() {
        return role;
    }
    
    public void setRole(MemberRole role) {
        this.role = role;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
