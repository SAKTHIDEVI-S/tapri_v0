package com.tapri.service;

import com.tapri.entity.OtpCode;
import com.tapri.entity.User;
import com.tapri.repository.OtpRepository;
import com.tapri.repository.UserRepository;
import com.tapri.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    @Autowired
    private OtpRepository otpRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    public String sendOtp(String phone, String purpose) {
        boolean userExists = userRepository.existsByPhone(phone);
        if ("signup".equals(purpose) && userExists) {
            throw new RuntimeException("ALREADY_EXISTS");
        }
        if ("login".equals(purpose) && !userExists) {
            throw new RuntimeException("NEW_USER");
        }
        String otp = String.format("%06d", new Random().nextInt(1000000));
        String hashedOtp = passwordEncoder.encode(otp);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
        otpRepository.deleteByPhone(phone);
        OtpCode otpCode = new OtpCode(phone, hashedOtp, expiresAt);
        otpRepository.save(otpCode);
        System.out.println("OTP for " + phone + ": " + otp);
        return "OTP sent successfully";
    }
    
    public Object verifyOtp(String phone, String code) {
        OtpCode otpCode = otpRepository.findByPhoneAndExpiresAtAfter(phone, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("INVALID_OTP"));
        if (otpCode.getAttempts() >= 3) {
            throw new RuntimeException("MAX_ATTEMPTS_EXCEEDED");
        }
        if (!passwordEncoder.matches(code, otpCode.getCodeHash())) {
            otpCode.setAttempts(otpCode.getAttempts() + 1);
            otpRepository.save(otpCode);
            throw new RuntimeException("INVALID_OTP");
        }
        User user = userRepository.findByPhone(phone).orElse(null);
        if (user == null) {
            String tempToken = jwtUtil.generateTempToken(phone);
            return new java.util.HashMap<String, Object>() {{
                put("needsSignup", true);
                put("tempToken", tempToken);
            }};
        } else {
            String jwt = jwtUtil.generateToken(phone, user.getId());
            return new java.util.HashMap<String, Object>() {{
                put("jwt", jwt);
                put("user", user);
            }};
        }
    }
    
    public User completeSignup(String phone, String name, String city) {
        User user = new User(phone, name, city);
        return userRepository.save(user);
    }

    public User createUserDirect(String phone, String name, String city) {
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("ALREADY_EXISTS");
        }
        User user = new User(phone, name, city);
        return userRepository.save(user);
    }

    // Overload: direct signup with state
    public User createUserDirect(String phone, String name, String city, String state) {
        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("ALREADY_EXISTS");
        }
        User user = new User(phone, name, city);
        user.setState(state);
        return userRepository.save(user);
    }
}
