-- Clean up database and Flyway history
USE tapri;

-- Remove failed migration entries
DELETE FROM flyway_schema_history WHERE version IN ('23', '24');

-- Drop posts table if it exists with wrong structure
DROP TABLE IF EXISTS posts;

-- Check current Flyway state
SELECT version, description, success FROM flyway_schema_history ORDER BY version;
