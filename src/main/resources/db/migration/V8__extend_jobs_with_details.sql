-- Extend jobs table for frontend fields
ALTER TABLE jobs
    ADD COLUMN hourly_rate DECIMAL(10,2) NULL AFTER payout,
    ADD COLUMN location VARCHAR(255) NULL,
    ADD COLUMN contact_phone VARCHAR(32) NULL,
    ADD COLUMN requirements TEXT NULL,
    ADD COLUMN instructions TEXT NULL,
    ADD COLUMN pickup_proof_required BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN dropoff_proof_required BOOLEAN NOT NULL DEFAULT TRUE;

-- Reset seed data to ensure exactly 5 rows for demo
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM job_claims;
DELETE FROM jobs;
SET FOREIGN_KEY_CHECKS = 1;

-- Seed 5 sample jobs matching the frontend
INSERT INTO jobs (
    title,
    description,
    payout,
    hourly_rate,
    duration_minutes,
    starts_at,
    ends_at,
    is_active,
    location,
    contact_phone,
    requirements,
    instructions,
    pickup_proof_required,
    dropoff_proof_required
) VALUES
('Grocery Delivery', 'Pickup groceries and deliver to customer doorstep', 600.00, 120.00, 240, '2025-06-30 11:00:00', '2025-06-30 15:00:00', TRUE, 'Greens supermarket, JP Nagar', '+91 6587349024', '2 wheeler vehicle; Smart phone with GPS', 'Pickup items; Take photo before leaving store and after delivery', TRUE, TRUE),
('Medicine Delivery', 'Pickup medicines from pharmacy and deliver', 400.00, 100.00, 240, '2025-06-30 10:00:00', '2025-06-30 14:00:00', TRUE, 'Apollo Pharmacy, BTM Layout', '+91 9123456780', '2 wheeler vehicle; Valid ID proof', 'Collect bill; Deliver to customer; Obtain signature', TRUE, TRUE),
('Food Delivery', 'Collect food order and drop at address', 500.00, 125.00, 240, '2025-06-30 18:00:00', '2025-06-30 22:00:00', TRUE, 'Koramangala 5th Block', '+91 9988776655', '2 wheeler vehicle; Insulated food bag', 'Confirm order code; Photo at pickup and drop', TRUE, TRUE),
('Parcel Pickup', 'Pick a small parcel and drop at office', 350.00, 87.50, 240, '2025-07-01 11:00:00', '2025-07-01 15:00:00', TRUE, 'HSR Layout', '+91 9000000001', '2 wheeler vehicle', 'Handle with care; Destination will be shared on claim', TRUE, TRUE),
('Document Drop', 'Collect documents and submit at client office', 300.00, 100.00, 180, '2025-07-01 09:00:00', '2025-07-01 12:00:00', TRUE, 'JP Nagar 7th Phase', '+91 9880001234', '2 wheeler vehicle; Smart phone with GPS', 'Carry ID; Get receiving stamp', TRUE, TRUE); 