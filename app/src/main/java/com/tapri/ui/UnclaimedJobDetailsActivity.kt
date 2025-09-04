package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class UnclaimedJobDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var claimButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unclaimed_job_details)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        claimButton = findViewById(R.id.claimButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        claimButton.setOnClickListener {
            // Navigate to claim activity
            val intent = Intent(this, ClaimActivity::class.java)
            startActivityForResult(intent, CLAIM_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == CLAIM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Job was claimed successfully, navigate to job details (claimed version)
            val intent = Intent(this, JobDetailsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val CLAIM_REQUEST_CODE = 1001
    }
} 