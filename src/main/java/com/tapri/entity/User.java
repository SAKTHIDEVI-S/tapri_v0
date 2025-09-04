package com.tapri.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mobile;
    private String name;
    private String city;
    private String state;
    private String email;
    private String profilePictureUrl;
    private Double rating = 0.0;
    private Integer totalRides = 0;
    private Double totalEarnings = 0.0;
    private String vehicleType;
    private String vehicleNumber;
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());
    private Timestamp last_login;
    private Boolean is_verified = false;
    private String referral_code;
    private String referred_by;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getTotalRides() { return totalRides; }
    public void setTotalRides(Integer totalRides) { this.totalRides = totalRides; }
    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    public Timestamp getCreated_at() { return created_at; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }
    public Timestamp getLast_login() { return last_login; }
    public void setLast_login(Timestamp last_login) { this.last_login = last_login; }
    public Boolean getIs_verified() { return is_verified; }
    public void setIs_verified(Boolean is_verified) { this.is_verified = is_verified; }
    public String getReferral_code() { return referral_code; }
    public void setReferral_code(String referral_code) { this.referral_code = referral_code; }
    public String getReferred_by() { return referred_by; }
    public void setReferred_by(String referred_by) { this.referred_by = referred_by; }
} 