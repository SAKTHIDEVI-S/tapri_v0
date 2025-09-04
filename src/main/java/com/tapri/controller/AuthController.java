package com.tapri.controller;

import com.tapri.entity.User;
import com.tapri.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @GetMapping("/test")
    public String test() {
        return "Backend is running!";
    }
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public User signup(@RequestBody User user) {
        System.out.println("Signup request received for mobile: " + user.getMobile());
        try {
            User savedUser = authService.signup(user);
            System.out.println("User saved successfully with ID: " + savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error during signup: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/verify-firebase")
    public String verifyFirebase(@RequestBody Map<String, String> payload) {
        String mobile = payload.get("mobile");
        String firebaseUid = payload.get("firebaseUid");
        
        Optional<User> user = authService.getUserByMobile(mobile);
        if (user.isPresent()) {
            boolean valid = authService.verifyFirebaseToken(mobile, firebaseUid);
            if (valid) {
                return "Hello, " + user.map(User::getName).orElse("User");
            } else {
                return "Verification failed";
            }
        } else {
            return "User not found";
        }
    }

    @GetMapping("/users/profile/mobile/{mobile}")
    public User getUserProfileByMobile(@PathVariable String mobile) {
        System.out.println("Login request received for mobile: " + mobile);
        Optional<User> user = authService.getUserByMobile(mobile);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getName());
            return user.get();
        } else {
            System.out.println("User not found for mobile: " + mobile);
            return null;
        }
    }

    @PostMapping("/users/login/{userId}")
    public Map<String, String> updateLastLogin(@PathVariable Long userId) {
        authService.updateLastLogin(userId);
        return Map.of("message", "Last login updated successfully");
    }
} 