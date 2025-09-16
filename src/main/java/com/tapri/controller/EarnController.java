package com.tapri.controller;

import com.tapri.entity.Job;
import com.tapri.entity.JobClaim;
import com.tapri.service.EarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/earn")
@CrossOrigin(origins = "*")
public class EarnController {

    @Autowired
    private EarnService earnService;

    @GetMapping("/jobs")
    public ResponseEntity<?> listJobs() {
        List<Job> jobs = earnService.listJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<?> getJob(@PathVariable Long id) {
        Optional<Job> job = earnService.getJob(id);
        return job.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("error", "JOB_NOT_FOUND")));
    }

    @PostMapping("/jobs/{id}/claim")
    public ResponseEntity<?> claim(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        Map<String, Object> claim = earnService.claimJob(userId, id);
        return ResponseEntity.ok(claim);
    }

    @GetMapping("/claims/ongoing")
    public ResponseEntity<?> ongoing(@RequestAttribute("userId") Long userId) {
        Optional<JobClaim> claim = earnService.getOngoingClaim(userId);
        return claim.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok().body(Map.of("message", "NO_ONGOING_CLAIM")));
    }

    @PostMapping("/claims/{claimId}/submit")
    public ResponseEntity<?> submit(@PathVariable Long claimId,
                                    @RequestAttribute("userId") Long userId,
                                    @RequestBody Map<String, String> body) {
        String proofUrl = body.get("proofUrl");
        String proofNotes = body.get("notes");
        JobClaim claim = earnService.submitProof(userId, claimId, proofUrl, proofNotes);
        return ResponseEntity.ok(claim);
    }
} 