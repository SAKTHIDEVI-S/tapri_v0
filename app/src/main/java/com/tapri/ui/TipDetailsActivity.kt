package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class TipDetailsActivity : AppCompatActivity() {

    private lateinit var backButton: TextView
    private lateinit var tipIcon: ImageView
    private lateinit var tipTitle: TextView
    private lateinit var tipCategory: TextView
    private lateinit var tipDescription: TextView
    private lateinit var tipBenefits: TextView
    private lateinit var usefulButton: TextView
    private lateinit var notUsefulButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tip_details)

        initializeViews()
        setupClickListeners()
        loadTipData()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        tipIcon = findViewById(R.id.tipIcon)
        tipTitle = findViewById(R.id.tipTitle)
        tipCategory = findViewById(R.id.tipCategory)
        tipDescription = findViewById(R.id.tipDescription)
        tipBenefits = findViewById(R.id.tipBenefits)
        usefulButton = findViewById(R.id.usefulButton)
        notUsefulButton = findViewById(R.id.notUsefulButton)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        usefulButton.setOnClickListener {
            Toast.makeText(this, "Thank you! This tip was marked as useful.", Toast.LENGTH_SHORT).show()
            usefulButton.isEnabled = false
            notUsefulButton.isEnabled = false
            usefulButton.background = getDrawable(R.drawable.primary_button_background)
            notUsefulButton.background = getDrawable(R.drawable.card_stroke_background)
        }

        notUsefulButton.setOnClickListener {
            Toast.makeText(this, "Thank you for your feedback. We'll improve our tips.", Toast.LENGTH_SHORT).show()
            usefulButton.isEnabled = false
            notUsefulButton.isEnabled = false
            notUsefulButton.background = getDrawable(R.drawable.primary_button_background)
            usefulButton.background = getDrawable(R.drawable.card_stroke_background)
        }
    }

    private fun loadTipData() {
        // Get tip data from intent extras
        val tipType = intent.getStringExtra("tip_type") ?: "fuel"
        val tipTitleText = intent.getStringExtra("tip_title") ?: "Fuel Efficiency Tips"
        
        // Set tip icon based on type
        val iconResource = when (tipType) {
            "fuel" -> R.drawable.fuel
            "thunder" -> R.drawable.thunder
            "tyre" -> R.drawable.tyre
            else -> R.drawable.fuel
        }
        tipIcon.setImageResource(iconResource)
        
        // Set tip title
        tipTitle.text = tipTitleText
        
        // Set tip category
        tipCategory.text = "Category: ${tipType.capitalize()}"
        
        // Set tip description based on type
        val description = when (tipType) {
            "fuel" -> "Maintain proper tire pressure to improve fuel efficiency. Under-inflated tires can reduce fuel economy by up to 3%. Check your tire pressure regularly and keep them inflated to the manufacturer's recommended levels. This simple maintenance task can save you money on fuel costs and extend the life of your tires."
            "thunder" -> "Regular vehicle maintenance is crucial for optimal performance and safety. Follow your vehicle's maintenance schedule and address any issues promptly. This includes regular oil changes, brake inspections, and engine tune-ups."
            "tyre" -> "Proper tire maintenance is essential for safety and performance. Regularly inspect your tires for wear, damage, and proper inflation. Rotate your tires according to the manufacturer's recommendations to ensure even wear."
            else -> "This tip provides valuable information to help you improve your driving experience and vehicle maintenance."
        }
        tipDescription.text = description
        
        // Set benefits based on type
        val benefits = when (tipType) {
            "fuel" -> "• Improved fuel efficiency (up to 3% savings)\n• Extended tire life\n• Better vehicle handling\n• Enhanced safety\n• Reduced carbon footprint"
            "thunder" -> "• Better vehicle performance\n• Increased safety\n• Lower repair costs\n• Extended vehicle life\n• Improved reliability"
            "tyre" -> "• Enhanced safety\n• Better handling\n• Extended tire life\n• Improved fuel efficiency\n• Reduced risk of blowouts"
            else -> "• Better overall vehicle performance\n• Enhanced safety\n• Cost savings\n• Improved reliability"
        }
        tipBenefits.text = benefits
    }
} 