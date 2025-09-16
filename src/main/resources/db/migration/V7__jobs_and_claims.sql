-- Jobs table
CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    payout DECIMAL(10,2) NOT NULL,
    duration_minutes INT NOT NULL,
    starts_at TIMESTAMP NULL,
    ends_at TIMESTAMP NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_jobs_active (is_active),
    INDEX idx_jobs_window (starts_at, ends_at)
);

-- Job claims table
CREATE TABLE IF NOT EXISTS job_claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_at TIMESTAMP NULL,
    submitted_at TIMESTAMP NULL,
    proof_url VARCHAR(500),
    proof_notes VARCHAR(500),
    UNIQUE KEY uniq_job_user_open (job_id, user_id, status),
    INDEX idx_claims_user_status (user_id, status),
    CONSTRAINT fk_claim_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    CONSTRAINT fk_claim_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Seed sample jobs
INSERT INTO jobs (title, description, payout, duration_minutes, is_active)
VALUES
('Deliver Flyers', 'Distribute flyers in your neighborhood', 50.00, 60, TRUE),
('Store Visit Audit', 'Visit store and take photos as per checklist', 120.00, 90, TRUE),
('On-foot Survey', 'Collect feedback from 5 local shops', 80.00, 75, TRUE); 