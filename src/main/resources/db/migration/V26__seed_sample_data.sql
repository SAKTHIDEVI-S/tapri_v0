-- Seed sample data for testing

-- Insert sample posts
INSERT IGNORE INTO posts (user_id, text, media_url, media_type, is_active, created_at) VALUES
(1, 'Just completed my first delivery! The customer was so happy with the service. #TapriLife', 'https://example.com/post1.jpg', 'IMAGE', TRUE, NOW()),
(1, 'Beautiful sunset view from my delivery route today 🌅', 'https://example.com/sunset.jpg', 'IMAGE', TRUE, NOW()),
(2, 'Quick tip: Always double-check the address before starting delivery. Saves time!', NULL, 'IMAGE', TRUE, NOW()),
(2, 'Met some amazing people today during deliveries. Love this job!', 'https://example.com/people.jpg', 'IMAGE', TRUE, NOW()),
(3, 'New to Tapri but loving the flexibility. Any tips for a beginner?', NULL, 'IMAGE', TRUE, NOW()),
(3, 'First week completed! Thanks to everyone for the support 🙏', 'https://example.com/week1.jpg', 'IMAGE', TRUE, NOW());

-- Insert sample groups
INSERT IGNORE INTO `groups` (name, description, photo_url, created_by, is_active, created_at) VALUES
('Mumbai Drivers', 'Connect with fellow drivers in Mumbai. Share tips, routes, and experiences!', 'https://example.com/mumbai-group.jpg', 1, TRUE, NOW()),
('Delivery Tips & Tricks', 'Share and learn delivery best practices, customer service tips, and efficiency hacks.', 'https://example.com/tips-group.jpg', 2, TRUE, NOW());

-- Add users to groups
INSERT IGNORE INTO group_members (group_id, user_id, role, joined_at) VALUES
(1, 1, 'ADMIN', NOW()),
(1, 2, 'MEMBER', NOW()),
(1, 3, 'MEMBER', NOW()),
(1, 4, 'MEMBER', NOW()),
(2, 2, 'ADMIN', NOW()),
(2, 1, 'MEMBER', NOW()),
(2, 5, 'MEMBER', NOW()),
(2, 6, 'MEMBER', NOW());

-- Insert sample group messages
INSERT IGNORE INTO group_messages (group_id, user_id, content, media_url, media_type, is_active, created_at) VALUES
(1, 1, 'Welcome everyone to Mumbai Drivers group! 🚗', NULL, 'IMAGE', TRUE, NOW()),
(1, 2, 'Thanks for creating this group! Looking forward to connecting with everyone.', NULL, 'IMAGE', TRUE, NOW()),
(1, 3, 'Any good delivery routes in Andheri area?', NULL, 'IMAGE', TRUE, NOW()),
(1, 4, 'I know some great routes in Andheri. Will share the details!', NULL, 'IMAGE', TRUE, NOW()),
(2, 2, 'Welcome to Delivery Tips & Tricks! Share your best practices here.', NULL, 'IMAGE', TRUE, NOW()),
(2, 1, 'Always carry extra packaging materials. You never know when you might need them!', NULL, 'IMAGE', TRUE, NOW()),
(2, 5, 'Great tip! I also keep a small first aid kit in my vehicle.', NULL, 'IMAGE', TRUE, NOW()),
(2, 6, 'New here. Thanks for all the tips! 🙏', NULL, 'IMAGE', TRUE, NOW());

-- Insert sample post comments
INSERT IGNORE INTO post_comments (post_id, user_id, content, is_active, created_at) VALUES
(1, 2, 'Congratulations on your first delivery! 🎉', TRUE, NOW()),
(1, 3, 'Welcome to the Tapri family!', TRUE, NOW()),
(2, 4, 'Beautiful sunset! Where was this taken?', TRUE, NOW()),
(3, 1, 'Great tip! I always do this too.', TRUE, NOW()),
(4, 5, 'The people you meet make this job special!', TRUE, NOW());

-- Insert sample post likes
INSERT IGNORE INTO post_likes (post_id, user_id, created_at) VALUES
(1, 2, NOW()),
(1, 3, NOW()),
(1, 4, NOW()),
(2, 1, NOW()),
(2, 3, NOW()),
(3, 1, NOW()),
(3, 4, NOW()),
(4, 1, NOW()),
(4, 5, NOW()),
(5, 2, NOW()),
(5, 4, NOW()),
(6, 1, NOW()),
(6, 3, NOW());
