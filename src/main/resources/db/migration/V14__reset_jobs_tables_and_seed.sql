-- Reset only job-related tables and seed fresh data
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS job_claims;
DROP TABLE IF EXISTS jobs;
SET FOREIGN_KEY_CHECKS = 1;

-- Recreate jobs
CREATE TABLE jobs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  description TEXT,
  payout DECIMAL(10,2) NOT NULL,
  hourly_rate DECIMAL(10,2) NULL,
  duration_minutes INT NOT NULL,
  starts_at TIMESTAMP NULL,
  ends_at TIMESTAMP NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  location VARCHAR(255),
  contact_phone VARCHAR(32),
  requirements TEXT,
  instructions TEXT,
  pickup_proof_required BOOLEAN NOT NULL DEFAULT TRUE,
  dropoff_proof_required BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_jobs_active (is_active),
  INDEX idx_jobs_window (starts_at, ends_at)
);

-- Recreate job_claims
CREATE TABLE job_claims (
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

-- Seed 5 Today
INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
VALUES
('Grocery Delivery - Today 1','Neighborhood grocery drops',600.00,120.00,240, NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), TRUE,'JP Nagar','+91 9000002101','2 wheeler; GPS','Pickup & drop photos',TRUE,TRUE),
('Grocery Delivery - Today 2','Neighborhood grocery drops',580.00,116.00,240, NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), TRUE,'BTM Layout','+91 9000002102','2 wheeler; GPS','Pickup & drop photos',TRUE,TRUE),
('Medicine Delivery - Today','Pharmacy to customer',420.00,105.00,240, NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), TRUE,'Jayanagar','+91 9000002103','2 wheeler; ID','Collect bill',TRUE,TRUE),
('Food Delivery - Today','Restaurant to home',500.00,125.00,240, NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), TRUE,'Koramangala','+91 9000002104','2 wheeler; bag','Order code check',TRUE,TRUE),
('Parcel Pickup - Today','Pick parcel and drop at office',350.00,87.50,240, NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR), TRUE,'HSR Layout','+91 9000002105','2 wheeler','Handle with care',TRUE,TRUE);

-- Seed 3 Tomorrow (11–15)
INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
VALUES
('Grocery Delivery - Tomorrow','Distribute groceries',620.00,124.00,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'15:00:00'), TRUE,'JP Nagar','+91 9000002201','2 wheeler; GPS','Photos required',TRUE,TRUE),
('Medicine Delivery - Tomorrow','Pharmacy to customer',430.00,107.50,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'11:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'15:00:00'), TRUE,'BTM Layout','+91 9000002202','2 wheeler; ID','Collect bill',TRUE,TRUE),
('Food Delivery - Tomorrow','Restaurant to home',520.00,130.00,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'18:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'22:00:00'), TRUE,'HSR Layout','+91 9000002203','2 wheeler; bag','Order code check',TRUE,TRUE);

-- Seed 4 Next Week (in 3–6 days, 11–15)
INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
VALUES
('Grocery Delivery - Next Week','Neighborhood delivery',610.00,122.00,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY),'11:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY),'15:00:00'), TRUE,'JP Nagar','+91 9000002301','2 wheeler; GPS','Pickup & drop photos',TRUE,TRUE),
('Medicine Delivery - Next Week','Pharmacy run',410.00,102.50,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 4 DAY),'11:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 4 DAY),'15:00:00'), TRUE,'Koramangala','+91 9000002302','2 wheeler','Collect bill',TRUE,TRUE),
('Food Delivery - Next Week','Dinner window',540.00,135.00,240, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 5 DAY),'18:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 5 DAY),'22:00:00'), TRUE,'BTM Layout','+91 9000002303','2 wheeler; bag','Order code; drop photo',TRUE,TRUE),
('Document Drop - Next Week','Submit at client office',320.00,106.67,180, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 6 DAY),'09:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 6 DAY),'12:00:00'), TRUE,'Jayanagar','+91 9000002304','2 wheeler; GPS','Get receiving stamp',TRUE,TRUE); 