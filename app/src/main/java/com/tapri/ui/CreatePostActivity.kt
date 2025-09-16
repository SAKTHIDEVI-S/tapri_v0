package com.tapri.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.PostsApi
import com.tapri.network.ImageApi
import com.tapri.utils.SessionManager
import com.tapri.utils.AnimationUtils
import com.tapri.ui.templates.TemplateLibrary
import com.tapri.ui.templates.PostTemplate
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException

class CreatePostActivity : AppCompatActivity() {
    
    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val REQUEST_VIDEO_PICK = 1002
        private const val REQUEST_PHOTO_CROP = 1003
        private const val REQUEST_VIDEO_TRIM = 1004
        private const val REQUEST_AUDIO_PERMISSION = 1005
        private const val REQUEST_CAMERA_PERMISSION = 1006
        private const val REQUEST_STORAGE_PERMISSION = 1007
        private const val REQUEST_MEDIA_IMAGES_PERMISSION = 1008
        private const val REQUEST_MEDIA_VIDEO_PERMISSION = 1009
        private const val REQUEST_RECORD_AUDIO = 1010
    }
    
    private lateinit var backButton: TextView
    private lateinit var contentInput: EditText
    private lateinit var postButton: TextView
    private lateinit var trafficAlertCard: LinearLayout
    private lateinit var askHelpCard: LinearLayout
    private lateinit var shareTipCard: LinearLayout
    private lateinit var audienceDropdown: LinearLayout
    private lateinit var audienceText: TextView
    private lateinit var photoOption: LinearLayout
    private lateinit var videoOption: LinearLayout
    private lateinit var audioOption: LinearLayout
    private lateinit var mediaPreviewContainer: LinearLayout
    private lateinit var mediaPreview: ImageView
    private lateinit var removeMediaButton: TextView
    
    private var selectedPostType: String = "Traffic alert"
    private var selectedAudience: String = "Everyone"
    
    // Media upload
    private var selectedMediaUri: Uri? = null
    private var selectedMediaType: String? = null
    
    // Audio recording
    private var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null
    private var isRecording = false
    
    // API and session management
    private lateinit var sessionManager: SessionManager
    private lateinit var postsApi: PostsApi
    private lateinit var imageApi: ImageApi
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        
        // Initialize session manager and API
        sessionManager = SessionManager(this)
        postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
        imageApi = ApiClient.imageRetrofit(sessionManager).create(ImageApi::class.java)
        
        initializeViews()
        setupClickListeners()
        setupAnimations()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        contentInput = findViewById(R.id.contentInput)
        postButton = findViewById(R.id.postButton)
        trafficAlertCard = findViewById(R.id.trafficAlertCard)
        askHelpCard = findViewById(R.id.askHelpCard)
        shareTipCard = findViewById(R.id.shareTipCard)
        audienceDropdown = findViewById(R.id.audienceDropdown)
        audienceText = findViewById(R.id.audienceText)
        photoOption = findViewById(R.id.photoOption)
        videoOption = findViewById(R.id.videoOption)
        audioOption = findViewById(R.id.audioOption)
        mediaPreviewContainer = findViewById(R.id.mediaPreviewContainer)
        mediaPreview = findViewById(R.id.mediaPreview)
        removeMediaButton = findViewById(R.id.removeMediaButton)
    }
    
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
        
        // Post type selection
        trafficAlertCard.setOnClickListener {
            AnimationUtils.animateButtonPress(trafficAlertCard) {
                selectPostType("Traffic alert")
            }
        }
        
        askHelpCard.setOnClickListener {
            AnimationUtils.animateButtonPress(askHelpCard) {
                selectPostType("Ask help")
            }
        }
        
        shareTipCard.setOnClickListener {
            AnimationUtils.animateButtonPress(shareTipCard) {
                selectPostType("Share tip")
            }
        }
        
        // Audience dropdown
        audienceDropdown.setOnClickListener {
            AnimationUtils.animateButtonPress(audienceDropdown) {
                showAudienceDialog()
            }
        }
        
        // Photo option
        photoOption.setOnClickListener {
            AnimationUtils.animateButtonPress(photoOption) {
                requestStoragePermissionForPhoto()
            }
        }
        
        // Video option
        videoOption.setOnClickListener {
            AnimationUtils.animateButtonPress(videoOption) {
                requestStoragePermissionForVideo()
            }
        }
        
        // Audio option
        audioOption.setOnClickListener {
            AnimationUtils.animateButtonPress(audioOption) {
                requestAudioPermission()
            }
        }
        
        // Remove media button
        removeMediaButton.setOnClickListener {
            AnimationUtils.animateButtonPress(removeMediaButton) {
                removeSelectedMedia()
            }
        }
        
        // Post button
        postButton.setOnClickListener {
            AnimationUtils.animateButtonPress(postButton) {
                val content = contentInput.text.toString().trim()
                if (content.isEmpty()) {
                    AnimationUtils.shake(contentInput) {
                        Toast.makeText(this@CreatePostActivity, "Please enter some content", Toast.LENGTH_SHORT).show()
                    }
                    return@animateButtonPress
                }
                
                createPost(content)
            }
        }
    }
    
    private fun setupAnimations() {
        // Animate views on screen entry
        AnimationUtils.slideInFromRight(backButton, 300)
        AnimationUtils.slideInFromBottom(contentInput, 500)
        AnimationUtils.slideInFromBottom(trafficAlertCard, 600)
        AnimationUtils.slideInFromBottom(askHelpCard, 700)
        AnimationUtils.slideInFromBottom(shareTipCard, 800)
        AnimationUtils.slideInFromBottom(audienceDropdown, 900)
        AnimationUtils.slideInFromBottom(photoOption, 1000)
        AnimationUtils.slideInFromBottom(videoOption, 1100)
        AnimationUtils.slideInFromBottom(audioOption, 1200)
        AnimationUtils.slideInFromBottom(postButton, 1300)
    }
    
    private fun getRandomTemplateForPostType(postType: String): PostTemplate? {
        val template = TemplateLibrary.getRandomTemplate(postType)
        Log.d("TemplateDebug", "Getting template for postType: $postType")
        Log.d("TemplateDebug", "Available templates count: ${TemplateLibrary.getAllTemplates(postType).size}")
        Log.d("TemplateDebug", "Selected template: $template")
        return template
    }
    
    private fun selectPostType(postType: String) {
        selectedPostType = postType
        
        // Reset all cards with animation
        AnimationUtils.fadeOut(trafficAlertCard, 200) {
            trafficAlertCard.setBackgroundResource(R.drawable.post_type_card_background)
            AnimationUtils.fadeIn(trafficAlertCard, 200)
        }
        AnimationUtils.fadeOut(askHelpCard, 200) {
            askHelpCard.setBackgroundResource(R.drawable.post_type_card_background)
            AnimationUtils.fadeIn(askHelpCard, 200)
        }
        AnimationUtils.fadeOut(shareTipCard, 200) {
            shareTipCard.setBackgroundResource(R.drawable.post_type_card_background)
            AnimationUtils.fadeIn(shareTipCard, 200)
        }
        
        // Check if user has already started typing meaningful content
        val currentText = contentInput.text.toString().trim()
        val allTemplates = TemplateLibrary.getAllTemplates(postType)
        val isFromTemplate = allTemplates.any { template -> 
            currentText.startsWith(template.text.take(20))
        }
        
        // Always autofill when clicking post type buttons, regardless of current content
        val shouldAutofill = true
        
        Log.d("TemplateDebug", "Current text: '$currentText'")
        Log.d("TemplateDebug", "Post type: '$postType'")
        Log.d("TemplateDebug", "All templates count: ${allTemplates.size}")
        Log.d("TemplateDebug", "Is from template: $isFromTemplate")
        Log.d("TemplateDebug", "Should autofill: $shouldAutofill")
        
        // Only autofill if user hasn't started meaningful typing
        if (shouldAutofill) {
            // Get random template for the selected post type
            val template = getRandomTemplateForPostType(postType)
            
            Log.d("TemplateDebug", "Post type: $postType")
            Log.d("TemplateDebug", "Template: $template")
            Log.d("TemplateDebug", "Should autofill: $shouldAutofill")
            
            if (template != null) {
                // Highlight selected card
        when (postType) {
            "Traffic alert" -> {
                AnimationUtils.bounce(trafficAlertCard) {
                    trafficAlertCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                }
            }
            "Ask help" -> {
                AnimationUtils.bounce(askHelpCard) {
                    askHelpCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                }
            }
            "Share tip" -> {
                AnimationUtils.bounce(shareTipCard) {
                    shareTipCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                }
                    }
                }
                
                // Autofill template with structured text
                contentInput.setText(template.text)
                // Select the first placeholder for easy replacement
                val startIndex = template.text.indexOf(template.placeholder)
                val endIndex = startIndex + template.placeholder.length
                contentInput.setSelection(startIndex, endIndex)
                AnimationUtils.bounce(contentInput)
            }
        } else {
            // User has already started typing, just highlight the selected card
            when (postType) {
                "Traffic alert" -> {
                    AnimationUtils.bounce(trafficAlertCard) {
                        trafficAlertCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                    }
                }
                "Ask help" -> {
                    AnimationUtils.bounce(askHelpCard) {
                        askHelpCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                    }
                }
                "Share tip" -> {
                    AnimationUtils.bounce(shareTipCard) {
                        shareTipCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
                    }
                }
            }
        }
        
        AnimationUtils.successAnimation(postButton) {
            Toast.makeText(this, "Selected: $postType", Toast.LENGTH_SHORT).show()
        }
        
        // Add haptic feedback for template selection
        try {
            val vibrator = getSystemService(VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(50) // Short vibration
        } catch (e: Exception) {
            // Ignore if vibration is not available
        }
    }
    
    private fun showAudienceDialog() {
        val options = arrayOf("Everyone", "Groups")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Post to")
        builder.setItems(options) { _, which ->
            selectedAudience = options[which]
            audienceText.text = selectedAudience
        }
        builder.show()
    }
    
    private fun createPost(content: String) {
        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validate content - text is now optional if media is selected
        val trimmedContent = content.trim()
        if (trimmedContent.isEmpty() && selectedMediaUri == null) {
            Toast.makeText(this, "Please enter some content or select media to post", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show loading state with animation
        AnimationUtils.showLoadingAnimation(postButton, true)
        postButton.text = "Posting..."
        postButton.isEnabled = false
        
        coroutineScope.launch {
            try {
                // Upload media first if selected
                val mediaUrl = if (selectedMediaUri != null) {
                    withContext(Dispatchers.Main) {
                        postButton.text = "Uploading media..."
                    }
                    val uploadedUrl = uploadMediaToServer(selectedMediaUri!!, selectedMediaType ?: "IMAGE")
                    if (uploadedUrl == null) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreatePostActivity, "Failed to upload media. Creating post without media.", Toast.LENGTH_LONG).show()
                        }
                    }
                    withContext(Dispatchers.Main) {
                        postButton.text = "Posting..."
                    }
                    uploadedUrl
                } else {
                    null
                }
                
                val request = com.tapri.network.CreatePostRequest(
                    text = trimmedContent,
                    mediaUrl = mediaUrl,
                    mediaType = if (mediaUrl != null) selectedMediaType ?: "IMAGE" else null,
                    postType = selectedPostType ?: "TRAFFIC_ALERT",
                    audience = selectedAudience ?: "EVERYONE"
                )
                
                // Log the request for debugging
                Log.d("CreatePost", "Sending request: $request")
                
                val response = postsApi.createPost(request)
                if (response.isSuccessful) {
                    AnimationUtils.successAnimation(postButton) {
                        Toast.makeText(this@CreatePostActivity, "Post created successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("CreatePost", "Error response: ${response.code()} - $errorBody")
                    
                    when (response.code()) {
                        401 -> {
                            Toast.makeText(this@CreatePostActivity, "Please login again", Toast.LENGTH_SHORT).show()
                            // Redirect to login
                            sessionManager.clearSession()
                            val intent = Intent(this@CreatePostActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        403 -> {
                            Toast.makeText(this@CreatePostActivity, "Access denied. Please check your permissions.", Toast.LENGTH_SHORT).show()
                        }
                        400 -> {
                            Toast.makeText(this@CreatePostActivity, "Invalid request: ${errorBody ?: "Bad request"}", Toast.LENGTH_LONG).show()
                        }
                        500 -> {
                            Toast.makeText(this@CreatePostActivity, "Server error. Please try again later.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this@CreatePostActivity, "Failed to create post: ${response.code()} - ${errorBody ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                Toast.makeText(this@CreatePostActivity, "Network error: Cannot connect to server. Please check your internet connection.", Toast.LENGTH_LONG).show()
            } catch (e: java.net.SocketTimeoutException) {
                Toast.makeText(this@CreatePostActivity, "Request timeout. Please try again.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@CreatePostActivity, "Error creating post: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                // Reset button state with animation
                AnimationUtils.showLoadingAnimation(postButton, false)
                postButton.text = "Post"
                postButton.isEnabled = true
            }
        }
    }
    
    private fun requestStoragePermissionForPhoto() {
        if (hasStoragePermission()) {
            pickImage()
        } else {
            requestStoragePermission(REQUEST_IMAGE_PICK)
        }
    }
    
    private fun requestStoragePermissionForVideo() {
        if (hasStoragePermission()) {
            pickVideo()
        } else {
            requestStoragePermission(REQUEST_VIDEO_PICK)
        }
    }
    
    private fun requestAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION)
        } else {
            startAudioRecording()
        }
    }
    
    private fun startAudioRecording() {
        if (isRecording) {
            stopAudioRecording()
        } else {
            startRecording()
        }
    }
    
    private fun checkVideoDurationAndTrim(videoUri: Uri) {
        // Check video duration and show warning if too long
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(this, videoUri)
            
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = duration?.toLongOrNull() ?: 0
            val durationSeconds = durationMs / 1000
            
            retriever.release()
            
            android.util.Log.d("CreatePost", "Video duration: ${durationSeconds}s")
            
            if (durationSeconds > 30) {
                // Start video trimming activity
                val intent = Intent(this, VideoTrimmerActivity::class.java)
                intent.putExtra(VideoTrimmerActivity.EXTRA_VIDEO_URI, videoUri)
                startActivityForResult(intent, REQUEST_VIDEO_TRIM)
            } else {
                // Video is already short enough
                selectedMediaUri = videoUri
                selectedMediaType = "video"
                showMediaPreview()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error reading video file", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun startPhotoCropping(imageUri: Uri) {
        val intent = Intent(this, PhotoCropperActivity::class.java)
        intent.putExtra(PhotoCropperActivity.EXTRA_IMAGE_URI, imageUri)
        startActivityForResult(intent, REQUEST_PHOTO_CROP)
    }
    
    private fun startRecording() {
        try {
            mediaRecorder = MediaRecorder()
            audioFilePath = File(cacheDir, "audio_${System.currentTimeMillis()}.3gp").absolutePath
            
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFilePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                
                prepare()
                start()
            }
            
            isRecording = true
            Toast.makeText(this, "Recording started... Tap again to stop", Toast.LENGTH_SHORT).show()
            
        } catch (e: IOException) {
            Toast.makeText(this, "Failed to start recording: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    private fun stopAudioRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            
            isRecording = false
            
            // Set the audio file as selected media
            audioFilePath?.let { path ->
                selectedMediaUri = Uri.fromFile(File(path))
                selectedMediaType = "AUDIO"
                showMediaPreview()
                Toast.makeText(this, "Audio recorded successfully!", Toast.LENGTH_SHORT).show()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to stop recording: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    
    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - use new media permissions
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 and below - use old storage permission
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }
    
    private fun requestStoragePermission(requestCode: Int) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - request new media permissions
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            // Android 12 and below - request old storage permission
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }
    
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }
    
    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_VIDEO_PICK)
    }
    
    private fun removeSelectedMedia() {
        selectedMediaUri = null
        selectedMediaType = null
        mediaPreviewContainer.visibility = View.GONE
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data.data
                    if (imageUri != null) {
                        startPhotoCropping(imageUri)
                    }
                }
                REQUEST_VIDEO_PICK -> {
                    val videoUri = data.data
                    if (videoUri != null) {
                        checkVideoDurationAndTrim(videoUri)
                    }
                }
                REQUEST_PHOTO_CROP -> {
                    val croppedImageUri = data.getParcelableExtra<Uri>(PhotoCropperActivity.RESULT_IMAGE_URI)
                    if (croppedImageUri != null) {
                        selectedMediaUri = croppedImageUri
                        selectedMediaType = "IMAGE"
                        showMediaPreview()
                    }
                }
                REQUEST_VIDEO_TRIM -> {
                    val trimmedVideoUri = data.getParcelableExtra<Uri>(VideoTrimmerActivity.RESULT_VIDEO_URI)
                    if (trimmedVideoUri != null) {
                        selectedMediaUri = trimmedVideoUri
                        selectedMediaType = "video"
                        showMediaPreview()
                    }
                }
            }
        }
    }
    
    private fun showMediaPreview() {
        selectedMediaUri?.let { uri ->
            when (selectedMediaType) {
                "VIDEO" -> {
                    // For videos, we need to show a video thumbnail or use VideoView
                    // For now, we'll use a placeholder and let the backend handle video processing
                    mediaPreview.setImageResource(R.drawable.video_placeholder)
                }
                "AUDIO" -> {
                    // For audio, show a speaker icon or waveform
                    mediaPreview.setImageResource(R.drawable.audio_placeholder)
                }
                else -> {
                    mediaPreview.setImageURI(uri)
                }
            }
            mediaPreviewContainer.visibility = View.VISIBLE
        }
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        val allPermissionsGranted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        
        when (requestCode) {
            REQUEST_IMAGE_PICK -> {
                if (allPermissionsGranted) {
                    pickImage()
                } else {
                    showPermissionDeniedDialog("photos")
                }
            }
            REQUEST_VIDEO_PICK -> {
                if (allPermissionsGranted) {
                    pickVideo()
                } else {
                    showPermissionDeniedDialog("videos")
                }
            }
            REQUEST_AUDIO_PERMISSION -> {
                if (allPermissionsGranted) {
                    startAudioRecording()
                } else {
                    showPermissionDeniedDialog("microphone")
                }
            }
        }
    }
    
    private fun showPermissionDeniedDialog(mediaType: String) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage("Storage permission is required to select $mediaType. Please grant permission in settings.")
        builder.setPositiveButton("Open Settings") { _, _ ->
            val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = android.net.Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    
    private suspend fun uploadMediaToServer(mediaUri: Uri, mediaType: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Get the file from URI
                val inputStream = contentResolver.openInputStream(mediaUri)
                val file = File(cacheDir, "temp_media_${System.currentTimeMillis()}.${getFileExtension(mediaUri)}")
                inputStream?.use { it.copyTo(file.outputStream()) }
                
                if (!file.exists()) {
                    Log.e("MediaUpload", "File does not exist after copying")
                    return@withContext null
                }
                
                // Compress the image if it's an image
                val compressedFile = if (mediaType == "IMAGE") {
                    try {
                        compressImage(file)
                    } catch (e: Exception) {
                        Log.e("MediaUpload", "Compression failed: ${e.message}")
                        file // Use original if compression fails
                    }
                } else {
                    file
                }
                
                // Create multipart body
                val requestFile = RequestBody.create(
                    getMediaType(mediaType).toMediaTypeOrNull(),
                    compressedFile
                )
                val body = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)
                
                // Upload to server using the image upload endpoint
                Log.d("MediaUpload", "Starting upload with file: ${compressedFile.name}, size: ${compressedFile.length()} bytes")
                val uploadResponse = imageApi.uploadImage(
                    file = body,
                    folder = "posts"
                )
                
                Log.d("MediaUpload", "Upload response received - Success: ${uploadResponse.isSuccessful}, Code: ${uploadResponse.code()}")
                
                if (uploadResponse.isSuccessful) {
                    val responseBody = uploadResponse.body()
                    Log.d("MediaUpload", "Response body: $responseBody")
                    if (responseBody != null && responseBody["success"] == "true") {
                        val mediaUrl = responseBody["imageUrl"] as? String
                        Log.d("MediaUpload", "Upload successful: $mediaUrl")
                        mediaUrl
                    } else {
                        Log.e("MediaUpload", "Upload failed: ${responseBody}")
                        null
                    }
                } else {
                    val errorBody = uploadResponse.errorBody()?.string()
                    Log.e("MediaUpload", "Upload failed: ${uploadResponse.code()} - ${uploadResponse.message()}")
                    Log.e("MediaUpload", "Error body: $errorBody")
                    null
                }
            } catch (e: Exception) {
                Log.e("MediaUpload", "Upload error: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
    
    private fun getFileExtension(uri: Uri): String {
        val mimeType = contentResolver.getType(uri)
        return when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "video/mp4" -> "mp4"
            "video/3gpp" -> "3gp"
            "audio/3gpp" -> "3gp"
            "audio/mpeg" -> "mp3"
            else -> {
                // For file URIs, try to get extension from path
                val path = uri.path
                if (path != null && path.contains(".")) {
                    path.substring(path.lastIndexOf(".") + 1)
                } else {
                    "jpg"
                }
            }
        }
    }
    
    private fun getMediaType(mediaType: String): String {
        return when (mediaType) {
            "IMAGE" -> "image/jpeg"
            "GIF" -> "image/gif"
            "VIDEO" -> "video/mp4"
            "AUDIO" -> "audio/3gpp"
            else -> "image/jpeg"
        }
    }
    
    private fun compressImage(inputFile: File): File {
        val outputFile = File(cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
        
        try {
            // Load the original image
            val originalBitmap = BitmapFactory.decodeFile(inputFile.absolutePath)
            if (originalBitmap == null) {
                Log.e("ImageCompression", "Failed to decode image")
                return inputFile
            }
            
            // Get image orientation
            val orientation = getImageOrientation(inputFile.absolutePath)
            
            // Calculate new dimensions (max 1280x720)
            val maxWidth = 1280
            val maxHeight = 720
            val width = originalBitmap.width
            val height = originalBitmap.height
            
            val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height, 1.0f)
            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()
            
            // Scale the bitmap
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            
            // Rotate if needed
            val rotatedBitmap = if (orientation != 0) {
                rotateBitmap(scaledBitmap, orientation)
            } else {
                scaledBitmap
            }
            
            // Compress and save
            val outputStream = outputFile.outputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            
            // Clean up
            originalBitmap.recycle()
            scaledBitmap.recycle()
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            
            Log.d("ImageCompression", "Image compressed: ${inputFile.length()} -> ${outputFile.length()} bytes")
            return outputFile
            
        } catch (e: Exception) {
            Log.e("ImageCompression", "Error compressing image: ${e.message}")
            return inputFile
        }
    }
    
    private fun getImageOrientation(imagePath: String): Int {
        return try {
            val exif = ExifInterface(imagePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            Log.e("ImageCompression", "Error reading EXIF data: ${e.message}")
            0
        }
    }
    
    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        
        // Clean up media recorder
        mediaRecorder?.release()
        mediaRecorder = null
    }
}