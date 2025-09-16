-- Add category column to groups table
ALTER TABLE `groups` ADD COLUMN category VARCHAR(50) DEFAULT 'General';

-- Update existing groups to have a default category
UPDATE `groups` SET category = 'General' WHERE category IS NULL;