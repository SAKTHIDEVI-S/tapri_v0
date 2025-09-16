package com.tapri.service;

import com.tapri.dto.GroupMessageDto;
import com.tapri.dto.MessageReactionDto;
import com.tapri.entity.*;
import com.tapri.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private GroupMessageRepository groupMessageRepository;
    
    @Autowired
    private MessageReactionRepository messageReactionRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Note: WebSocket messaging will be enabled when dependencies are available
    // @Autowired
    // private SimpMessagingTemplate messagingTemplate;
    
    // Send message to group
    public GroupMessageDto sendMessage(Long groupId, Long userId, String content, String mediaUrl, MediaType mediaType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        GroupMessage message = new GroupMessage(group, user, content, mediaUrl, mediaType);
        message = groupMessageRepository.save(message);
        
        GroupMessageDto messageDto = new GroupMessageDto(message);
        
        // Send to WebSocket subscribers (will be enabled when dependencies are available)
        // messagingTemplate.convertAndSend("/topic/groups/" + groupId, messageDto);
        
        return messageDto;
    }
    
    // Get group messages
    public List<GroupMessageDto> getGroupMessages(Long groupId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        List<GroupMessage> messages = groupMessageRepository.findActiveMessagesByGroup(group);
        
        return messages.stream().map(message -> {
            GroupMessageDto messageDto = new GroupMessageDto(message);
            
            // Load reactions
            List<MessageReaction> reactions = messageReactionRepository.findReactionsByMessage(message);
            List<MessageReactionDto> reactionDtos = reactions.stream()
                    .map(MessageReactionDto::new)
                    .collect(Collectors.toList());
            messageDto.setReactions(reactionDtos);
            
            return messageDto;
        }).collect(Collectors.toList());
    }
    
    // Add reaction to message
    public MessageReactionDto addReaction(Long messageId, Long userId, String emoji) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getIsActive()) {
            throw new RuntimeException("Message not found");
        }
        
        // Check if user is a member of the group
        if (!groupMemberRepository.existsByGroupAndUser(message.getGroup(), user)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        Optional<MessageReaction> existingReaction = messageReactionRepository.findByMessageAndUser(message, user);
        
        if (existingReaction.isPresent()) {
            // Update existing reaction
            MessageReaction reaction = existingReaction.get();
            reaction.setEmoji(emoji);
            reaction = messageReactionRepository.save(reaction);
            return new MessageReactionDto(reaction);
        } else {
            // Create new reaction
            MessageReaction reaction = new MessageReaction(message, user, emoji);
            reaction = messageReactionRepository.save(reaction);
            return new MessageReactionDto(reaction);
        }
    }
    
    // Remove reaction from message
    public void removeReaction(Long messageId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getIsActive()) {
            throw new RuntimeException("Message not found");
        }
        
        // Check if user is a member of the group
        if (!groupMemberRepository.existsByGroupAndUser(message.getGroup(), user)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        Optional<MessageReaction> reaction = messageReactionRepository.findByMessageAndUser(message, user);
        if (reaction.isPresent()) {
            messageReactionRepository.delete(reaction.get());
        }
    }
    
    // Edit message
    public GroupMessageDto editMessage(Long messageId, Long userId, String newContent) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getIsActive()) {
            throw new RuntimeException("Message not found");
        }
        
        // Check if user is the message sender
        if (!message.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized to edit this message");
        }
        
        message.setContent(newContent);
        message.setIsEdited(true);
        message = groupMessageRepository.save(message);
        
        GroupMessageDto messageDto = new GroupMessageDto(message);
        
        // Send update to WebSocket subscribers (will be enabled when dependencies are available)
        // messagingTemplate.convertAndSend("/topic/groups/" + message.getGroup().getId(), messageDto);
        
        return messageDto;
    }
    
    // Delete message (soft delete)
    public void deleteMessage(Long messageId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (!message.getIsActive()) {
            throw new RuntimeException("Message not found");
        }
        
        // Check if user is the message sender or admin
        GroupMember member = groupMemberRepository.findByGroupAndUser(message.getGroup(), user)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (!message.getUser().getId().equals(userId) && member.getRole() != MemberRole.ADMIN) {
            throw new RuntimeException("Unauthorized to delete this message");
        }
        
        message.setIsActive(false);
        groupMessageRepository.save(message);
        
        // Send delete notification to WebSocket subscribers (will be enabled when dependencies are available)
        // messagingTemplate.convertAndSend("/topic/groups/" + message.getGroup().getId(), 
        //         "MESSAGE_DELETED:" + messageId);
    }
    
    // Send typing indicator
    public void sendTypingIndicator(Long groupId, Long userId, boolean isTyping) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupAndUser(group, userRepository.findById(userId).orElse(null))) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Send typing indicator to WebSocket subscribers (will be enabled when dependencies are available)
        // String typingData = String.format("{\"userId\":%d,\"userName\":\"%s\",\"isTyping\":%s}", 
        //         userId, user.getName(), isTyping);
        // messagingTemplate.convertAndSend("/topic/groups/" + groupId + "/typing", typingData);
    }
}