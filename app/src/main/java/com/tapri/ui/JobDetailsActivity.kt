package com.tapri.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var callButton: TextView
    private lateinit var submitProofsButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        callButton = findViewById(R.id.callButton)
        submitProofsButton = findViewById(R.id.submitProofsButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        callButton.setOnClickListener {
            // Simulate calling customer
            Toast.makeText(this, "Calling customer...", Toast.LENGTH_SHORT).show()
            // In a real app, you would launch the phone dialer
            // val intent = Intent(Intent.ACTION_DIAL)
            // intent.data = Uri.parse("tel:1234567890")
            // startActivity(intent)
        }

        submitProofsButton.setOnClickListener {
            // Simulate submitting proofs
            Toast.makeText(this, "Proofs submitted successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
} 