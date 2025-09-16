package com.tapri.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.EarnApi
import com.tapri.network.SubmitProofRequest
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var callButton: TextView
    private lateinit var submitProofsButton: TextView
    private lateinit var jobTitle: TextView
    private lateinit var hourlyRate: TextView
    private lateinit var payAmount: TextView
    private lateinit var jobDescription: TextView
    private lateinit var location: TextView
    private lateinit var contactPhone: TextView
    
    // Networking
    private lateinit var sessionManager: SessionManager
    private lateinit var earnApi: EarnApi
    private var jobId: Long = -1
    private var claimId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_details)

        // Get job and claim IDs from intent
        jobId = intent.getLongExtra("jobId", -1)
        claimId = intent.getLongExtra("claimId", -1)

        initializeViews()
        setupClickListeners()
        
        // Initialize networking
        sessionManager = SessionManager(this)
        earnApi = ApiClient.earnRetrofit(sessionManager).create(EarnApi::class.java)
        
        // Load job details
        loadJobDetails()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        callButton = findViewById(R.id.callButton)
        submitProofsButton = findViewById(R.id.submitProofsButton)
        
        // Add these new views - you'll need to add IDs to the layout
        jobTitle = findViewById(R.id.jobTitle)
        hourlyRate = findViewById(R.id.hourlyRate)
        payAmount = findViewById(R.id.payAmount)
        jobDescription = findViewById(R.id.jobDescription)
        location = findViewById(R.id.location)
        contactPhone = findViewById(R.id.contactPhone)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        callButton.setOnClickListener {
            val phone = contactPhone.text.toString()
            if (phone.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show()
            }
        }

        submitProofsButton.setOnClickListener {
            submitProof()
        }
    }

    private fun loadJobDetails() {
        if (jobId == -1L) {
            Toast.makeText(this, "Invalid job ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = earnApi.getJobDetails(jobId)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val job = response.body()
                        job?.let { displayJobDetails(it) }
                    } else {
                        Toast.makeText(this@JobDetailsActivity, "Failed to load job details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@JobDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayJobDetails(job: com.tapri.network.JobItem) {
        jobTitle.text = job.title ?: "Job Title"
        hourlyRate.text = job.hourlyRate?.let { "${it.toInt()}/hr" } ?: "N/A"
        payAmount.text = job.pay?.toString() ?: "N/A"
        jobDescription.text = job.description ?: "No description available"
        location.text = job.location ?: "Location not specified"
        contactPhone.text = job.contactPhone ?: "Phone not available"
    }

    private fun submitProof() {
        if (claimId == -1L) {
            Toast.makeText(this, "Invalid claim ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = SubmitProofRequest(
                    proofUrl = "proof_image_url", // In real app, get from camera/gallery
                    notes = "Job completed successfully"
                )
                val response = earnApi.submitProof(claimId, request)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@JobDetailsActivity, "Proof submitted successfully!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@JobDetailsActivity, "Failed to submit proof", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@JobDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
