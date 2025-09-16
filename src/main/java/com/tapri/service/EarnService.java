package com.tapri.service;

import com.tapri.entity.Job;
import com.tapri.entity.JobClaim;
import com.tapri.entity.User;
import com.tapri.repository.JobClaimRepository;
import com.tapri.repository.JobRepository;
import com.tapri.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EarnService {
    @Autowired private JobRepository jobRepository;
    @Autowired private JobClaimRepository jobClaimRepository;
    @Autowired private UserRepository userRepository;

    public List<Job> listJobs() {
        // Return active and upcoming jobs; client filters into tabs
        return jobRepository.findActiveAndUpcoming(LocalDateTime.now());
    }

    public Optional<Job> getJob(Long jobId) {
        return jobRepository.findById(jobId);
    }

    public Map<String, Object> claimJob(Long userId, Long jobId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("JOB_NOT_FOUND"));

        List<JobClaim> claims = jobClaimRepository.findByJob(job);
        boolean alreadyOpen = claims.stream().anyMatch(c -> "OPEN".equals(c.getStatus()));
        if (alreadyOpen) {
            throw new RuntimeException("ALREADY_CLAIMED");
        }

        JobClaim claim = new JobClaim();
        claim.setJob(job);
        claim.setUser(user);
        claim.setStatus("OPEN");
        claim.setDueAt(LocalDateTime.now().plusMinutes(job.getDurationMinutes()));
        jobClaimRepository.save(claim);

        Map<String, Object> result = new HashMap<>();
        result.put("claimId", claim.getId());
        result.put("dueAt", claim.getDueAt());
        return result;
    }

    public Optional<JobClaim> getOngoingClaim(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        return jobClaimRepository.findFirstByUserAndStatus(user, "OPEN");
    }

    public JobClaim submitProof(Long userId, Long claimId, String proofUrl, String proofNotes) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
        JobClaim claim = jobClaimRepository.findById(claimId).orElseThrow(() -> new RuntimeException("CLAIM_NOT_FOUND"));
        if (!claim.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("FORBIDDEN");
        }
        if (!"OPEN".equals(claim.getStatus())) {
            throw new RuntimeException("INVALID_STATUS");
        }
        claim.setStatus("SUBMITTED");
        claim.setProofUrl(proofUrl);
        claim.setProofNotes(proofNotes);
        claim.setSubmittedAt(LocalDateTime.now());
        JobClaim saved = jobClaimRepository.save(claim);

        // Update user's earnings immediately for demo (future: after approval)
        if (user.getEarnings() == null) user.setEarnings(0.0);
        double add = claim.getJob().getPayout().doubleValue();
        user.setEarnings(user.getEarnings() + add);
        userRepository.save(user);

        return saved;
    }
} 