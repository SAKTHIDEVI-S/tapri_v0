-- Seed sample posts for testing (after table structure is fixed)

INSERT IGNORE INTO posts (user_id, text, media_url, media_type, is_active, created_at) VALUES
(1, 'Just completed my first delivery! The customer was so happy with the service. #TapriLife', 'https://example.com/post1.jpg', 'IMAGE', TRUE, NOW()),
(1, 'Beautiful sunset view from my delivery route today 🌅', 'https://example.com/sunset.jpg', 'IMAGE', TRUE, NOW()),
(2, 'Quick tip: Always double-check the address before starting delivery. Saves time!', NULL, 'IMAGE', TRUE, NOW()),
(2, 'Met some amazing people today during deliveries. Love this job!', 'https://example.com/people.jpg', 'IMAGE', TRUE, NOW()),
(3, 'New to Tapri but loving the flexibility. Any tips for a beginner?', NULL, 'IMAGE', TRUE, NOW()),
(3, 'First week completed! Thanks to everyone for the support 🙏', 'https://example.com/week1.jpg', 'IMAGE', TRUE, NOW());
