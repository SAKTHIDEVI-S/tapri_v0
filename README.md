# Tapri Backend + Android App

## 🚀 Tapri v0 Release Notes

**Tapri v0 release includes community features only. Earnings and Info sections are hidden and replaced by Coming Soon placeholders. Profile simplified.**

### What's Available in v0:
- ✅ **Community Features**: Posts, groups, messages, chat
- ✅ **Groups Tab**: Create groups, discover and join groups, communicate
- ✅ **Profile**: Core driver info, settings, logout (earnings sections removed)
- ✅ **Home Screen**: Community post feed (earnings card and toggle removed)

### What's Coming Soon:
- 🚀 **Earnings Section**: Beautiful Coming Soon screen with animations
- 🚀 **Info Section**: Coming Soon placeholder with consistent styling
- 🚀 **My Apps**: Will be added to profile in future releases

### Feature Flags:
All disabled features use feature flags in `FeatureFlags.kt` for easy re-enabling:
- `EARNINGS_FEATURES = false`
- `INFO_FEATURES = false`
- `SHOW_COMING_SOON_EARN = true`
- `SHOW_COMING_SOON_INFO = true`

## Backend (Spring Boot + MySQL)

### Community Module (v0 Active)
- **Posts API**: Create, like, comment on posts with media support (images, GIFs, videos)
- **Groups API**: Create and manage driver groups with admin/member roles
- **Real-time Messaging**: WebSocket-based group chat with typing indicators and reactions
- **Profiles API**: User profiles with bio, photo, and privacy settings
- **Database**: MySQL 8 with comprehensive schema for community features
- **Seeded Data**: 6 sample users, 2 groups, posts, comments, likes, and messages

### Legacy Features (Disabled in v0)
- DB: MySQL 8 (local) – jdbc:mysql://localhost:3306/tapri
- Migrations: Flyway (V1..V7)
- Auth: OTP-based, JWT protected
- Earn: Jobs listing, job claim, submit proof (disabled in v0)

### Run backend
- From IDE (Eclipse/IntelliJ): run `com.tapri.TapriApplication`
- Ensure MySQL is running; app auto-applies migrations

### Test Auth with curl
1) Signup
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","phone":"+911234567890","city":"Pune","state":"MH"}'
```
2) Login - Send OTP
```bash
curl -X POST http://localhost:8080/api/auth/otp \
  -H "Content-Type: application/json" \
  -d '{"phone":"+911234567890","purpose":"login"}'
```
(OTP prints in backend console)

3) Verify OTP
```bash
curl -X POST http://localhost:8080/api/auth/verify \
  -H "Content-Type: application/json" \
  -d '{"phone":"+911234567890","code":"123456"}'
```
Response contains `jwt` and `user` (or `needsSignup` for new users).

### Test Community APIs with curl

#### Posts
```bash
# Get all posts
curl -H "X-User-Id: 1" http://localhost:8080/api/posts/all

# Create post
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"text":"Heavy traffic on MG Road!","mediaUrl":"https://example.com/traffic.jpg","mediaType":"IMAGE"}'

# Like post
curl -X POST http://localhost:8080/api/posts/1/like \
  -H "X-User-Id: 2"
```

#### Groups
```bash
# Get user's groups
curl -H "X-User-Id: 1" http://localhost:8080/api/groups

# Create group
curl -X POST http://localhost:8080/api/groups \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"name":"Delhi Drivers","description":"Professional drivers in Delhi"}'

# Join group
curl -X POST http://localhost:8080/api/groups/1/join \
  -H "X-User-Id: 2"
```

#### Chat
```bash
# Send message
curl -X POST http://localhost:8080/api/chat/groups/1/send \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"content":"Hello everyone!"}'

# Send typing indicator
curl -X POST http://localhost:8080/api/chat/groups/1/typing \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"isTyping":true}'
```

### Test Legacy Earn flow with curl (Disabled in v0)
1) List jobs
```bash
curl http://localhost:8080/api/earn/jobs
```
2) Job detail
```bash
curl http://localhost:8080/api/earn/jobs/1
```
3) Claim a job (requires JWT)
```bash
curl -X POST http://localhost:8080/api/earn/jobs/1/claim \
  -H "Authorization: Bearer YOUR_JWT"
```
4) Ongoing claim
```bash
curl http://localhost:8080/api/earn/claims/ongoing \
  -H "Authorization: Bearer YOUR_JWT"
```
5) Submit proof
```bash
curl -X POST http://localhost:8080/api/earn/claims/CLAIM_ID/submit \
  -H "Authorization: Bearer YOUR_JWT" \
  -H "Content-Type: application/json" \
  -d '{"proofUrl":"http://example.com/photo.jpg","notes":"done"}'
```

## Android App
- Emulator base URL: `http://10.0.2.2:8080/api/auth/`
- Physical device base URL: set to your PC IP (e.g., `http://192.168.1.2:8080/api/auth/`)
- Cleartext HTTP permitted in `network_security_config.xml`

### Run
- In Android Studio: build and run on emulator or device
- Flow: Signup → Login (OTP) → Home → Community (posts, groups, chat)

## 📚 API Documentation

For comprehensive API documentation including:
- Complete endpoint reference
- WebSocket implementation details
- Database schema
- Sample requests and responses
- Testing instructions

See: **[TAPRI_BACKEND_API.md](TAPRI_BACKEND_API.md)**

## 🔧 WebSocket Testing

Test real-time messaging with the provided HTML test page or use the WebSocket endpoints:
- Connect: `ws://localhost:8080/ws/chat`
- Subscribe: `/topic/groups/{groupId}`
- Send: `/app/groups/{groupId}/send`
- Typing: `/app/groups/{groupId}/typing` 