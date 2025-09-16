package com.tapri.service;

import com.tapri.dto.UserDto;
import com.tapri.entity.User;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get user profile
    public UserDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserDto(user);
    }
    
    // Update user profile
    public UserDto updateUserProfile(Long userId, String name, String bio, String profilePhotoUrl, Boolean lastSeenVisible) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (name != null && !name.trim().isEmpty()) {
            user.setName(name.trim());
        }
        
        if (bio != null) {
            user.setBio(bio);
        }
        
        if (profilePhotoUrl != null) {
            user.setProfilePhotoUrl(profilePhotoUrl);
        }
        
        if (lastSeenVisible != null) {
            user.setLastSeenVisible(lastSeenVisible);
        }
        
        user = userRepository.save(user);
        
        return new UserDto(user);
    }
    
    // Update last seen
    public void updateLastSeen(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }
    
    // Search users
    public List<UserDto> searchUsers(String query) {
        List<User> users = userRepository.findActiveUsersByNameContaining(query);
        
        return users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
    
    // Get users with visible last seen
    public List<UserDto> getUsersWithVisibleLastSeen() {
        List<User> users = userRepository.findActiveUsersWithVisibleLastSeen();
        
        return users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }
}
