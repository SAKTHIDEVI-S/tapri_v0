package com.tapri.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class CreatePostActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var contentInput: EditText
    private lateinit var postButton: TextView
    private lateinit var trafficAlertCard: LinearLayout
    private lateinit var askHelpCard: LinearLayout
    private lateinit var shareTipCard: LinearLayout
    private lateinit var audienceDropdown: LinearLayout
    private lateinit var audienceText: TextView
    
    private var selectedPostType: String = "Traffic alert"
    private var selectedAudience: String = "Everyone"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        
        initializeViews()
        setupClickListeners()
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
    }
    
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Post type selection
        trafficAlertCard.setOnClickListener {
            selectPostType("Traffic alert")
        }
        
        askHelpCard.setOnClickListener {
            selectPostType("Ask help")
        }
        
        shareTipCard.setOnClickListener {
            selectPostType("Share tip")
        }
        
        // Audience dropdown
        audienceDropdown.setOnClickListener {
            showAudienceDialog()
        }
        
        // Post button
        postButton.setOnClickListener {
            val content = contentInput.text.toString().trim()
            if (content.isEmpty()) {
                Toast.makeText(this, "Please enter some content", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // Here you would typically save the post to your backend
            Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun selectPostType(postType: String) {
        selectedPostType = postType
        
        // Reset all cards
        trafficAlertCard.setBackgroundResource(R.drawable.post_type_card_background)
        askHelpCard.setBackgroundResource(R.drawable.post_type_card_background)
        shareTipCard.setBackgroundResource(R.drawable.post_type_card_background)
        
        // Highlight selected card
        when (postType) {
            "Traffic alert" -> {
                trafficAlertCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
            }
            "Ask help" -> {
                askHelpCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
            }
            "Share tip" -> {
                shareTipCard.setBackgroundResource(R.drawable.post_type_card_selected_background)
            }
        }
        
        Toast.makeText(this, "Selected: $postType", Toast.LENGTH_SHORT).show()
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
}