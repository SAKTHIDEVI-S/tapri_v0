package com.tapri.repository;

import com.tapri.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByMobileAndIsUsedOrderByCreatedAtDesc(String mobile, Boolean isUsed);
} 