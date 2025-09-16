# WebSocket Setup Instructions

## Current Status
The WebSocket functionality is currently **disabled** due to dependency resolution issues. The backend will work perfectly for all REST API endpoints, but real-time messaging features are temporarily disabled.

## What's Working
✅ **All REST APIs** - Posts, Groups, Chat, Profile endpoints work perfectly  
✅ **Database operations** - All CRUD operations function normally  
✅ **Message storage** - Messages are saved and retrieved via REST API  
✅ **Group management** - Create, join, leave groups via REST API  

## What's Temporarily Disabled
❌ **Real-time messaging** - WebSocket connections for live chat  
❌ **Typing indicators** - Real-time typing status updates  
❌ **Live message updates** - Messages appear only after page refresh  

## To Enable WebSocket Functionality

### 1. Rebuild the Project
After adding the WebSocket dependency to `pom.xml`, rebuild the project:
```bash
mvn clean install
```

### 2. Uncomment WebSocket Code
Once dependencies are resolved, uncomment the following files:

#### `src/main/java/com/tapri/config/WebSocketConfig.java`
- Remove the `//` comments from the class annotations
- Uncomment all the configuration methods

#### `src/main/java/com/tapri/controller/WebSocketController.java`
- Uncomment the `@Autowired` ChatService injection
- Uncomment all the `@MessageMapping` methods

#### `src/main/java/com/tapri/service/ChatService.java`
- Uncomment the `SimpMessagingTemplate` injection
- Uncomment all the `messagingTemplate.convertAndSend()` calls

### 3. Test WebSocket Connection
Use the provided `websocket-test.html` file to test real-time messaging.

## Alternative: Polling-Based Chat
If WebSocket setup is complex, you can implement polling-based chat by:

1. **Frontend polling** - Call `/api/chat/groups/{groupId}/messages` every 2-3 seconds
2. **Message timestamps** - Only fetch messages newer than the last received timestamp
3. **Typing indicators** - Use a separate endpoint to check typing status

## Current API Endpoints (All Working)
```
GET    /api/posts/all                    - Get all posts
POST   /api/posts                        - Create post
POST   /api/posts/{id}/like              - Like/unlike post
POST   /api/posts/{id}/comment           - Add comment

GET    /api/groups                       - Get user's groups
POST   /api/groups                       - Create group
POST   /api/groups/{id}/join             - Join group
POST   /api/groups/{id}/leave            - Leave group

GET    /api/chat/groups/{id}/messages    - Get group messages
POST   /api/chat/groups/{id}/send        - Send message
POST   /api/chat/messages/{id}/reaction  - Add reaction
PUT    /api/chat/messages/{id}           - Edit message
DELETE /api/chat/messages/{id}           - Delete message

GET    /api/profile                      - Get user profile
PUT    /api/profile                      - Update profile
```

## Testing Without WebSocket
You can test all functionality using the provided cURL commands in the main README.md file. The chat will work perfectly - users just need to refresh to see new messages instead of getting them in real-time.

## Next Steps
1. **Immediate**: Use REST APIs for all functionality
2. **Short-term**: Implement polling-based chat for better UX
3. **Long-term**: Enable WebSocket for real-time messaging

The backend is fully functional and ready for production use with REST APIs!
