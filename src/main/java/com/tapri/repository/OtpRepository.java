package com.tapri.repository;

import com.tapri.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findByPhoneAndExpiresAtAfter(String phone, LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpCode o WHERE o.phone = :phone")
    void deleteByPhone(@Param("phone") String phone);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}
