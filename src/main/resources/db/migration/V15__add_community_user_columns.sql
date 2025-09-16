-- Add missing columns for community features to users table

-- bio VARCHAR(500)
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'bio'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN bio VARCHAR(500);', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- profile_photo_url VARCHAR(255)
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'profile_photo_url'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN profile_photo_url VARCHAR(255);', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- last_seen TIMESTAMP NULL
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'last_seen'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN last_seen TIMESTAMP NULL;', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- last_seen_visible BOOLEAN DEFAULT TRUE
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'last_seen_visible'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN last_seen_visible BOOLEAN DEFAULT TRUE;', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- is_active BOOLEAN DEFAULT TRUE
SET @col_exists := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'is_active'
);
SET @ddl := IF(@col_exists = 0, 'ALTER TABLE users ADD COLUMN is_active BOOLEAN DEFAULT TRUE;', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
