-- Add phone column to users if it does not exist (compatible across MySQL 8 variants)
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'users'
      AND COLUMN_NAME = 'phone'
);

SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN phone VARCHAR(15);', 'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt; 