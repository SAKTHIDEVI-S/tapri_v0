package com.tapri.controller;

import com.tapri.entity.User;
import com.tapri.service.AuthService;
import com.tapri.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String purpose = request.get("purpose");
            
            if (phone == null || purpose == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone and purpose are required"));
            }
            
            String result = authService.sendOtp(phone, purpose);
            return ResponseEntity.ok(Map.of("message", result));
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        try {
            String phone = request.get("phone");
            String code = request.get("code");
            
            if (phone == null || code == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone and code are required"));
            }
            
            Object result = authService.verifyOtp(phone, code);
            
            // If verification was successful and user exists, add refresh token
            if (result instanceof Map) {
                Map<String, Object> resultMap = (Map<String, Object>) result;
                String jwt = (String) resultMap.get("jwt");
                if (jwt != null) {
                    // Extract user info to generate refresh token
                    Long userId = jwtUtil.getUserIdFromToken(jwt);
                    String refreshToken = jwtUtil.generateRefreshToken(phone, userId);
                    resultMap.put("refreshToken", refreshToken);
                    resultMap.put("expiresIn", 3600); // 1 hour in seconds
                }
            }
            
            return ResponseEntity.ok(result);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/signup/complete")
    public ResponseEntity<?> completeSignup(@RequestHeader("Temp-Token") String tempToken, 
                                          @RequestBody Map<String, String> request) {
        try {
            if (!jwtUtil.validateToken(tempToken) || !jwtUtil.isTempToken(tempToken)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid temp token"));
            }
            
            String phone = jwtUtil.getPhoneFromToken(tempToken);
            String name = request.get("name");
            String city = request.get("city");
            
            if (name == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
            }
            
            User user = authService.completeSignup(phone, name, city);
            String jwt = jwtUtil.generateToken(phone, user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(phone, user.getId());
            
            return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "refreshToken", refreshToken,
                "expiresIn", 3600, // 1 hour in seconds
                "user", user
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // New: Direct signup endpoint per requested flow
    @PostMapping("/signup")
    public ResponseEntity<?> directSignup(@RequestBody Map<String, String> request) {
        String phone = request.get("phone");
        String name = request.get("name");
        String city = request.get("city");
        String state = request.get("state");
        if (phone == null || name == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone and name are required"));
        }
        try {
            User user = (state != null && !state.isBlank())
                    ? authService.createUserDirect(phone, name, city, state)
                    : authService.createUserDirect(phone, name, city);
            String jwt = jwtUtil.generateToken(phone, user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(phone, user.getId());
            return ResponseEntity.ok(Map.of(
                "jwt", jwt,
                "refreshToken", refreshToken,
                "expiresIn", 3600, // 1 hour in seconds
                "user", user
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is required"));
            }
            
            // Validate the refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token"));
            }
            
            // Extract user info from refresh token
            String phone = jwtUtil.getPhoneFromToken(refreshToken);
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            
            // Generate new JWT token
            String newJwt = jwtUtil.generateToken(phone, userId);
            
            // Generate new refresh token (optional - you can reuse the same one)
            String newRefreshToken = jwtUtil.generateRefreshToken(phone, userId);
            
            return ResponseEntity.ok(Map.of(
                "jwt", newJwt,
                "refreshToken", newRefreshToken,
                "expiresIn", 3600 // 1 hour in seconds
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
