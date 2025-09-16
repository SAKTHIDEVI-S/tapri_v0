-- Add state column to users if missing
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'state'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN state VARCHAR(100);', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt; 