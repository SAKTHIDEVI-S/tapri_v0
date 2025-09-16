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
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UnclaimedJobDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var callButton: TextView
    private lateinit var claimButton: TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unclaimed_job_details)

        // Get job ID from intent
        jobId = intent.getLongExtra("jobId", -1)

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
        claimButton = findViewById(R.id.claimButton)
        
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

        claimButton.setOnClickListener {
            claimJob()
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
                        Toast.makeText(this@UnclaimedJobDetailsActivity, "Failed to load job details", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UnclaimedJobDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
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

    private fun claimJob() {
        if (jobId == -1L) {
            Toast.makeText(this, "Invalid job ID", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = earnApi.claimJob(jobId)
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UnclaimedJobDetailsActivity, "Job claimed successfully!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@UnclaimedJobDetailsActivity, "Failed to claim job", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UnclaimedJobDetailsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
