package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class TipsActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var allTab: TextView
    private lateinit var fuelTab: TextView
    private lateinit var earningsTab: TextView
    private lateinit var safetyTab: TextView
    private lateinit var fuelTipsContainer: LinearLayout
    private lateinit var allTipsContainer: LinearLayout
    
    // Bottom navigation references
    private lateinit var homeNav: LinearLayout
    private lateinit var tapriNav: LinearLayout
    private lateinit var infoNav: LinearLayout
    private lateinit var tipsNav: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tips)

        // Find views
        backButton = findViewById(R.id.backButton)
        allTab = findViewById(R.id.allTab)
        fuelTab = findViewById(R.id.fuelTab)
        earningsTab = findViewById(R.id.earningsTab)
        safetyTab = findViewById(R.id.safetyTab)
        fuelTipsContainer = findViewById(R.id.fuelTipsContainer)
        allTipsContainer = findViewById(R.id.allTipsContainer)
        
        // Find bottom navigation
        homeNav = findViewById(R.id.homeNav)
        tapriNav = findViewById(R.id.tapriNav)
        infoNav = findViewById(R.id.infoNav)
        tipsNav = findViewById(R.id.tipsNav)

        // Set up click listeners
        setupClickListeners()
        setupTipCardListeners()
        
        // Set initial tab state (All selected by default)
        setTabSelected(allTab)
        showAllTips()
    }
    
    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Tab clicks
        allTab.setOnClickListener {
            setTabSelected(allTab)
            showAllTips()
        }
        
        fuelTab.setOnClickListener {
            setTabSelected(fuelTab)
            showFuelTips()
        }
        
        earningsTab.setOnClickListener {
            setTabSelected(earningsTab)
            showEarningsTips()
        }
        
        safetyTab.setOnClickListener {
            setTabSelected(safetyTab)
            showSafetyTips()
        }
        
        // Set up bottom navigation
        setupBottomNavigation()
    }
    
    private fun setupTipCardListeners() {
        // Fuel tip cards
        findViewById<LinearLayout>(R.id.fuelTipCard1).setOnClickListener {
            val intent = Intent(this, TipDetailsActivity::class.java)
            intent.putExtra("tip_type", "fuel")
            intent.putExtra("tip_title", "Save fuel with smooth acceleration")
            startActivity(intent)
        }
        
        findViewById<LinearLayout>(R.id.fuelTipCard2).setOnClickListener {
            val intent = Intent(this, TipDetailsActivity::class.java)
            intent.putExtra("tip_type", "thunder")
            intent.putExtra("tip_title", "Maintain your vehicle regularly")
            startActivity(intent)
        }
        
        findViewById<LinearLayout>(R.id.fuelTipCard3).setOnClickListener {
            val intent = Intent(this, TipDetailsActivity::class.java)
            intent.putExtra("tip_type", "tyre")
            intent.putExtra("tip_title", "Quick tyre check every morning")
            startActivity(intent)
        }
        
        // All tip cards
        findViewById<LinearLayout>(R.id.allTipCard1).setOnClickListener {
            val intent = Intent(this, TipDetailsActivity::class.java)
            intent.putExtra("tip_type", "safety")
            intent.putExtra("tip_title", "Always wear your seatbelt")
            startActivity(intent)
        }
        
        findViewById<LinearLayout>(R.id.allTipCard2).setOnClickListener {
            val intent = Intent(this, TipDetailsActivity::class.java)
            intent.putExtra("tip_type", "safety")
            intent.putExtra("tip_title", "Follow traffic rules")
            startActivity(intent)
        }
    }
    
    private fun setTabSelected(selectedTab: TextView) {
        // Reset all tabs to unselected state
        allTab.setBackgroundResource(R.drawable.tab_unselected_background)
        allTab.setTextColor(resources.getColor(android.R.color.black, null))
        
        fuelTab.setBackgroundResource(R.drawable.tab_unselected_background)
        fuelTab.setTextColor(resources.getColor(android.R.color.black, null))
        
        earningsTab.setBackgroundResource(R.drawable.tab_unselected_background)
        earningsTab.setTextColor(resources.getColor(android.R.color.black, null))
        
        safetyTab.setBackgroundResource(R.drawable.tab_unselected_background)
        safetyTab.setTextColor(resources.getColor(android.R.color.black, null))
        
        // Set selected tab
        selectedTab.setBackgroundResource(R.drawable.tab_selected_background)
        selectedTab.setTextColor(resources.getColor(android.R.color.white, null))
    }
    
    private fun showAllTips() {
        fuelTipsContainer.visibility = View.VISIBLE
        allTipsContainer.visibility = View.VISIBLE
    }
    
    private fun showFuelTips() {
        fuelTipsContainer.visibility = View.VISIBLE
        allTipsContainer.visibility = View.GONE
    }
    
    private fun showEarningsTips() {
        fuelTipsContainer.visibility = View.GONE
        allTipsContainer.visibility = View.GONE
        // TODO: Add earnings tips container
    }
    
    private fun showSafetyTips() {
        fuelTipsContainer.visibility = View.GONE
        allTipsContainer.visibility = View.GONE
        // TODO: Add safety tips container
    }
    
    private fun setupBottomNavigation() {
        // Set tips as selected by default
        updateNavigationSelection(homeNav, false)
        updateNavigationSelection(tapriNav, false)
        updateNavigationSelection(infoNav, false)
        updateNavigationSelection(tipsNav, true)
        
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
        
        infoNav.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        tipsNav.setOnClickListener {
            // Already on tips screen
        }
    }
    
    private fun updateNavigationSelection(navItem: LinearLayout, isSelected: Boolean) {
        val icon = navItem.getChildAt(0) as ImageView
        val text = navItem.getChildAt(1) as TextView
        
        if (isSelected) {
            icon.setColorFilter(resources.getColor(R.color.red, null))
            text.setTextColor(resources.getColor(R.color.red, null))
        } else {
            icon.setColorFilter(resources.getColor(android.R.color.black, null))
            text.setTextColor(resources.getColor(android.R.color.black, null))
        }
    }
} 