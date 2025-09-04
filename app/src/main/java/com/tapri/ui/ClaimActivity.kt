package com.tapri.ui

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class ClaimActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var claimButton: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_claim)

        try {
            // Find views
            backButton = findViewById(R.id.backButton)
            claimButton = findViewById(R.id.claimButton)

            // Set up click listeners
            setupClickListeners()
            
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading Claim screen: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupClickListeners() {
        try {
            // Back button
            backButton.setOnClickListener {
                finish()
            }
            
            // Claim button
            claimButton.setOnClickListener {
                // Set result to indicate job was claimed
                setResult(RESULT_OK)
                Toast.makeText(this, "Job claimed successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up click listeners: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 