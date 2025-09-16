-- Seed comprehensive posts data for testing

-- Insert sample posts with different types and media
INSERT IGNORE INTO posts (user_id, text, media_url, media_type, post_type, audience, share_count, is_active, created_at) VALUES
-- User 1 posts
(1, 'Just completed my first delivery! The customer was so happy with the service. #TapriLife', 'https://example.com/post1.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 5, TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(1, 'Beautiful sunset view from my delivery route today 🌅', 'https://example.com/sunset.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 12, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(1, 'Heavy traffic on MG Road! Avoid if possible. #TrafficAlert', NULL, 'IMAGE', 'TRAFFIC_ALERT', 'EVERYONE', 8, TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- User 2 posts
(2, 'Quick tip: Always double-check the address before starting delivery. Saves time!', NULL, 'IMAGE', 'SHARE_TIP', 'EVERYONE', 15, TRUE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(2, 'Met some amazing people today during deliveries. Love this job!', 'https://example.com/people.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 3, TRUE, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(2, 'Need help with finding a good parking spot near City Mall. Any suggestions?', NULL, 'IMAGE', 'ASK_HELP', 'EVERYONE', 7, TRUE, DATE_SUB(NOW(), INTERVAL 8 HOUR)),

-- User 3 posts
(3, 'New to Tapri but loving the flexibility. Any tips for a beginner?', NULL, 'IMAGE', 'ASK_HELP', 'EVERYONE', 20, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 'First week completed! Thanks to everyone for the support 🙏', 'https://example.com/week1.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 9, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 'Best delivery routes in the city. Sharing my experience!', 'https://example.com/routes.jpg', 'IMAGE', 'SHARE_TIP', 'EVERYONE', 18, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)),

-- User 4 posts
(4, 'Rainy day deliveries are challenging but rewarding!', 'https://example.com/rainy.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 6, TRUE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(4, 'Customer left a 5-star review today. Made my day! ⭐', NULL, 'IMAGE', 'GENERAL', 'EVERYONE', 11, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 'Road construction near Airport Road. Use alternative route!', NULL, 'IMAGE', 'TRAFFIC_ALERT', 'EVERYONE', 14, TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),

-- User 5 posts
(5, 'Weekend deliveries are always busy but fun!', 'https://example.com/weekend.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 4, TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 'How to handle difficult customers - my experience', 'https://example.com/customer_tips.mp4', 'VIDEO', 'SHARE_TIP', 'EVERYONE', 25, TRUE, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 'Looking for delivery partners in my area. Anyone interested?', NULL, 'IMAGE', 'ASK_HELP', 'EVERYONE', 13, TRUE, DATE_SUB(NOW(), INTERVAL 1 DAY)),

-- User 6 posts
(6, 'Just joined Tapri community. Excited to start this journey!', 'https://example.com/welcome.jpg', 'IMAGE', 'GENERAL', 'EVERYONE', 16, TRUE, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(6, 'First delivery completed successfully! 🎉', NULL, 'IMAGE', 'GENERAL', 'EVERYONE', 22, TRUE, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 'Traffic jam at Central Market. Avoid for next 2 hours!', NULL, 'IMAGE', 'TRAFFIC_ALERT', 'EVERYONE', 19, TRUE, DATE_SUB(NOW(), INTERVAL 6 HOUR));

-- Insert sample likes
INSERT IGNORE INTO post_likes (post_id, user_id, created_at) VALUES
-- Likes for post 1
(1, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 3, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(1, 4, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),

-- Likes for post 2
(2, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, 3, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(2, 5, DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(2, 6, DATE_SUB(NOW(), INTERVAL 14 HOUR)),

-- Likes for post 3 (Traffic Alert)
(3, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 4, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 5, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(3, 6, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),

-- Likes for post 4 (Share Tip)
(4, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(4, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(4, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

-- Likes for post 5
(5, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(5, 3, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, 4, DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- Likes for post 6 (Ask Help)
(6, 1, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(6, 2, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(6, 4, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(6, 5, DATE_SUB(NOW(), INTERVAL 4 HOUR)),

-- Likes for post 7 (Ask Help - popular)
(7, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(7, 2, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(7, 4, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(7, 5, DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(7, 6, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

-- Likes for post 8
(8, 1, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(8, 2, DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(8, 4, DATE_SUB(NOW(), INTERVAL 36 HOUR)),
(8, 5, DATE_SUB(NOW(), INTERVAL 34 HOUR)),
(8, 6, DATE_SUB(NOW(), INTERVAL 32 HOUR)),

-- Likes for post 9 (Share Tip)
(9, 1, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(9, 2, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(9, 4, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(9, 5, DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(9, 6, DATE_SUB(NOW(), INTERVAL 52 HOUR)),

-- Likes for post 10
(10, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(10, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(10, 3, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(10, 5, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(10, 6, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),

-- Likes for post 11
(11, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(11, 2, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(11, 3, DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(11, 4, DATE_SUB(NOW(), INTERVAL 14 HOUR)),
(11, 5, DATE_SUB(NOW(), INTERVAL 12 HOUR)),
(11, 6, DATE_SUB(NOW(), INTERVAL 10 HOUR)),

-- Likes for post 12 (Traffic Alert)
(12, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(12, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(12, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(12, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(12, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

-- Likes for post 13
(13, 1, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(13, 2, DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(13, 3, DATE_SUB(NOW(), INTERVAL 36 HOUR)),
(13, 4, DATE_SUB(NOW(), INTERVAL 34 HOUR)),

-- Likes for post 14 (Video Share Tip)
(14, 1, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(14, 2, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(14, 3, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(14, 4, DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(14, 6, DATE_SUB(NOW(), INTERVAL 52 HOUR)),

-- Likes for post 15 (Ask Help)
(15, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(15, 2, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(15, 3, DATE_SUB(NOW(), INTERVAL 16 HOUR)),
(15, 4, DATE_SUB(NOW(), INTERVAL 14 HOUR)),
(15, 6, DATE_SUB(NOW(), INTERVAL 12 HOUR)),

-- Likes for post 16
(16, 1, DATE_SUB(NOW(), INTERVAL 80 HOUR)),
(16, 2, DATE_SUB(NOW(), INTERVAL 78 HOUR)),
(16, 3, DATE_SUB(NOW(), INTERVAL 76 HOUR)),
(16, 4, DATE_SUB(NOW(), INTERVAL 74 HOUR)),
(16, 5, DATE_SUB(NOW(), INTERVAL 72 HOUR)),

-- Likes for post 17
(17, 1, DATE_SUB(NOW(), INTERVAL 100 HOUR)),
(17, 2, DATE_SUB(NOW(), INTERVAL 98 HOUR)),
(17, 3, DATE_SUB(NOW(), INTERVAL 96 HOUR)),
(17, 4, DATE_SUB(NOW(), INTERVAL 94 HOUR)),
(17, 5, DATE_SUB(NOW(), INTERVAL 92 HOUR)),
(17, 6, DATE_SUB(NOW(), INTERVAL 90 HOUR)),

-- Likes for post 18 (Traffic Alert)
(18, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(18, 2, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(18, 3, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(18, 4, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(18, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(18, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE));

-- Insert sample comments
INSERT IGNORE INTO post_comments (post_id, user_id, content, is_active, created_at) VALUES
-- Comments for post 1
(1, 2, 'Congratulations! Welcome to the Tapri family! 🎉', TRUE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 3, 'Great job! Keep it up!', TRUE, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(1, 4, 'The first delivery is always special!', TRUE, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),

-- Comments for post 2
(2, 1, 'Beautiful view! Where is this?', TRUE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, 3, 'Amazing sunset! 📸', TRUE, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(2, 5, 'Love these scenic routes!', TRUE, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

-- Comments for post 3 (Traffic Alert)
(3, 2, 'Thanks for the alert! Saved me time!', TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 4, 'Appreciate the heads up!', TRUE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 5, 'Good to know, thanks!', TRUE, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

-- Comments for post 4 (Share Tip)
(4, 1, 'Great tip! I always do this too.', TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(4, 3, 'Very helpful advice!', TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4, 5, 'This saved me so much time!', TRUE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- Comments for post 5
(5, 1, 'The people make this job amazing!', TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(5, 3, 'So true!', TRUE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(5, 4, 'Love meeting new people!', TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),

-- Comments for post 6 (Ask Help)
(6, 1, 'Try the parking lot behind the mall!', TRUE, DATE_SUB(NOW(), INTERVAL 7 HOUR)),
(6, 2, 'There\'s usually space on the 3rd floor.', TRUE, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
(6, 4, 'I park near the metro station and walk.', TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),

-- Comments for post 7 (Ask Help - popular)
(7, 1, 'Welcome! Start with shorter routes first.', TRUE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(7, 2, 'Download offline maps!', TRUE, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(7, 4, 'Keep your phone charged!', TRUE, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(7, 5, 'Join local driver groups on WhatsApp.', TRUE, DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(7, 6, 'Don\'t hesitate to ask for help!', TRUE, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

-- Comments for post 8
(8, 1, 'Congratulations on completing your first week!', TRUE, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(8, 2, 'Great milestone! 🎉', TRUE, DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(8, 4, 'Keep up the good work!', TRUE, DATE_SUB(NOW(), INTERVAL 36 HOUR)),

-- Comments for post 9 (Share Tip)
(9, 1, 'This is really helpful!', TRUE, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(9, 2, 'Thanks for sharing your experience!', TRUE, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(9, 4, 'Great insights!', TRUE, DATE_SUB(NOW(), INTERVAL 56 HOUR)),

-- Comments for post 10
(10, 1, 'Rainy days are tough but rewarding!', TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(10, 2, 'Stay safe in the rain!', TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(10, 3, 'Beautiful photo!', TRUE, DATE_SUB(NOW(), INTERVAL 1 HOUR)),

-- Comments for post 11
(11, 1, 'That\'s awesome! Well done!', TRUE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(11, 2, '5-star reviews are the best!', TRUE, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(11, 3, 'Congratulations! 🎉', TRUE, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

-- Comments for post 12 (Traffic Alert)
(12, 1, 'Thanks for the update!', TRUE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(12, 2, 'Good to know!', TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(12, 3, 'Appreciate the alert!', TRUE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),

-- Comments for post 13
(13, 1, 'Weekend deliveries are always busy!', TRUE, DATE_SUB(NOW(), INTERVAL 40 HOUR)),
(13, 2, 'But the tips are usually better!', TRUE, DATE_SUB(NOW(), INTERVAL 38 HOUR)),
(13, 3, 'True!', TRUE, DATE_SUB(NOW(), INTERVAL 36 HOUR)),

-- Comments for post 14 (Video Share Tip)
(14, 1, 'Great video! Very helpful tips.', TRUE, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(14, 2, 'Thanks for sharing!', TRUE, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(14, 3, 'This will help many drivers!', TRUE, DATE_SUB(NOW(), INTERVAL 56 HOUR)),

-- Comments for post 15 (Ask Help)
(15, 1, 'I\'m interested! Which area?', TRUE, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(15, 2, 'Count me in!', TRUE, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(15, 3, 'Let me know the details!', TRUE, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

-- Comments for post 16
(16, 1, 'Welcome to Tapri! 🎉', TRUE, DATE_SUB(NOW(), INTERVAL 80 HOUR)),
(16, 2, 'Great to have you here!', TRUE, DATE_SUB(NOW(), INTERVAL 78 HOUR)),
(16, 3, 'Welcome aboard!', TRUE, DATE_SUB(NOW(), INTERVAL 76 HOUR)),

-- Comments for post 17
(17, 1, 'Congratulations on your first delivery!', TRUE, DATE_SUB(NOW(), INTERVAL 100 HOUR)),
(17, 2, 'Well done! 🎉', TRUE, DATE_SUB(NOW(), INTERVAL 98 HOUR)),
(17, 3, 'Great start!', TRUE, DATE_SUB(NOW(), INTERVAL 96 HOUR)),

-- Comments for post 18 (Traffic Alert)
(18, 1, 'Thanks for the alert!', TRUE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(18, 2, 'Good to know!', TRUE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(18, 3, 'Appreciate the heads up!', TRUE, DATE_SUB(NOW(), INTERVAL 3 HOUR));

-- Insert sample saved posts
INSERT IGNORE INTO saved_posts (user_id, post_id, created_at) VALUES
-- User 1 saved posts
(1, 4, DATE_SUB(NOW(), INTERVAL 2 HOUR)), -- Share tip
(1, 9, DATE_SUB(NOW(), INTERVAL 1 DAY)), -- Route tips
(1, 14, DATE_SUB(NOW(), INTERVAL 2 DAY)), -- Video tips

-- User 2 saved posts
(2, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)), -- First delivery
(2, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)), -- Beginner tips
(2, 12, DATE_SUB(NOW(), INTERVAL 4 HOUR)), -- Traffic alert

-- User 3 saved posts
(3, 4, DATE_SUB(NOW(), INTERVAL 3 HOUR)), -- Share tip
(3, 6, DATE_SUB(NOW(), INTERVAL 7 HOUR)), -- Parking help
(3, 9, DATE_SUB(NOW(), INTERVAL 2 DAY)), -- Route tips

-- User 4 saved posts
(4, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)), -- Traffic alert
(4, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)), -- Beginner tips
(4, 14, DATE_SUB(NOW(), INTERVAL 2 DAY)), -- Video tips

-- User 5 saved posts
(5, 4, DATE_SUB(NOW(), INTERVAL 3 HOUR)), -- Share tip
(5, 9, DATE_SUB(NOW(), INTERVAL 2 DAY)), -- Route tips
(5, 12, DATE_SUB(NOW(), INTERVAL 4 HOUR)), -- Traffic alert

-- User 6 saved posts
(6, 1, DATE_SUB(NOW(), INTERVAL 1 HOUR)), -- First delivery
(6, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)), -- Beginner tips
(6, 14, DATE_SUB(NOW(), INTERVAL 2 DAY)); -- Video tips

-- Insert sample shares
INSERT IGNORE INTO post_shares (post_id, user_id, created_at) VALUES
-- Shares for popular posts
(1, 2, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 3, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

(2, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(2, 4, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(2, 5, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

(3, 2, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(3, 4, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(3, 5, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(3, 6, DATE_SUB(NOW(), INTERVAL 15 MINUTE)),

(4, 1, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(4, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(4, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

(7, 1, DATE_SUB(NOW(), INTERVAL 20 HOUR)),
(7, 2, DATE_SUB(NOW(), INTERVAL 19 HOUR)),
(7, 4, DATE_SUB(NOW(), INTERVAL 18 HOUR)),
(7, 5, DATE_SUB(NOW(), INTERVAL 17 HOUR)),
(7, 6, DATE_SUB(NOW(), INTERVAL 16 HOUR)),

(9, 1, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(9, 2, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(9, 4, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(9, 5, DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(9, 6, DATE_SUB(NOW(), INTERVAL 52 HOUR)),

(12, 1, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(12, 2, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(12, 3, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(12, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(12, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),

(14, 1, DATE_SUB(NOW(), INTERVAL 60 HOUR)),
(14, 2, DATE_SUB(NOW(), INTERVAL 58 HOUR)),
(14, 3, DATE_SUB(NOW(), INTERVAL 56 HOUR)),
(14, 4, DATE_SUB(NOW(), INTERVAL 54 HOUR)),
(14, 6, DATE_SUB(NOW(), INTERVAL 52 HOUR)),

(18, 1, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(18, 2, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(18, 3, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
(18, 4, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(18, 5, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(18, 6, DATE_SUB(NOW(), INTERVAL 30 MINUTE));
