-- Enhance posts table to match frontend requirements

-- Add post_type column for quick buttons (Traffic Alert, Ask Help, Share Tip)
ALTER TABLE posts ADD COLUMN post_type VARCHAR(50) DEFAULT 'GENERAL';

-- Add audience column (Everyone, Groups)
ALTER TABLE posts ADD COLUMN audience VARCHAR(20) DEFAULT 'EVERYONE';

-- Add share_count column
ALTER TABLE posts ADD COLUMN share_count INT DEFAULT 0;

-- Create saved_posts table for user saved posts
CREATE TABLE IF NOT EXISTS saved_posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_saved_post (user_id, post_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- Create post_shares table to track shares
CREATE TABLE IF NOT EXISTS post_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_user_shared_post (user_id, post_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
