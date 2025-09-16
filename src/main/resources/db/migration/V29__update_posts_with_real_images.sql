-- Update posts with real uploaded images
-- This migration updates the existing posts to use actual uploaded images

-- Update posts that have example.com URLs to use real uploaded images
UPDATE posts 
SET media_url = '/api/images/posts/00b4db9a-1666-42c9-913e-8d22587fdcae.jpg'
WHERE media_url = 'https://example.com/post1.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/6e67a119-506e-457a-92fd-c6fe2a8cec6f.jpg'
WHERE media_url = 'https://example.com/sunset.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/fa8da67f-791e-4c12-b254-f70167c8237c.jpg'
WHERE media_url = 'https://example.com/people.jpg';

-- Update other example URLs to use the available images (cycling through them)
UPDATE posts 
SET media_url = '/api/images/posts/00b4db9a-1666-42c9-913e-8d22587fdcae.jpg'
WHERE media_url = 'https://example.com/week1.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/6e67a119-506e-457a-92fd-c6fe2a8cec6f.jpg'
WHERE media_url = 'https://example.com/routes.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/fa8da67f-791e-4c12-b254-f70167c8237c.jpg'
WHERE media_url = 'https://example.com/rainy.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/00b4db9a-1666-42c9-913e-8d22587fdcae.jpg'
WHERE media_url = 'https://example.com/weekend.jpg';

UPDATE posts 
SET media_url = '/api/images/posts/6e67a119-506e-457a-92fd-c6fe2a8cec6f.jpg'
WHERE media_url = 'https://example.com/welcome.jpg';

-- Keep video URLs as they are (they might be valid)
-- UPDATE posts SET media_url = '/api/images/posts/sample_video.mp4' WHERE media_url = 'https://example.com/customer_tips.mp4';
