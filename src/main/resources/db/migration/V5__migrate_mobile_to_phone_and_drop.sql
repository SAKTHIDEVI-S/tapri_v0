-- Migrate legacy `mobile` column to `phone` and drop `mobile` if present

-- 1) If `mobile` exists, copy non-null values into `phone` where missing
SET @has_mobile := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'mobile'
);

SET @sql_copy := IF(@has_mobile = 1,
    'UPDATE users SET phone = mobile WHERE phone IS NULL AND mobile IS NOT NULL;',
    'SELECT 1');
PREPARE stmt FROM @sql_copy; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) If `mobile` exists, drop the column to avoid future NOT NULL/default issues
SET @sql_drop := IF(@has_mobile = 1,
    'ALTER TABLE users DROP COLUMN mobile;',
    'SELECT 1');
PREPARE stmt FROM @sql_drop; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Optional: ensure phone is NOT NULL and UNIQUE (uncomment if needed)
-- ALTER TABLE users MODIFY COLUMN phone VARCHAR(15) NOT NULL;
-- CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone ON users (phone); 