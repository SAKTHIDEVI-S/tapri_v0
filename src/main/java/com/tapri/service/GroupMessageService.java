package com.tapri.service;

import com.tapri.dto.GroupMessageDto;
import com.tapri.entity.Group;
import com.tapri.entity.GroupMessage;
import com.tapri.entity.GroupMessageRead;
import com.tapri.entity.MediaType;
import com.tapri.entity.User;
import com.tapri.repository.GroupMemberRepository;
import com.tapri.repository.GroupMessageRepository;
import com.tapri.repository.GroupMessageReadRepository;
import com.tapri.repository.GroupRepository;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupMessageService {
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupMessageReadRepository groupMessageReadRepository;
    
    public GroupMessagesResponse getGroupMessages(Long groupId, Long userId, int page, int size) {
        // Verify user is member of group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, user);
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Get paginated messages
        Pageable pageable = PageRequest.of(page, size);
        Page<GroupMessage> messagePage = groupMessageRepository.findByGroupAndIsActiveTrueOrderByCreatedAtDesc(group, pageable);
        
        List<GroupMessageDto> messages = messagePage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        GroupMessagesResponse response = new GroupMessagesResponse();
        response.setMessages(messages);
        response.setTotalElements((int) messagePage.getTotalElements());
        response.setTotalPages(messagePage.getTotalPages());
        response.setCurrentPage(messagePage.getNumber());
        response.setSize(messagePage.getSize());
        response.setHasNext(messagePage.hasNext());
        
        return response;
    }
    
    public GroupMessageDto sendGroupMessage(Long groupId, Long userId, String content, String mediaUrl, String mediaType) {
        // Verify user is member of group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, user);
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Create message
        GroupMessage message = new GroupMessage();
        message.setGroup(group);
        message.setUser(user);
        message.setContent(content);
        message.setMediaUrl(mediaUrl);
        if (mediaType != null) {
            message.setMediaType(MediaType.valueOf(mediaType.toUpperCase()));
        }
        message.setIsActive(true);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        
        GroupMessage savedMessage = groupMessageRepository.save(message);
        
        // Automatically mark the message as read for the sender
        GroupMessageRead readRecord = new GroupMessageRead(savedMessage, user);
        groupMessageReadRepository.save(readRecord);
        
        return convertToDto(savedMessage);
    }
    
    public void markMessagesAsRead(Long groupId, Long userId) {
        // Verify user is member of group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isMember = groupMemberRepository.existsByGroupAndUser(group, user);
        if (!isMember) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Get all unread messages for this user in this group
        List<GroupMessage> unreadMessages = groupMessageRepository.findByGroupAndIsActiveTrueOrderByCreatedAtDesc(group)
                .stream()
                .filter(message -> !groupMessageReadRepository.existsByMessageAndUser(message, user))
                .collect(Collectors.toList());
        
        // Mark all unread messages as read
        for (GroupMessage message : unreadMessages) {
            GroupMessageRead readRecord = new GroupMessageRead(message, user);
            groupMessageReadRepository.save(readRecord);
        }
    }
    
    private GroupMessageDto convertToDto(GroupMessage message) {
        return new GroupMessageDto(message);
    }
    
    // Inner class for response
    public static class GroupMessagesResponse {
        private List<GroupMessageDto> messages;
        private int totalElements;
        private int totalPages;
        private int currentPage;
        private int size;
        private boolean hasNext;
        
        // Getters and Setters
        public List<GroupMessageDto> getMessages() {
            return messages;
        }
        
        public void setMessages(List<GroupMessageDto> messages) {
            this.messages = messages;
        }
        
        public int getTotalElements() {
            return totalElements;
        }
        
        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
        
        public int getCurrentPage() {
            return currentPage;
        }
        
        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }
        
        public int getSize() {
            return size;
        }
        
        public void setSize(int size) {
            this.size = size;
        }
        
        public boolean isHasNext() {
            return hasNext;
        }
        
        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
    }
    
}
