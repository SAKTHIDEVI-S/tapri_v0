package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class InfoActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var homeNav: LinearLayout
    private lateinit var tapriNav: LinearLayout
    private lateinit var infoNav: LinearLayout
    private lateinit var tipsNav: LinearLayout
    private lateinit var earnButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        initializeViews()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        homeNav = findViewById(R.id.homeNav)
        tapriNav = findViewById(R.id.tapriNav)
        infoNav = findViewById(R.id.infoNav)
        tipsNav = findViewById(R.id.tipsNav)
        earnButton = findViewById(R.id.earnButton)
    }
    
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Bottom navigation
        homeNav.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        tapriNav.setOnClickListener {
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        earnButton.setOnClickListener {
            val intent = Intent(this, EarnActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        tipsNav.setOnClickListener {
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
} 