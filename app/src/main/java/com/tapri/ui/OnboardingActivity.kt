package com.tapri.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tapri.R
import com.tapri.ui.adapter.OnboardingAdapter
import com.tapri.ui.model.OnboardingItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var continueButton: MaterialButton
    
    private var currentPage = 0
    private val totalPages = 3
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_onboarding)
            
            initializeViews()
            setupOnboardingItems()
            setupViewPager()
            setupButtons()
        } catch (e: Exception) {
            android.util.Log.e("OnboardingActivity", "Error in onboarding screen", e)
            // If onboarding fails, go directly to login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun initializeViews() {
        viewPager = findViewById(R.id.onboardingViewPager)
        tabLayout = findViewById(R.id.tabLayout)
        continueButton = findViewById(R.id.continueButton)
    }
    
    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.tapri_logo,
                "Welcome to Tapri",
                "Connect with your community and share your moments"
            ),
            OnboardingItem(
                R.drawable.onboarding_earn,
                "Share & Engage",
                "Post photos, videos and interact with friends"
            ),
            OnboardingItem(
                R.drawable.onboarding_join,
                "Join Communities",
                "Discover groups and connect with like-minded people"
            )
        )
        
        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter
    }
    
    private fun setupViewPager() {
        // Connect ViewPager with TabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Tab is automatically configured
        }.attach()
        
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                updateContinueButton()
            }
        })
    }
    
    private fun setupButtons() {
        continueButton.setOnClickListener {
            if (currentPage < totalPages - 1) {
                // Go to next page
                viewPager.currentItem = currentPage + 1
            } else {
                // Complete onboarding
                completeOnboarding()
            }
        }
    }
    
    private fun updateContinueButton() {
        if (currentPage == totalPages - 1) {
            continueButton.text = "Get Started"
        } else {
            continueButton.text = "Continue"
        }
    }
    
    private fun completeOnboarding() {
        // Mark onboarding as completed
        val sharedPrefs: SharedPreferences = getSharedPreferences("tapri_prefs", MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("has_completed_onboarding", true).apply()
        
        // Navigate to login
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}