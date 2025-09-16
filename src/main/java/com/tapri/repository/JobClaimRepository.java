package com.tapri.repository;

import com.tapri.entity.JobClaim;
import com.tapri.entity.Job;
import com.tapri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobClaimRepository extends JpaRepository<JobClaim, Long> {
    Optional<JobClaim> findFirstByUserAndStatus(User user, String status);
    List<JobClaim> findByUser(User user);
    List<JobClaim> findByJob(Job job);
} 