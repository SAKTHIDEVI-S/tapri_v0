-- Seed groups data for demo
-- First ensure we have users to create groups and add as members
INSERT IGNORE INTO `users` (id, phone, name, city, is_active, created_at, updated_at, last_seen_visible) VALUES
(1, '9999999999', 'Admin User', 'Bangalore', true, NOW(), NOW(), true),
(2, '8888888888', 'User Two', 'Delhi', true, NOW(), NOW(), true),
(3, '7777777777', 'User Three', 'Mumbai', true, NOW(), NOW(), true),
(4, '6666666666', 'User Four', 'Chennai', true, NOW(), NOW(), true),
(5, '5555555555', 'User Five', 'Hyderabad', true, NOW(), NOW(), true);

INSERT INTO `groups` (id, name, description, photo_url, is_active, created_at, updated_at, created_by) VALUES
(1, 'Bangalore Traffic Updates', 'Real-time traffic updates for Bangalore city', 'https://example.com/traffic.jpg', true, NOW(), NOW(), 1),
(2, 'Delhi Metro Users', 'Delhi Metro updates and discussions', 'https://example.com/metro.jpg', true, NOW(), NOW(), 1),
(3, 'Mumbai Local Train', 'Mumbai local train updates and alerts', 'https://example.com/train.jpg', true, NOW(), NOW(), 1),
(4, 'Chennai Auto Drivers', 'Chennai auto rickshaw drivers community', 'https://example.com/auto.jpg', true, NOW(), NOW(), 1),
(5, 'Hyderabad Bus Routes', 'Hyderabad bus route information and updates', 'https://example.com/bus.jpg', true, NOW(), NOW(), 1),
(6, 'Pune Traffic Police', 'Official traffic updates from Pune traffic police', 'https://example.com/police.jpg', true, NOW(), NOW(), 1),
(7, 'Kolkata Yellow Taxi', 'Kolkata yellow taxi drivers group', 'https://example.com/taxi.jpg', true, NOW(), NOW(), 1),
(8, 'Ahmedabad BRTS', 'Ahmedabad BRTS updates and information', 'https://example.com/brts.jpg', true, NOW(), NOW(), 1),
(9, 'Kochi Water Metro', 'Kochi Water Metro updates and schedules', 'https://example.com/water.jpg', true, NOW(), NOW(), 1),
(10, 'Indore City Bus', 'Indore city bus routes and schedules', 'https://example.com/citybus.jpg', true, NOW(), NOW(), 1);

-- Add some group members (assuming user IDs 1-5 exist)
INSERT INTO `group_members` (id, group_id, user_id, role, joined_at) VALUES
(1, 1, 1, 'ADMIN', NOW()),
(2, 1, 2, 'MEMBER', NOW()),
(3, 1, 3, 'MEMBER', NOW()),
(4, 2, 1, 'ADMIN', NOW()),
(5, 2, 4, 'MEMBER', NOW()),
(6, 3, 2, 'ADMIN', NOW()),
(7, 3, 5, 'MEMBER', NOW()),
(8, 4, 3, 'ADMIN', NOW()),
(9, 5, 4, 'ADMIN', NOW()),
(10, 6, 5, 'ADMIN', NOW());

-- Add some sample group messages
INSERT INTO `group_messages` (id, group_id, user_id, content, media_url, media_type, is_active, created_at, updated_at) VALUES
(1, 1, 1, 'Welcome to Bangalore Traffic Updates! Share real-time traffic conditions here.', null, null, true, NOW(), NOW()),
(2, 1, 2, 'Heavy traffic on MG Road towards Brigade Road. Avoid if possible.', null, null, true, NOW(), NOW()),
(3, 1, 3, 'ORR is clear from Silk Board to Marathahalli', null, null, true, NOW(), NOW()),
(4, 2, 1, 'Welcome to Delhi Metro Users group! Share metro updates and experiences.', null, null, true, NOW(), NOW()),
(5, 2, 4, 'Yellow Line delayed by 10 minutes due to technical issues', null, null, true, NOW(), NOW()),
(6, 3, 2, 'Welcome to Mumbai Local Train updates! Stay informed about train schedules.', null, null, true, NOW(), NOW()),
(7, 3, 5, 'Western Line running on time', null, null, true, NOW(), NOW()),
(8, 4, 3, 'Chennai Auto Drivers - Share fare updates and route information', null, null, true, NOW(), NOW()),
(9, 5, 4, 'Hyderabad Bus Routes - Find the best bus routes across the city', null, null, true, NOW(), NOW()),
(10, 6, 5, 'Official Pune Traffic Police updates - Drive safe!', null, null, true, NOW(), NOW());

-- Note: members_count is calculated dynamically from group_members table
-- No need to update it manually
