-- Clean up Flyway schema history to remove failed migrations
USE tapri;

-- Remove failed migration entries
DELETE FROM flyway_schema_history WHERE version = '23';

-- Check current state
SELECT version, description, success FROM flyway_schema_history ORDER BY version;
