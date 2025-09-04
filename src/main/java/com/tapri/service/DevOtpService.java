package com.tapri.service;

import org.springframework.stereotype.Service;

@Service
public class DevOtpService {
    public void sendOtp(String to, String otp) {
        System.out.println("DEV OTP for " + to + ": " + otp);
    }
} 