package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import androidx.appcompat.app.AlertDialog
import com.tapri.R
import com.tapri.utils.SessionManager
import com.tapri.utils.FeatureFlags
import com.tapri.utils.AnimationUtils
import com.tapri.network.ApiClient
import com.tapri.network.ProfileApi
// import com.tapri.network.UserDto as ProfileUserDto
import com.tapri.ui.LoginActivity
import com.tapri.ui.MyPostsActivity
import kotlinx.coroutines.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var profileApi: ProfileApi
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // UI Elements
    private lateinit var profilePicImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var backButton: TextView
    private lateinit var myPostsButton: LinearLayout
    private lateinit var taskHistoryButton: LinearLayout
    private lateinit var savedPostsButton: LinearLayout
    private lateinit var settingsButton: LinearLayout
    private lateinit var logoutButton: LinearLayout
    private lateinit var loadingGifView: ImageView
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize
        sessionManager = SessionManager(this)
        profileApi = ApiClient.profileRetrofit(sessionManager).create(ProfileApi::class.java)
        
        // Find views
        profilePicImageView = findViewById(R.id.profilePic)
        userNameTextView = findViewById(R.id.userName)
        backButton = findViewById(R.id.backButton)
        myPostsButton = findViewById(R.id.myPostsButton)
        taskHistoryButton = findViewById(R.id.taskHistoryButton)
        savedPostsButton = findViewById(R.id.savedPostsButton)
        settingsButton = findViewById(R.id.settingsButton)
        logoutButton = findViewById(R.id.logoutButton)
        loadingGifView = findViewById(R.id.loadingGifView)
        errorMessageView = findViewById(R.id.errorMessageView)
        retryButton = findViewById(R.id.retryButton)
        
        // Set up retry button
        retryButton.setOnClickListener {
            loadUserProfileFromServer()
        }
        
        // Load user data
        loadUserProfileFromServer()
        
        
        // Set up button click listeners
        setupButtonListeners()
    }
    
    private fun loadUserProfileFromServer() {
        val currentUser = sessionManager.getUserSession()
        val authToken = sessionManager.getAuthToken()
        
        android.util.Log.d("ProfileActivity", "Current user: $currentUser")
        android.util.Log.d("ProfileActivity", "Auth token: $authToken")
        
        if (currentUser == null) {
            // User not logged in, redirect to login
            android.util.Log.e("ProfileActivity", "User not logged in, redirecting to login")
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Check authentication status
        android.util.Log.d("ProfileActivity", "Token status: ${sessionManager.getTokenStatus()}")
        com.tapri.utils.AuthDebugHelper.logTokenStatus(sessionManager)
        
        if (sessionManager.needsReauthentication()) {
            android.util.Log.w("ProfileActivity", "Token expired or missing, attempting refresh...")
            
            // Test refresh endpoint first for debugging
            com.tapri.utils.AuthDebugHelper.testRefreshEndpoint(sessionManager, coroutineScope)
            
            // Try to refresh token first
            com.tapri.utils.TokenRefreshHelper.refreshTokenAsync(
                sessionManager = sessionManager,
                coroutineScope = coroutineScope,
                onSuccess = {
                    android.util.Log.d("ProfileActivity", "Token refreshed successfully, loading profile")
                    com.tapri.utils.AuthDebugHelper.logTokenStatus(sessionManager)
                    loadProfileData()
                },
                onFailure = {
                    android.util.Log.e("ProfileActivity", "Token refresh failed, redirecting to login")
                    com.tapri.utils.AuthDebugHelper.logTokenStatus(sessionManager)
                    Toast.makeText(this@ProfileActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                    sessionManager.clearSession()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finish()
                }
            )
            return
        }
        
        // Load profile data
        loadProfileData()
    }
    
    private fun loadProfileData() {
        // Show loading state
        showLoadingState()
        
        coroutineScope.launch {
            try {
                android.util.Log.d("ProfileActivity", "Making profile API call")
                val response = profileApi.getProfile()
                android.util.Log.d("ProfileActivity", "Profile API response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val userProfile = response.body()
                        android.util.Log.d("ProfileActivity", "Profile response body: $userProfile")
                        
                        userProfile?.let { user ->
                            updateProfileUI(user)
                            showContentState()
                        } ?: run {
                            android.util.Log.e("ProfileActivity", "Profile response body is null")
                            showErrorState("Failed to load profile")
                        }
                    } else {
                        android.util.Log.e("ProfileActivity", "Profile API failed with code: ${response.code()}, message: ${response.message()}")
                        
                        when (response.code()) {
                            401 -> {
                                // Try to refresh token and retry
                                android.util.Log.w("ProfileActivity", "Received 401, attempting token refresh...")
                                if (sessionManager.canRefreshToken()) {
                                    attemptTokenRefreshAndRetry()
                                } else {
                                    android.util.Log.e("ProfileActivity", "No refresh token available, redirecting to login")
                                    sessionManager.clearSession()
                                    Toast.makeText(this@ProfileActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            403 -> {
                                android.util.Log.e("ProfileActivity", "Access forbidden (403) - showing fallback profile")
                                // Show fallback profile with basic user info
                                showFallbackProfile()
                            }
                            else -> {
                                android.util.Log.e("ProfileActivity", "Profile API failed with code: ${response.code()}")
                                val errorBody = response.errorBody()?.string()
                                android.util.Log.e("ProfileActivity", "Error body: $errorBody")
                                showErrorState("Failed to load profile: ${response.code()}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProfileActivity", "Profile API exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showErrorState("Network error: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }
    
    private fun updateProfileUI(user: com.tapri.network.UserDto) {
        android.util.Log.d("ProfileActivity", "Updating profile UI with user: $user")
        
        // Update user name
        userNameTextView.text = user.name
        
        
        // Load profile picture
        loadProfilePicture(user.profilePictureUrl)
        
        android.util.Log.d("ProfileActivity", "Profile UI updated: name=${user.name}, profilePicture=${user.profilePictureUrl}")
    }
    
    
    private fun loadProfilePicture(profilePictureUrl: String?) {
        if (!profilePictureUrl.isNullOrEmpty()) {
            // Load profile picture from URL
            Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile))
                .into(profilePicImageView)
        } else {
            // Use default profile picture
            profilePicImageView.setImageResource(R.drawable.profile)
        }
    }
    
    private fun attemptTokenRefreshAndRetry() {
        android.util.Log.d("ProfileActivity", "Attempting token refresh and retry...")
        com.tapri.utils.TokenRefreshHelper.refreshTokenAsync(
            sessionManager = sessionManager,
            coroutineScope = coroutineScope,
            onSuccess = {
                android.util.Log.d("ProfileActivity", "Token refreshed successfully, retrying profile load")
                loadProfileData()
            },
            onFailure = {
                android.util.Log.e("ProfileActivity", "Token refresh failed during retry, redirecting to login")
                sessionManager.clearSession()
                Toast.makeText(this@ProfileActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        )
    }
    
    private fun showLoadingState() {
        // Load and start the GIF animation
        Glide.with(this)
            .load(R.drawable.loading)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(loadingGifView)
        
        loadingGifView.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.errorContainer).visibility = View.GONE
        findViewById<LinearLayout>(R.id.mainContent).visibility = View.GONE
    }
    
    private fun showContentState() {
        loadingGifView.visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorContainer).visibility = View.GONE
        findViewById<LinearLayout>(R.id.mainContent).visibility = View.VISIBLE
    }
    
    private fun showErrorState(message: String) {
        loadingGifView.visibility = View.GONE
        findViewById<LinearLayout>(R.id.errorContainer).visibility = View.VISIBLE
        errorMessageView.text = message
        findViewById<LinearLayout>(R.id.mainContent).visibility = View.GONE
    }
    
    private fun showFallbackProfile() {
        android.util.Log.d("ProfileActivity", "Showing fallback profile")
        
        // Get basic user info from session
        val currentUser = sessionManager.getUserSession()
        
        if (currentUser != null) {
            // Convert User to UserDto for updateProfileUI
            val fallbackProfile = com.tapri.network.UserDto(
                id = currentUser.id,
                name = currentUser.name,
                phoneNumber = currentUser.phone,
                profilePictureUrl = currentUser.profilePhotoUrl ?: currentUser.profilePicture,
                city = currentUser.city,
                state = currentUser.state,
                bio = currentUser.bio,
                rating = currentUser.rating,
                earnings = currentUser.earnings,
                lastSeen = currentUser.lastSeen,
                lastLogin = currentUser.lastLogin,
                lastSeenVisible = currentUser.lastSeenVisible,
                createdAt = currentUser.createdAt
            )
            
            updateProfileUI(fallbackProfile)
            showContentState()
            
            // Show a subtle message that this is limited data
            Toast.makeText(this, "Showing basic profile information", Toast.LENGTH_SHORT).show()
        } else {
            showErrorState("Unable to load profile information")
        }
    }
    
    
    private fun setupButtonListeners() {
        // Back button
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
        
        // Add entrance animations for profile elements
        startProfileEntranceAnimation()
        
        // My Posts button
        myPostsButton.setOnClickListener {
            AnimationUtils.animateButtonPress(myPostsButton) {
                val intent = Intent(this, MyPostsActivity::class.java)
                startActivity(intent)
            }
        }
        
        taskHistoryButton.setOnClickListener {
            AnimationUtils.animateButtonPress(taskHistoryButton) {
                // TODO: Implement task history screen
                Toast.makeText(this, "Task History - Coming Soon", Toast.LENGTH_SHORT).show()
            }
        }
        
        savedPostsButton.setOnClickListener {
            AnimationUtils.animateButtonPress(savedPostsButton) {
                val intent = Intent(this, MyPostsActivity::class.java)
                startActivity(intent)
            }
        }
        
        settingsButton.setOnClickListener {
            AnimationUtils.animateButtonPress(settingsButton) {
                showSettingsDialog()
            }
        }
        
        logoutButton.setOnClickListener {
            AnimationUtils.animateButtonPress(logoutButton) {
                showLogoutConfirmationDialog()
            }
        }
    }
    
    private fun showSettingsDialog() {
        val options = arrayOf("Edit Profile", "Privacy Settings", "Notification Settings", "About")
        
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Settings")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Edit Profile
                    Toast.makeText(this, "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    // Privacy Settings
                    Toast.makeText(this, "Privacy Settings - Coming Soon", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    // Notification Settings
                    Toast.makeText(this, "Notification Settings - Coming Soon", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    // About
                    showAboutDialog()
                }
            }
        }
        builder.show()
    }
    
    private fun showAboutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("About Tapri")
        builder.setMessage("Version 1.0.0\n\nTapri - Your community for traffic updates and local insights.\n\nBuilt with ❤️ for better commuting.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        builder.setPositiveButton("Logout") { _, _ ->
            performLogout()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    
    private fun performLogout() {
        // Clear user session
        sessionManager.clearSession()
        
        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to login screen
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh user data when returning to profile
        loadUserProfileFromServer()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
    
    private fun startProfileEntranceAnimation() {
        // Initial state for profile elements
        profilePicImageView.alpha = 0f
        profilePicImageView.scaleX = 0.8f
        profilePicImageView.scaleY = 0.8f
        
        userNameTextView.alpha = 0f
        userNameTextView.translationY = 30f
        
        // userRatingTextView.alpha = 0f
        // userRatingTextView.translationY = 30f
        
        // Animate profile picture first
        profilePicImageView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600)
            .setInterpolator(android.view.animation.OvershootInterpolator(1.2f))
            .withEndAction {
                // Then animate text elements
                userNameTextView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(400)
                    .setInterpolator(android.view.animation.DecelerateInterpolator())
                    .start()
                
                // userRatingTextView.animate()
                //     .alpha(1f)
                //     .translationY(0f)
                //     .setDuration(400)
                //     .setStartDelay(100)
                //     .setInterpolator(android.view.animation.DecelerateInterpolator())
                //     .start()
            }
            .start()
    }
}