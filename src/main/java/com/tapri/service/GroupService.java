package com.tapri.service;

import com.tapri.dto.GroupDto;
import com.tapri.dto.GroupMemberDto;
import com.tapri.dto.UserDto;
import com.tapri.entity.*;
import com.tapri.repository.*;
import com.tapri.entity.GroupMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    @Autowired
    private GroupMessageReadRepository groupMessageReadRepository;
    
    // Get groups user is a member of
    public List<GroupDto> getGroupsByUser(Long userId) {
        System.out.println("GroupService - getGroupsByUser called for userId: " + userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("GroupService - Found user: " + user.getName() + " (ID: " + user.getId() + ")");
        
        // Debug: Check if there are any groups at all
        List<Group> allGroups = groupRepository.findByIsActiveTrue();
        System.out.println("GroupService - Total active groups in database: " + allGroups.size());
        
        // Debug: Check if there are any group members for this user
        List<GroupMember> userMemberships = groupMemberRepository.findByUser(user);
        System.out.println("GroupService - User memberships found: " + userMemberships.size());
        for (GroupMember gm : userMemberships) {
            System.out.println("GroupService - User is member of group: " + gm.getGroup().getName() + " (ID: " + gm.getGroup().getId() + ")");
        }
        
        // Use native SQL query directly as it's more reliable
        List<Group> groups = groupRepository.findGroupsByMemberNative(userId);
        System.out.println("GroupService - Found " + groups.size() + " groups for user " + userId);
        
        if (groups.isEmpty()) {
            System.out.println("GroupService - No groups found for user " + userId);
            return new ArrayList<>();
        }
        
        System.out.println("GroupService - Starting to process " + groups.size() + " groups");
        List<GroupDto> result = groups.stream().map(group -> {
            try {
                System.out.println("GroupService - Processing group: " + group.getName() + " (ID: " + group.getId() + ")");
                Optional<GroupMember> membership = groupMemberRepository.findByGroupAndUser(group, user);
                MemberRole userRole = membership.map(GroupMember::getRole).orElse(MemberRole.MEMBER);
                System.out.println("GroupService - User role in group: " + userRole);
                
                // Get actual members count from database
                long actualMembersCount = groupMemberRepository.countMembersByGroup(group);
                System.out.println("GroupService - Actual members count for group " + group.getName() + ": " + actualMembersCount);
                
                // Load the createdBy user separately to avoid LAZY loading issues
                User createdByUser = userRepository.findById(group.getCreatedBy().getId())
                    .orElseThrow(() -> new RuntimeException("CreatedBy user not found"));
                System.out.println("GroupService - Loaded createdBy user: " + createdByUser.getName());
                
                System.out.println("GroupService - Creating GroupDto for group: " + group.getName());
                GroupDto dto = new GroupDto(group, userRole);
                System.out.println("GroupService - GroupDto created successfully");
                
                dto.setMembersCount((int) actualMembersCount);
                System.out.println("GroupService - Set members count: " + actualMembersCount);
                
                dto.setCreatedBy(new UserDto(createdByUser));
                System.out.println("GroupService - Set createdBy user successfully");
                
                // Get last message
                List<GroupMessage> lastMessages = groupMessageRepository.findTop1ByGroupAndIsActiveTrueOrderByCreatedAtDesc(group.getId());
                if (!lastMessages.isEmpty()) {
                    GroupMessage lastMessage = lastMessages.get(0);
                    dto.setLastMessage(lastMessage.getContent());
                    dto.setLastMessageTime(formatTimeAgo(lastMessage.getCreatedAt()));
                }
                
                // Get unread count
                long unreadCount = groupMessageReadRepository.countUnreadMessagesByUserAndGroup(user, group);
                dto.setUnreadCount((int) unreadCount);
                
                // Set category
                dto.setCategory(group.getCategory());
                
                // Set isJoined to true since user is a member
                dto.setIsJoined(true);
                
                System.out.println("GroupService - Successfully created DTO for group: " + group.getName());
                return dto;
            } catch (Exception e) {
                System.out.println("GroupService - Error processing group " + group.getName() + ": " + e.getMessage());
                e.printStackTrace();
                // Return a basic DTO if there's an error
                GroupDto dto = new GroupDto(group, MemberRole.MEMBER);
                dto.setIsJoined(true);
                return dto;
            }
        }).collect(Collectors.toList());
        
        System.out.println("GroupService - Successfully processed " + result.size() + " groups");
        return result;
    }
    
    // Create new group
    public GroupDto createGroup(Long userId, String name, String description, String photoUrl, String category) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = new Group(name, description, user);
        if (photoUrl != null) {
            group.setPhotoUrl(photoUrl);
        }
        if (category != null) {
            group.setCategory(category);
        }
        group = groupRepository.save(group);
        
        // Add creator as admin
        GroupMember adminMember = new GroupMember(group, user, MemberRole.ADMIN);
        groupMemberRepository.save(adminMember);
        System.out.println("GroupService - Created GroupMember for user " + userId + " in group " + group.getId() + " with role ADMIN");
        
        return new GroupDto(group, MemberRole.ADMIN);
    }
    
    // Join group
    public GroupDto joinGroup(Long groupId, Long userId) {
        System.out.println("GroupService - joinGroup called for groupId: " + groupId + ", userId: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("GroupService - Found user: " + user.getName() + " (ID: " + user.getId() + ")");
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        System.out.println("GroupService - Found group: " + group.getName() + " (ID: " + group.getId() + ")");
        
        if (!group.getIsActive()) {
            System.out.println("GroupService - Group is not active");
            throw new RuntimeException("Group not found");
        }
        
        // Check if already a member
        boolean alreadyMember = groupMemberRepository.existsByGroupAndUser(group, user);
        System.out.println("GroupService - User already member: " + alreadyMember);
        if (alreadyMember) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        GroupMember member = new GroupMember(group, user);
        groupMemberRepository.save(member);
        System.out.println("GroupService - Created GroupMember for user " + userId + " in group " + groupId + " with role MEMBER");
        
        return new GroupDto(group, MemberRole.MEMBER);
    }
    
    // Leave group
    public void leaveGroup(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        // Check if user is the creator/admin
        if (member.getRole() == MemberRole.ADMIN) {
            // If there are other admins, allow leaving
            List<GroupMember> admins = groupMemberRepository.findMembersByGroupAndRole(group, MemberRole.ADMIN);
            if (admins.size() <= 1) {
                throw new RuntimeException("Cannot leave group as the only admin. Transfer admin role or delete group.");
            }
        }
        
        groupMemberRepository.delete(member);
    }
    
    // Get group members
    public List<GroupMemberDto> getGroupMembers(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        List<GroupMember> members = groupMemberRepository.findMembersByGroupOrdered(group);
        
        return members.stream()
                .map(GroupMemberDto::new)
                .collect(Collectors.toList());
    }
    
    // Update group info (admin only)
    public GroupDto updateGroup(Long groupId, Long userId, String name, String description, String photoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (member.getRole() != MemberRole.ADMIN) {
            throw new RuntimeException("Only admins can update group information");
        }
        
        if (name != null) {
            group.setName(name);
        }
        if (description != null) {
            group.setDescription(description);
        }
        if (photoUrl != null) {
            group.setPhotoUrl(photoUrl);
        }
        
        group = groupRepository.save(group);
        
        return new GroupDto(group, MemberRole.ADMIN);
    }
    
    // Delete group (admin only)
    public void deleteGroup(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (member.getRole() != MemberRole.ADMIN) {
            throw new RuntimeException("Only admins can delete the group");
        }
        
        group.setIsActive(false);
        groupRepository.save(group);
    }
    
    // Explore groups (groups user is not a member of)
    public List<GroupDto> exploreGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get all active groups
        List<Group> allGroups = groupRepository.findByIsActiveTrue();
        
        // Get groups user is already a member of
        List<Group> userGroups = groupRepository.findGroupsByMember(user);
        
        // Filter out groups user is already a member of
        List<Group> exploreGroups = allGroups.stream()
                .filter(group -> !userGroups.contains(group))
                .collect(Collectors.toList());
        
        return exploreGroups.stream()
                .map(group -> {
                    GroupDto dto = new GroupDto();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setDescription(group.getDescription());
                    dto.setPhotoUrl(group.getPhotoUrl());
                    dto.setMembersCount(group.getMembersCount());
                    dto.setCreatedAt(group.getCreatedAt());
                    dto.setUpdatedAt(group.getUpdatedAt());
                    dto.setUserRole(null); // null role since user is not a member
                    dto.setCategory(group.getCategory());
                    dto.setIsJoined(false); // user is not a member
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    private String formatTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + "m ago";
        } else if (hours < 24) {
            return hours + "h ago";
        } else if (days < 7) {
            return days + "d ago";
        } else {
            return dateTime.toLocalDate().toString();
        }
    }
}
