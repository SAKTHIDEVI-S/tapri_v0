# 📸 Image Upload & Storage Guide for Tapri

## How Image Upload Works in Tapri

### 🔄 **Complete Flow: From Phone to Display**

#### **1. User Uploads Image from Phone**
```
User selects image → Android app → Upload to server → Store in database → Display to others
```

#### **2. Step-by-Step Process**

**Step 1: User Action**
- User opens Tapri app
- Clicks "Create Post" button
- Selects image from gallery or takes photo
- Adds caption/text
- Clicks "Post"

**Step 2: Android App Processing**
```kotlin
// Android app sends multipart request
val requestBody = MultipartBody.Builder()
    .setType(MultipartBody.FORM)
    .addFormDataPart("text", caption)
    .addFormDataPart("image", imageFile.name, 
        imageFile.asRequestBody("image/*".toMediaType()))
    .build()

// Send to backend
POST /api/posts/with-image
```

**Step 3: Backend Processing**
```java
// Server receives the request
@PostMapping("/with-image")
public ResponseEntity<Map<String, Object>> createPostWithImage(
    @RequestParam("text") String text,
    @RequestParam("image") MultipartFile image,
    @RequestHeader("X-User-Id") Long userId) {
    
    // 1. Upload image to server storage
    String imageUrl = imageUploadService.uploadImage(image, "posts");
    
    // 2. Save post to database with image URL
    PostDto post = postService.createPost(userId, text, imageUrl, MediaType.IMAGE);
    
    return ResponseEntity.ok(response);
}
```

**Step 4: Image Storage**
```java
// Image is saved to: uploads/posts/uuid-filename.jpg
// Example: uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
// Database stores: /uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg
```

**Step 5: Display to Other Users**
```java
// When other users view posts, they get:
{
  "id": 1,
  "text": "Beautiful sunset!",
  "mediaUrl": "/uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg",
  "mediaType": "IMAGE",
  "user": {...}
}
```

### 🗂️ **File Storage Structure**

```
D:\Tapri-full\Tapri_v0\
├── uploads/
│   ├── posts/           # Post images
│   │   ├── uuid1.jpg
│   │   ├── uuid2.png
│   │   └── uuid3.gif
│   ├── profiles/        # Profile pictures
│   │   ├── user1.jpg
│   │   └── user2.png
│   └── groups/          # Group photos
│       ├── group1.jpg
│       └── group2.png
```

### 📱 **Android App Integration**

#### **Image Upload in Android**
```kotlin
// In your Android app's CreatePostActivity
class CreatePostActivity : AppCompatActivity() {
    
    private fun uploadPostWithImage(text: String, imageUri: Uri) {
        val apiService = ApiClient.postsRetrofit(sessionManager).create(PostApiService::class.java)
        
        val requestFile = imageUri.toFile(this)
        val imagePart = MultipartBody.Part.createFormData(
            "image", 
            requestFile.name, 
            requestFile.asRequestBody("image/*".toMediaType())
        )
        
        val textPart = MultipartBody.Part.createFormData("text", text)
        
        apiService.createPostWithImage(textPart, imagePart).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Post created successfully
                    Toast.makeText(this@CreatePostActivity, "Post created!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CreatePostActivity, "Failed to create post", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
```

#### **Displaying Images in Posts**
```kotlin
// In your PostAdapter
class PostAdapter {
    
    fun bind(post: Post) {
        // Set text
        captionText.text = post.caption
        
        // Load image if exists
        if (post.mediaUrl != null && post.mediaType == MediaType.IMAGE) {
            val imageUrl = "http://192.168.1.2:8080${post.mediaUrl}"
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(postImage)
            
            postImage.visibility = View.VISIBLE
        } else {
            postImage.visibility = View.GONE
        }
    }
}
```

### 🌐 **API Endpoints**

#### **Upload Image Only**
```http
POST /api/images/upload
Content-Type: multipart/form-data

file: [image file]
folder: posts
```

**Response:**
```json
{
  "success": true,
  "imageUrl": "/uploads/posts/uuid-filename.jpg",
  "message": "Image uploaded successfully"
}
```

#### **Create Post with Image**
```http
POST /api/posts/with-image
Content-Type: multipart/form-data
X-User-Id: 1

text: "Beautiful sunset!"
image: [image file]
```

**Response:**
```json
{
  "success": true,
  "post": {
    "id": 1,
    "text": "Beautiful sunset!",
    "mediaUrl": "/uploads/posts/uuid-filename.jpg",
    "mediaType": "IMAGE",
    "user": {...},
    "createdAt": "2025-01-10T19:30:00"
  },
  "message": "Post created successfully"
}
```

#### **Get Image**
```http
GET /api/images/posts/uuid-filename.jpg
```

**Response:** Binary image data

### 🔧 **Configuration**

#### **Backend Configuration**
```properties
# application.properties
app.upload.dir=uploads/
server.address=0.0.0.0
server.port=8080
```

#### **Android Network Security**
```xml
<!-- network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.1.2</domain>
    </domain-config>
</network-security-config>
```

### 🚀 **Testing the Flow**

#### **1. Test Image Upload**
```bash
curl -X POST "http://192.168.1.2:8080/api/images/upload" \
  -F "file=@test-image.jpg" \
  -F "folder=posts"
```

#### **2. Test Post Creation with Image**
```bash
curl -X POST "http://192.168.1.2:8080/api/posts/with-image" \
  -H "X-User-Id: 1" \
  -F "text=Test post with image" \
  -F "image=@test-image.jpg"
```

#### **3. Test Image Display**
```bash
curl "http://192.168.1.2:8080/api/images/posts/uuid-filename.jpg"
```

### 📋 **Key Points**

1. **Image Storage**: Images are stored locally on the server in the `uploads/` directory
2. **URL Format**: Database stores relative URLs like `/uploads/posts/filename.jpg`
3. **Full URL**: Android app constructs full URLs like `http://192.168.1.2:8080/uploads/posts/filename.jpg`
4. **Unique Names**: Each uploaded image gets a UUID to prevent conflicts
5. **Folder Organization**: Images are organized by type (posts, profiles, groups)
6. **Error Handling**: Both upload and display have proper error handling
7. **Security**: Only image files are accepted for upload

### 🔄 **Real-World Example**

1. **User Action**: Sarah takes a photo of her delivery route and posts "Beautiful sunset today! 🌅"
2. **Upload**: Image is saved as `uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg`
3. **Database**: Post record stores `mediaUrl: "/uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"`
4. **Display**: Other users see the post with the image loaded from `http://192.168.1.2:8080/uploads/posts/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg`

This creates a seamless Instagram-like experience where users can share photos and see them instantly in their feed! 📸✨
