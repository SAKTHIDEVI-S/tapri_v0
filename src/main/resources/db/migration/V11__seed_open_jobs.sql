-- Seed 5 open demo jobs (no time window) if not already present
INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
SELECT 'Grocery Delivery', 'Neighborhood grocery drops', 600.00, 120.00, 240, NULL, NULL, TRUE, 'JP Nagar', '+91 9000000101', '2 wheeler; GPS', 'Pickup and drop photos', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Grocery Delivery');

INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
SELECT 'Medicine Delivery', 'Pickup medicines from pharmacy and deliver', 400.00, 100.00, 180, NULL, NULL, TRUE, 'BTM Layout', '+91 9000000102', '2 wheeler; ID', 'Collect bill; delivery proof', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Medicine Delivery');

INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
SELECT 'Food Delivery', 'Collect food order and drop at address', 500.00, 125.00, 180, NULL, NULL, TRUE, 'Koramangala', '+91 9000000103', '2 wheeler; Insulated bag', 'Order code; drop photo', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Food Delivery');

INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
SELECT 'Parcel Pickup', 'Pick a parcel and drop at office', 350.00, 87.50, 120, NULL, NULL, TRUE, 'HSR Layout', '+91 9000000104', '2 wheeler', 'Handle with care', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Parcel Pickup');

INSERT INTO jobs (title, description, payout, hourly_rate, duration_minutes, starts_at, ends_at, is_active, location, contact_phone, requirements, instructions, pickup_proof_required, dropoff_proof_required)
SELECT 'Document Drop', 'Collect documents and submit', 300.00, 100.00, 120, NULL, NULL, TRUE, 'Jayanagar', '+91 9000000105', '2 wheeler; GPS', 'Get receiving stamp', TRUE, TRUE
WHERE NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Document Drop'); 