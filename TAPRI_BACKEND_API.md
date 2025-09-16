# Tapri v0 Backend API Documentation

## Overview
Tapri v0 Backend provides a comprehensive community platform for drivers with Instagram-style posts and WhatsApp/Telegram-style group messaging. The backend is built with Spring Boot and includes real-time messaging via WebSockets.

## Features
- **Posts**: Create, like, comment on posts with media support (images, GIFs, videos)
- **Groups**: Create and manage driver groups with admin/member roles
- **Real-time Messaging**: WebSocket-based group chat with typing indicators and reactions
- **Profiles**: User profiles with bio, photo, and privacy settings
- **Authentication**: Phone number-based user system (existing OTP system)

## Database Schema

### Users Table
- `id` (Primary Key)
- `phone_number` (Unique)
- `name`
- `bio`
- `profile_photo_url`
- `last_seen`
- `last_seen_visible`
- `is_active`
- `created_at`
- `updated_at`

### Posts Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `text`
- `media_url`
- `media_type` (IMAGE, GIF, VIDEO)
- `is_active`
- `created_at`
- `updated_at`

### Post Comments Table
- `id` (Primary Key)
- `post_id` (Foreign Key)
- `user_id` (Foreign Key)
- `content`
- `is_active`
- `created_at`
- `updated_at`

### Post Likes Table
- `id` (Primary Key)
- `post_id` (Foreign Key)
- `user_id` (Foreign Key)
- `created_at`

### Groups Table
- `id` (Primary Key)
- `name`
- `description`
- `photo_url`
- `created_by` (Foreign Key)
- `is_active`
- `created_at`
- `updated_at`

### Group Members Table
- `id` (Primary Key)
- `group_id` (Foreign Key)
- `user_id` (Foreign Key)
- `role` (ADMIN, MEMBER)
- `joined_at`

### Group Messages Table
- `id` (Primary Key)
- `group_id` (Foreign Key)
- `user_id` (Foreign Key)
- `content`
- `media_url`
- `media_type` (IMAGE, GIF, VIDEO)
- `is_active`
- `is_edited`
- `created_at`
- `updated_at`

### Message Reactions Table
- `id` (Primary Key)
- `message_id` (Foreign Key)
- `user_id` (Foreign Key)
- `emoji`
- `created_at`

## Seeded Test Data

### Sample Users
1. **Ramesh Kumar** - +919876543210
   - Bio: "Professional driver with 5+ years experience"
   - Avatar: https://example.com/avatars/ramesh.jpg

2. **Suresh Singh** - +919876543211
   - Bio: "City driver, always on time!"
   - Avatar: https://example.com/avatars/suresh.jpg

3. **Priya Sharma** - +919876543212
   - Bio: "Safe and reliable driver"
   - Avatar: https://example.com/avatars/priya.jpg

4. **Amit Patel** - +919876543213
   - Bio: "Experienced in long distance travel"
   - Avatar: https://example.com/avatars/amit.jpg

5. **Deepika Reddy** - +919876543214
   - Bio: "Friendly driver, great with customers"
   - Avatar: https://example.com/avatars/deepika.jpg

6. **Vikram Joshi** - +919876543215
   - Bio: "Professional and punctual"
   - Avatar: https://example.com/avatars/vikram.jpg

### Sample Groups
1. **Delhi Drivers** - Created by Ramesh Kumar
   - Members: Ramesh (Admin), Suresh, Priya, Deepika
   - Description: "Professional drivers in Delhi area"

2. **Mumbai Rides** - Created by Amit Patel
   - Members: Amit (Admin), Vikram, Ramesh
   - Description: "Mumbai city drivers community"

## REST API Endpoints

### Posts API

#### Get All Posts (with pagination)
```http
GET /api/posts?page=0&size=10
Headers: X-User-Id: 1
```

#### Get All Posts (without pagination)
```http
GET /api/posts/all
Headers: X-User-Id: 1
```

#### Get Post by ID
```http
GET /api/posts/{id}
Headers: X-User-Id: 1
```

#### Create Post
```http
POST /api/posts
Headers: X-User-Id: 1
Content-Type: application/json

{
  "text": "Heavy traffic on MG Road today!",
  "mediaUrl": "https://example.com/images/traffic.jpg",
  "mediaType": "IMAGE"
}
```

#### Like/Unlike Post
```http
POST /api/posts/{id}/like
Headers: X-User-Id: 1
```

#### Add Comment
```http
POST /api/posts/{id}/comment
Headers: X-User-Id: 1
Content-Type: application/json

{
  "content": "Thanks for the update!"
}
```

#### Delete Post
```http
DELETE /api/posts/{id}
Headers: X-User-Id: 1
```

#### Delete Comment
```http
DELETE /api/posts/comments/{commentId}
Headers: X-User-Id: 1
```

### Groups API

#### Get User's Groups
```http
GET /api/groups
Headers: X-User-Id: 1
```

#### Create Group
```http
POST /api/groups
Headers: X-User-Id: 1
Content-Type: application/json

{
  "name": "Delhi Drivers",
  "description": "Professional drivers in Delhi area",
  "photoUrl": "https://example.com/groups/delhi.jpg"
}
```

#### Join Group
```http
POST /api/groups/{id}/join
Headers: X-User-Id: 1
```

#### Leave Group
```http
POST /api/groups/{id}/leave
Headers: X-User-Id: 1
```

#### Get Group Members
```http
GET /api/groups/{id}/members
Headers: X-User-Id: 1
```

#### Update Group
```http
PUT /api/groups/{id}
Headers: X-User-Id: 1
Content-Type: application/json

{
  "name": "Updated Group Name",
  "description": "Updated description"
}
```

#### Delete Group
```http
DELETE /api/groups/{id}
Headers: X-User-Id: 1
```

### Chat API

#### Get Group Messages
```http
GET /api/chat/groups/{groupId}/messages
Headers: X-User-Id: 1
```

#### Send Message
```http
POST /api/chat/groups/{groupId}/send
Headers: X-User-Id: 1
Content-Type: application/json

{
  "content": "Hello everyone!",
  "mediaUrl": "https://example.com/images/photo.jpg",
  "mediaType": "IMAGE"
}
```

#### Add Message Reaction
```http
POST /api/chat/messages/{messageId}/reaction
Headers: X-User-Id: 1
Content-Type: application/json

{
  "emoji": "👍"
}
```

#### Remove Message Reaction
```http
DELETE /api/chat/messages/{messageId}/reaction
Headers: X-User-Id: 1
```

#### Edit Message
```http
PUT /api/chat/messages/{messageId}
Headers: X-User-Id: 1
Content-Type: application/json

{
  "content": "Updated message content"
}
```

#### Delete Message
```http
DELETE /api/chat/messages/{messageId}
Headers: X-User-Id: 1
```

#### Send Typing Indicator
```http
POST /api/chat/groups/{groupId}/typing
Headers: X-User-Id: 1
Content-Type: application/json

{
  "isTyping": true
}
```

### Profile API

#### Get User Profile
```http
GET /api/profile
Headers: X-User-Id: 1
```

#### Update Profile
```http
PUT /api/profile
Headers: X-User-Id: 1
Content-Type: application/json

{
  "name": "Updated Name",
  "bio": "Updated bio",
  "profilePhotoUrl": "https://example.com/new-photo.jpg",
  "lastSeenVisible": true
}
```

#### Update Last Seen
```http
POST /api/profile/last-seen
Headers: X-User-Id: 1
```

#### Search Users
```http
GET /api/profile/search?query=ramesh
```

#### Get Online Users
```http
GET /api/profile/online
```

## WebSocket Endpoints

### Connection
```javascript
// Connect to WebSocket
const socket = new SockJS('/ws/chat');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to group messages
    stompClient.subscribe('/topic/groups/1', function(message) {
        const messageData = JSON.parse(message.body);
        console.log('New message:', messageData);
    });
    
    // Subscribe to typing indicators
    stompClient.subscribe('/topic/groups/1/typing', function(typing) {
        const typingData = JSON.parse(typing.body);
        console.log('Typing indicator:', typingData);
    });
});
```

### Send Message via WebSocket
```javascript
// Send message
stompClient.send("/app/groups/1/send", {}, JSON.stringify({
    content: "Hello from WebSocket!",
    mediaUrl: null,
    mediaType: null
}));
```

### Send Typing Indicator via WebSocket
```javascript
// Send typing indicator
stompClient.send("/app/groups/1/typing", {}, JSON.stringify({
    isTyping: true
}));
```

## Example cURL Requests

### Create Post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "text": "Heavy traffic on MG Road today!",
    "mediaUrl": "https://example.com/images/traffic.jpg",
    "mediaType": "IMAGE"
  }'
```

### Like Post
```bash
curl -X POST http://localhost:8080/api/posts/1/like \
  -H "X-User-Id: 2"
```

### Join Group
```bash
curl -X POST http://localhost:8080/api/groups/1/join \
  -H "X-User-Id: 2"
```

### Send Message
```bash
curl -X POST http://localhost:8080/api/chat/groups/1/send \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "content": "Hello everyone!",
    "mediaUrl": null,
    "mediaType": null
  }'
```

### Send Typing Indicator
```bash
curl -X POST http://localhost:8080/api/chat/groups/1/typing \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "isTyping": true
  }'
```

## Testing WebSockets

### Using JavaScript (Browser)
```html
<!DOCTYPE html>
<html>
<head>
    <title>Tapri WebSocket Test</title>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
</head>
<body>
    <div id="messages"></div>
    <input type="text" id="messageInput" placeholder="Type a message...">
    <button onclick="sendMessage()">Send</button>
    
    <script>
        const socket = new SockJS('/ws/chat');
        const stompClient = Stomp.over(socket);
        
        stompClient.connect({}, function(frame) {
            console.log('Connected: ' + frame);
            
            // Subscribe to group messages
            stompClient.subscribe('/topic/groups/1', function(message) {
                const messageData = JSON.parse(message.body);
                document.getElementById('messages').innerHTML += 
                    '<div>' + messageData.user.name + ': ' + messageData.content + '</div>';
            });
        });
        
        function sendMessage() {
            const messageInput = document.getElementById('messageInput');
            const message = messageInput.value;
            
            stompClient.send("/app/groups/1/send", {}, JSON.stringify({
                content: message,
                mediaUrl: null,
                mediaType: null
            }));
            
            messageInput.value = '';
        }
    </script>
</body>
</html>
```

### Using Postman
1. Create a new WebSocket request
2. URL: `ws://localhost:8080/ws/chat`
3. Send connection message: `CONNECT\naccept-version:1.1,1.0\nheart-beat:10000,10000\n\n\x00`
4. Subscribe to messages: `SUBSCRIBE\nid:sub-0\ndestination:/topic/groups/1\n\n\x00`
5. Send message: `SEND\ndestination:/app/groups/1/send\ncontent-type:application/json\n\n{"content":"Hello from Postman!"}\x00`

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Request successful
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - User not authenticated
- `403 Forbidden` - User not authorized for action
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource (e.g., already liked post)
- `500 Internal Server Error` - Server error

### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "User is not a member of this group",
  "path": "/api/chat/groups/1/send"
}
```

## Security Considerations

1. **Authentication**: All endpoints require `X-User-Id` header for user identification
2. **Authorization**: Users can only perform actions they're authorized for
3. **Input Validation**: All input is validated before processing
4. **CORS**: Configured to allow cross-origin requests for development
5. **WebSocket Security**: WebSocket connections should be secured in production

## Deployment Notes

1. **Database**: Configure your database connection in `application.properties`
2. **WebSocket**: Ensure your load balancer supports WebSocket connections
3. **CORS**: Update CORS configuration for production domains
4. **Authentication**: Implement proper JWT or session-based authentication
5. **File Upload**: Configure file storage for media uploads

## Development Setup

1. **Prerequisites**: Java 17+, Maven, MySQL/PostgreSQL
2. **Database**: Create database and update connection properties
3. **Run**: `mvn spring-boot:run`
4. **Test**: Use the provided cURL commands or WebSocket test page

## API Rate Limiting

Consider implementing rate limiting for:
- Post creation (e.g., 10 posts per hour)
- Message sending (e.g., 100 messages per hour)
- Like/comment actions (e.g., 1000 actions per hour)

## Monitoring and Logging

- All API calls are logged
- WebSocket connections are tracked
- Database queries are monitored
- Error rates are tracked

---

**Note**: This is the v0 release focusing on community features. Earnings and Info modules are disabled and replaced with "Coming Soon" placeholders in the frontend.
