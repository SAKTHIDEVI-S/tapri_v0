package com.tapri.entity;


import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "otps")
public class Otp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mobile;
    private String otp_code;
    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    private Timestamp expires_at;
    @Column(name = "is_used")
    private Boolean isUsed = false;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getOtp_code() { return otp_code; }
    public void setOtp_code(String otp_code) { this.otp_code = otp_code; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getExpires_at() { return expires_at; }
    public void setExpires_at(Timestamp expires_at) { this.expires_at = expires_at; }
    public Boolean getIsUsed() { return isUsed; }
    public void setIsUsed(Boolean isUsed) { this.isUsed = isUsed; }
} 