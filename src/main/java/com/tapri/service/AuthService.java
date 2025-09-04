package com.tapri.service;

import com.tapri.entity.User;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepo;

    public User signup(User user) {
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));
        return userRepo.save(user);
    }

    public boolean verifyFirebaseToken(String mobile, String firebaseUid) {
        // In a real app, you would verify the Firebase token with Firebase Admin SDK
        // For now, we'll just check if the user exists and update their verification status
        Optional<User> userOpt = userRepo.findByMobile(mobile);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIs_verified(true);
            user.setLast_login(new Timestamp(System.currentTimeMillis()));
            userRepo.save(user);
            return true;
        }
        return false;
    }

    public Optional<User> getUserByMobile(String mobile) {
        return userRepo.findByMobile(mobile);
    }

    public void updateLastLogin(Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLast_login(new Timestamp(System.currentTimeMillis()));
            userRepo.save(user);
        }
    }
} 