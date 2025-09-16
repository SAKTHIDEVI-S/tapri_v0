# 📱 Tapri v0 - Posts API Documentation

## 🎯 Overview
This document describes the Posts API for Tapri v0 Community module, designed to match the frontend requirements for Instagram-style posts with WhatsApp/Telegram-style interactions.

## 🗄️ Database Schema

### Core Tables
- **`posts`** - Main posts table with text, media, post type, and audience settings
- **`post_likes`** - User likes on posts
- **`post_comments`** - Comments on posts
- **`post_shares`** - Post sharing tracking
- **`saved_posts`** - User saved posts
- **`users`** - Extended user table with profile information

### Post Types
- `GENERAL` - Regular posts
- `TRAFFIC_ALERT` - Traffic alerts (from quick buttons)
- `ASK_HELP` - Help requests (from quick buttons)
- `SHARE_TIP` - Tips sharing (from quick buttons)

### Audience Types
- `EVERYONE` - Public posts
- `GROUPS` - Group-specific posts (for future use)

## 🚀 API Endpoints

### 1. Get Posts Feed
**GET** `/api/posts/feed`

Returns posts in frontend-compatible format with user interactions.

**Headers:**
```
X-User-Id: 1
```

**Response:**
```json
[
  {
    "id": 1,
    "userName": "Ramesh Kumar",
    "userAvatar": "/uploads/profiles/avatar1.jpg",
    "postTime": "2 hours ago",
    "caption": "Just completed my first delivery! The customer was so happy with the service. #TapriLife",
    "mediaUrl": "https://example.com/post1.jpg",
    "mediaType": "IMAGE",
    "postType": "GENERAL",
    "likeCount": 3,
    "commentCount": 3,
    "shareCount": 5,
    "isLiked": false,
    "isSaved": false
  }
]
```

### 2. Create Post
**POST** `/api/posts`

Create a new post with text and optional media.

**Headers:**
```
X-User-Id: 1
Content-Type: application/json
```

**Request Body:**
```json
{
  "text": "Beautiful sunset from my delivery route!",
  "mediaUrl": "https://example.com/sunset.jpg",
  "mediaType": "IMAGE",
  "postType": "GENERAL",
  "audience": "EVERYONE"
}
```

**Response:**
```json
{
  "id": 19,
  "userId": 1,
  "text": "Beautiful sunset from my delivery route!",
  "mediaUrl": "https://example.com/sunset.jpg",
  "mediaType": "IMAGE",
  "postType": "GENERAL",
  "audience": "EVERYONE",
  "likeCount": 0,
  "commentCount": 0,
  "shareCount": 0,
  "isLiked": false,
  "createdAt": "2025-01-10T15:30:00",
  "user": {
    "id": 1,
    "name": "Ramesh Kumar",
    "phoneNumber": "+919876543210"
  }
}
```

### 3. Create Post with Image Upload
**POST** `/api/posts/with-image`

Create a post with image upload.

**Headers:**
```
X-User-Id: 1
Content-Type: multipart/form-data
```

**Form Data:**
- `text`: Post content
- `image`: Image file (optional)

**Response:**
```json
{
  "success": true,
  "post": {
    "id": 20,
    "userId": 1,
    "text": "My delivery today!",
    "mediaUrl": "/uploads/posts/image_20250110_153000.jpg",
    "mediaType": "IMAGE",
    "likeCount": 0,
    "commentCount": 0,
    "shareCount": 0,
    "isLiked": false
  },
  "message": "Post created successfully"
}
```

### 4. Like/Unlike Post
**POST** `/api/posts/{id}/like`

Toggle like status for a post.

**Headers:**
```
X-User-Id: 1
```

**Response:**
```json
{
  "success": true,
  "isLiked": true,
  "likeCount": 4,
  "message": "Post liked"
}
```

### 5. Comment on Post
**POST** `/api/posts/{id}/comment`

Add a comment to a post.

**Headers:**
```
X-User-Id: 1
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "Great post! Keep it up! 👍"
}
```

**Response:**
```json
{
  "success": true,
  "comment": {
    "id": 15,
    "content": "Great post! Keep it up! 👍",
    "createdAt": "2025-01-10T15:35:00",
    "user": {
      "id": 1,
      "name": "Ramesh Kumar"
    }
  },
  "message": "Comment added successfully"
}
```

### 6. Save/Unsave Post
**POST** `/api/posts/{id}/save`

Toggle save status for a post.

**Headers:**
```
X-User-Id: 1
```

**Response:**
```json
{
  "success": true,
  "isSaved": true,
  "message": "Post saved"
}
```

### 7. Share Post
**POST** `/api/posts/{id}/share`

Share a post (increments share count).

**Headers:**
```
X-User-Id: 1
```

**Response:**
```json
{
  "success": true,
  "isShared": true,
  "message": "Post shared"
}
```

### 8. Get Post Details
**GET** `/api/posts/{id}`

Get detailed post information with comments.

**Headers:**
```
X-User-Id: 1
```

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "text": "Just completed my first delivery!",
  "mediaUrl": "https://example.com/post1.jpg",
  "mediaType": "IMAGE",
  "postType": "GENERAL",
  "audience": "EVERYONE",
  "likeCount": 3,
  "commentCount": 3,
  "shareCount": 5,
  "isLiked": false,
  "createdAt": "2025-01-10T13:30:00",
  "user": {
    "id": 1,
    "name": "Ramesh Kumar",
    "phoneNumber": "+919876543210"
  },
  "comments": [
    {
      "id": 1,
      "content": "Congratulations! Welcome to the Tapri family! 🎉",
      "createdAt": "2025-01-10T14:30:00",
      "user": {
        "id": 2,
        "name": "Priya Sharma"
      }
    }
  ]
}
```

## 🧪 Test Data

### Sample Users
| ID | Name | Phone | City |
|----|------|-------|------|
| 1 | Ramesh Kumar | +919876543210 | Mumbai |
| 2 | Priya Sharma | +919876543211 | Delhi |
| 3 | Amit Singh | +919876543212 | Bangalore |
| 4 | Sunita Patel | +919876543213 | Pune |
| 5 | Rajesh Gupta | +919876543214 | Chennai |
| 6 | Kavita Reddy | +919876543215 | Hyderabad |

### Sample Posts
- **18 posts** with various types (General, Traffic Alert, Ask Help, Share Tip)
- **Realistic engagement** with likes, comments, and shares
- **Time-based content** (recent to 5 days old)
- **Mixed media** (text-only, images, videos)

## 🔧 cURL Examples

### Get Posts Feed
```bash
curl -X GET "http://localhost:8080/api/posts/feed" \
  -H "X-User-Id: 1"
```

### Create Post
```bash
curl -X POST "http://localhost:8080/api/posts" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "text": "Just completed a great delivery!",
    "postType": "GENERAL",
    "audience": "EVERYONE"
  }'
```

### Like Post
```bash
curl -X POST "http://localhost:8080/api/posts/1/like" \
  -H "X-User-Id: 1"
```

### Comment on Post
```bash
curl -X POST "http://localhost:8080/api/posts/1/comment" \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Great post! Keep it up! 👍"
  }'
```

### Save Post
```bash
curl -X POST "http://localhost:8080/api/posts/1/save" \
  -H "X-User-Id: 1"
```

### Share Post
```bash
curl -X POST "http://localhost:8080/api/posts/1/share" \
  -H "X-User-Id: 1"
```

## 🎨 Frontend Integration

### Post Feed Display
The `/api/posts/feed` endpoint returns data in the exact format expected by the frontend:

```kotlin
data class Post(
    val id: String,
    val userName: String,
    val postTime: String,
    val caption: String,
    val mediaUrl: String?,
    val mediaType: MediaType = MediaType.IMAGE,
    var likeCount: Int,
    var commentCount: Int,
    var shareCount: Int,
    var isLiked: Boolean = false,
    var isSaved: Boolean = false
)
```

### Create Post Integration
The frontend can create posts using the quick buttons:

```kotlin
// Traffic Alert
val request = CreatePostRequest(
    text = "Heavy traffic on MG Road!",
    postType = "TRAFFIC_ALERT",
    audience = "EVERYONE"
)

// Ask Help
val request = CreatePostRequest(
    text = "Need help with parking near City Mall",
    postType = "ASK_HELP",
    audience = "EVERYONE"
)

// Share Tip
val request = CreatePostRequest(
    text = "Always double-check addresses before delivery",
    postType = "SHARE_TIP",
    audience = "EVERYONE"
)
```

## 🔄 Real-time Features (Future)
- WebSocket support for real-time likes/comments
- Push notifications for interactions
- Live post updates

## 🚀 Getting Started

1. **Start the backend server:**
   ```bash
   cd src
   mvn spring-boot:run
   ```

2. **Test the API:**
   ```bash
   curl -X GET "http://localhost:8080/api/posts/feed" -H "X-User-Id: 1"
   ```

3. **View sample data:**
   The database is automatically seeded with 18 sample posts, realistic engagement data, and 6 test users.

## 📱 Android Integration

The Android app can now:
- ✅ Display posts feed with user avatars and relative timestamps
- ✅ Show like, comment, share, and save counts
- ✅ Handle different post types (Traffic Alert, Ask Help, Share Tip)
- ✅ Create posts with text and media
- ✅ Like, comment, save, and share posts
- ✅ Display user interactions (isLiked, isSaved)

## 🎯 Next Steps
- [ ] Implement WebSocket for real-time updates
- [ ] Add push notifications
- [ ] Implement post search and filtering
- [ ] Add post reporting and moderation
- [ ] Implement user following system
