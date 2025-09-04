package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.utils.SessionManager
import com.tapri.ui.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    
    // UI Elements
    private lateinit var profilePicImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userRatingTextView: TextView
    private lateinit var todayEarningsTextView: TextView
    private lateinit var backButton: TextView
    private lateinit var taskHistoryButton: LinearLayout
    private lateinit var savedPostsButton: LinearLayout
    private lateinit var settingsButton: LinearLayout
    private lateinit var logoutButton: LinearLayout
    

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize
        sessionManager = SessionManager(this)
        
        // Find views
        profilePicImageView = findViewById(R.id.profilePic)
        userNameTextView = findViewById(R.id.userName)
        userRatingTextView = findViewById(R.id.userRating)
        todayEarningsTextView = findViewById(R.id.todayEarnings)
        backButton = findViewById(R.id.backButton)
        taskHistoryButton = findViewById(R.id.taskHistoryButton)
        savedPostsButton = findViewById(R.id.savedPostsButton)
        settingsButton = findViewById(R.id.settingsButton)
        logoutButton = findViewById(R.id.logoutButton)
        
        // Load user data
        loadUserData()
        
        // Set up button click listeners
        setupButtonListeners()
    }
    
    private fun loadUserData() {
        val currentUser = sessionManager.getUserSession()
        
        if (currentUser != null) {
            // Display user name
            userNameTextView.text = currentUser.name
            
            // Display user rating
            userRatingTextView.text = currentUser.rating?.toString() ?: "0.0"
            
            // Display today's earnings (you can modify this based on your requirements)
            val todayEarnings = currentUser.totalEarnings ?: 0.0
            todayEarningsTextView.text = "₹${String.format("%.0f", todayEarnings)}"
            
            // Load profile picture
            loadProfilePicture(currentUser.profilePictureUrl)
            
            // Frontend-only: no backend calls needed
        } else {
            // User not logged in, redirect to login
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    
    private fun loadProfilePicture(profilePictureUrl: String?) {
        // Use the existing profile.png image
        profilePicImageView.setImageResource(R.drawable.profile)
    }
    
    private fun setupButtonListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        taskHistoryButton.setOnClickListener {
            Toast.makeText(this, "Task History", Toast.LENGTH_SHORT).show()
        }
        
        savedPostsButton.setOnClickListener {
            Toast.makeText(this, "Saved Posts", Toast.LENGTH_SHORT).show()
        }
        
        settingsButton.setOnClickListener {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
        }
        
        logoutButton.setOnClickListener {
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
    }
    

    
    override fun onResume() {
        super.onResume()
        // Refresh user data when returning to profile
        loadUserData()
    }
}