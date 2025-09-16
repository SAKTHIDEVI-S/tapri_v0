-- Ensure demo jobs exist (MySQL-compatible): seed only if table is empty
INSERT INTO jobs (
    title, description, payout, hourly_rate, duration_minutes,
    starts_at, ends_at, is_active, location, contact_phone,
    requirements, instructions, pickup_proof_required, dropoff_proof_required
)
SELECT s.title, s.description, s.payout, s.hourly_rate, s.duration_minutes,
       s.starts_at, s.ends_at, s.is_active, s.location, s.contact_phone,
       s.requirements, s.instructions, s.pickup_proof_required, s.dropoff_proof_required
FROM (
    SELECT 'Grocery Delivery' AS title, 'Pickup groceries and deliver to doorstep' AS description, 600.00 AS payout, 120.00 AS hourly_rate, 240 AS duration_minutes,
           TIMESTAMP(CURRENT_DATE, '11:00:00') AS starts_at, TIMESTAMP(CURRENT_DATE, '15:00:00') AS ends_at, TRUE AS is_active,
           'JP Nagar' AS location, '+91 9000000001' AS contact_phone, '2 wheeler; GPS' AS requirements, 'Pickup and drop photos' AS instructions,
           TRUE AS pickup_proof_required, TRUE AS dropoff_proof_required
    UNION ALL SELECT 'Medicine Delivery', 'Pickup medicines from pharmacy and deliver', 400.00, 100.00, 240,
           TIMESTAMP(CURRENT_DATE, '11:00:00'), TIMESTAMP(CURRENT_DATE, '15:00:00'), TRUE,
           'BTM Layout', '+91 9000000002', '2 wheeler; ID', 'Collect bill; delivery proof', TRUE, TRUE
    UNION ALL SELECT 'Food Delivery', 'Collect food order and drop at address', 500.00, 125.00, 240,
           TIMESTAMP(CURRENT_DATE, '14:00:00'), TIMESTAMP(CURRENT_DATE, '18:00:00'), TRUE,
           'Koramangala', '+91 9000000003', '2 wheeler; Insulated bag', 'Order code; photo at drop', TRUE, TRUE
    UNION ALL SELECT 'Parcel Pickup', 'Pick a parcel and drop at office', 350.00, 87.50, 240,
           TIMESTAMP(CURRENT_DATE, '11:00:00'), TIMESTAMP(CURRENT_DATE, '15:00:00'), TRUE,
           'HSR Layout', '+91 9000000004', '2 wheeler', 'Handle with care', TRUE, TRUE
    UNION ALL SELECT 'Document Drop', 'Collect documents and submit', 300.00, 100.00, 180,
           TIMESTAMP(CURRENT_DATE, '09:00:00'), TIMESTAMP(CURRENT_DATE, '12:00:00'), TRUE,
           'JP Nagar 7th Phase', '+91 9000000005', '2 wheeler; GPS', 'Get receiving stamp', TRUE, TRUE
    UNION ALL SELECT 'Grocery Delivery - Tomorrow', 'Distribute groceries', 620.00, 124.00, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '11:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '15:00:00'), TRUE,
           'JP Nagar', '+91 9000000011', '2 wheeler; GPS', 'Photos required', TRUE, TRUE
    UNION ALL SELECT 'Medicine Delivery - Tomorrow', 'Pharmacy to customer', 420.00, 105.00, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '10:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '14:00:00'), TRUE,
           'BTM Layout', '+91 9000000012', '2 wheeler', 'Collect bill', TRUE, TRUE
    UNION ALL SELECT 'Food Delivery - Tomorrow', 'Restaurant to home', 520.00, 130.00, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '18:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 1 DAY), '22:00:00'), TRUE,
           'HSR Layout', '+91 9000000013', '2 wheeler; Food bag', 'Order code check', TRUE, TRUE
    UNION ALL SELECT 'Grocery Delivery - Next Week', 'Neighborhood delivery', 610.00, 122.00, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), '11:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 3 DAY), '15:00:00'), TRUE,
           'JP Nagar', '+91 9000000021', '2 wheeler; GPS', 'Pickup and drop photos', TRUE, TRUE
    UNION ALL SELECT 'Medicine Delivery - Next Week', 'Pharmacy run', 410.00, 102.50, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), '10:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 4 DAY), '14:00:00'), TRUE,
           'Koramangala', '+91 9000000022', '2 wheeler', 'Collect bill', TRUE, TRUE
    UNION ALL SELECT 'Food Delivery - Next Week', 'Dinner window', 540.00, 135.00, 240,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), '18:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 5 DAY), '22:00:00'), TRUE,
           'BTM Layout', '+91 9000000023', '2 wheeler; Insulated bag', 'Order code; drop photo', TRUE, TRUE
    UNION ALL SELECT 'Document Drop - Next Week', 'Submit at client office', 320.00, 106.67, 180,
           TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), '09:00:00'), TIMESTAMP(DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), '12:00:00'), TRUE,
           'Jayanagar', '+91 9000000024', '2 wheeler; GPS', 'Get receiving stamp', TRUE, TRUE
) AS s
WHERE NOT EXISTS (SELECT 1 FROM jobs); 