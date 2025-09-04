package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class PostJobActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var postJobButton: TextView
    private lateinit var jobTitleInput: EditText
    private lateinit var jobDescriptionInput: EditText
    private lateinit var hourlyRateInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var durationInput: EditText
    private lateinit var contactInput: EditText
    private lateinit var jobTypeSpinner: Spinner
    private lateinit var requirementsInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_job)

        initializeViews()
        setupJobTypeSpinner()
        setupClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        postJobButton = findViewById(R.id.postJobButton)
        jobTitleInput = findViewById(R.id.jobTitleInput)
        jobDescriptionInput = findViewById(R.id.jobDescriptionInput)
        hourlyRateInput = findViewById(R.id.hourlyRateInput)
        locationInput = findViewById(R.id.locationInput)
        durationInput = findViewById(R.id.durationInput)
        contactInput = findViewById(R.id.contactInput)
        jobTypeSpinner = findViewById(R.id.jobTypeSpinner)
        requirementsInput = findViewById(R.id.requirementsInput)
    }

    private fun setupJobTypeSpinner() {
        val jobTypes = arrayOf("Grocery Delivery", "Food Delivery", "Package Delivery", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jobTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        jobTypeSpinner.adapter = adapter
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        postJobButton.setOnClickListener {
            if (validateInputs()) {
                // Here you would typically send the job data to your backend
                Toast.makeText(this, "Job posted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun validateInputs(): Boolean {
        if (jobTitleInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a job title", Toast.LENGTH_SHORT).show()
            return false
        }

        if (jobDescriptionInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a job description", Toast.LENGTH_SHORT).show()
            return false
        }

        if (hourlyRateInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter an hourly rate", Toast.LENGTH_SHORT).show()
            return false
        }

        if (locationInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
            return false
        }

        if (durationInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter job duration", Toast.LENGTH_SHORT).show()
            return false
        }

        if (contactInput.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter a contact number", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
} 