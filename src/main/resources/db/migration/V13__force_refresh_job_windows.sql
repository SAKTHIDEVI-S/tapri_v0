-- Force-refresh job windows so API returns lists immediately
-- Make all jobs active
UPDATE jobs SET is_active = 1;

-- 5 TODAY (Now): earliest 5 ids → now to +4h
UPDATE jobs j
JOIN (SELECT id FROM jobs ORDER BY id LIMIT 5) s ON j.id = s.id
SET j.starts_at = NOW(),
    j.ends_at   = DATE_ADD(NOW(), INTERVAL 4 HOUR);

-- 3 TOMORROW: next 3 ids → 11:00–15:00 tomorrow
UPDATE jobs j
JOIN (SELECT id FROM jobs ORDER BY id LIMIT 5,3) s ON j.id = s.id
SET j.starts_at = TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:00:00'),
    j.ends_at   = TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY), '15:00:00');

-- 4 NEXT WEEK: next 4 ids → 11:00–15:00 in 3 days
UPDATE jobs j
JOIN (SELECT id FROM jobs ORDER BY id LIMIT 8,4) s ON j.id = s.id
SET j.starts_at = TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '11:00:00'),
    j.ends_at   = TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '15:00:00');

-- Verify no-op
SELECT 1; 