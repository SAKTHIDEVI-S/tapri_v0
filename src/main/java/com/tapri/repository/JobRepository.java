package com.tapri.repository;

import com.tapri.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND (j.startsAt IS NULL OR j.startsAt <= :now) AND (j.endsAt IS NULL OR j.endsAt >= :now)")
    List<Job> findActiveJobs(LocalDateTime now);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND (j.startsAt IS NULL OR j.startsAt <= :now) AND (j.endsAt IS NULL OR j.endsAt >= :now) AND NOT EXISTS (SELECT 1 FROM JobClaim c WHERE c.job = j AND c.status = 'OPEN')")
    List<Job> findAvailableJobs(LocalDateTime now);

    // New: include future jobs so frontend tabs can display Now/Tomorrow/Next week
    @Query("SELECT j FROM Job j WHERE j.isActive = true AND (j.endsAt IS NULL OR j.endsAt >= :now)")
    List<Job> findActiveAndUpcoming(LocalDateTime now);
} 