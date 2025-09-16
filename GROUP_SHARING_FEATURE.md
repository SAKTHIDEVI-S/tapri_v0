# Group Sharing Feature

## Overview
The group sharing feature allows users to share posts directly to their Tapri groups, in addition to the existing external app sharing functionality.

## Features

### 1. Enhanced Share Options
When users click the share button on any post, they now see two options:
- **"Share to Group"** - Share directly to Tapri groups
- **"Share via Apps"** - Share via external apps (WhatsApp, Instagram, etc.)

### 2. Group Selection Dialog
- Clean, modern UI for selecting groups
- Shows all groups the user is a member of
- Displays group name, member count, and avatar
- Single-selection with visual feedback
- Loading states and error handling

### 3. Backend Integration
- New API endpoint: `POST /api/posts/{id}/share-to-group`
- Validates user membership in target group
- Creates group message with post content
- Increments post share count automatically

## Technical Implementation

### Backend Changes

#### PostController.java
```java
@PostMapping("/{id}/share-to-group")
public ResponseEntity<Map<String, Object>> sharePostToGroup(
        @PathVariable Long id,
        @RequestBody ShareToGroupRequest request,
        HttpServletRequest httpRequest)
```

#### PostService.java
```java
public boolean sharePostToGroup(Long postId, Long groupId, Long userId) {
    // Validates user membership
    // Creates GroupMessage with post content
    // Increments share count
}
```

### Android Changes

#### Enhanced Share Flow
```kotlin
private fun handleSharePost(post: Post) {
    val shareOptions = arrayOf("Share to Group", "Share via Apps")
    AlertDialog.Builder(this)
        .setTitle("Share Post")
        .setItems(shareOptions) { _, which ->
            when (which) {
                0 -> showGroupSelectionDialog(post)
                1 -> shareViaSystemApps(post)
            }
        }
        .show()
}
```

#### New Components
- **ShareToGroupDialog.kt**: Full-featured dialog with group selection
- **GroupSelectionAdapter.kt**: RecyclerView adapter for group list
- **GroupsApi.kt**: API interface for fetching user's groups

## User Experience

### How It Works
1. User clicks share button on any post
2. Dialog appears with two options:
   - "Share to Group" 
   - "Share via Apps"
3. If "Share to Group" selected:
   - Group selection dialog opens
   - Shows all user's groups with member counts
   - User selects a group and clicks "Share"
   - Post is shared as a message in that group
   - Share count is incremented
4. If "Share via Apps" selected:
   - Traditional system share sheet opens
   - WhatsApp, Instagram, etc. options appear

### Visual Design
- Clean interface with modern group selection
- Group avatars with fallback icons
- Member count display
- Selection feedback with checkmarks
- Smooth loading animations

## Security & Validation

### Backend Validation
- User authentication via JWT token
- Group membership validation
- Post existence and activity checks
- Comprehensive error handling

### Frontend Validation
- Network error handling
- Loading states with user feedback
- Input validation to prevent invalid selections

## API Endpoints

### Share Post to Group
```
POST /api/posts/{id}/share-to-group
Headers: Authorization: Bearer <token>
Body: { "groupId": 123 }
```

### Get User's Groups
```
GET /api/groups
Headers: Authorization: Bearer <token>
```

## Files Added/Modified

### New Files
- `app/src/main/java/com/tapri/ui/ShareToGroupDialog.kt`
- `app/src/main/java/com/tapri/ui/adapters/GroupSelectionAdapter.kt`
- `app/src/main/java/com/tapri/network/GroupsApi.kt`
- `app/src/main/res/layout/dialog_share_to_group.xml`
- `app/src/main/res/layout/item_group_selection.xml`
- `app/src/main/res/drawable/ic_group.xml`
- `app/src/main/res/drawable/ic_check_circle.xml`
- `app/src/main/res/drawable/circle_background.xml`

### Modified Files
- `src/main/java/com/tapri/controller/PostController.java`
- `src/main/java/com/tapri/service/PostService.java`
- `app/src/main/java/com/tapri/network/PostsApi.kt`
- `app/src/main/java/com/tapri/ui/HomeActivity.kt`

## Testing

The feature has been tested and verified to work correctly:
- Build compiles successfully
- No linting errors
- Proper error handling for network issues
- UI components render correctly
- API integration works as expected

## Future Enhancements

Potential improvements for future versions:
- Multiple group selection
- Share with custom message
- Group-specific post visibility
- Share analytics and insights
- Bulk sharing capabilities
