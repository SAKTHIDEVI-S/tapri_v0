package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tapri.R
import com.tapri.ui.adapter.OnboardingAdapter
import com.tapri.ui.model.OnboardingItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val onboardingItems = listOf(
            OnboardingItem(R.drawable.onboarding_join, "Join Us", "Join community and share your experiences"),
            OnboardingItem(R.drawable.onboarding_earn, "Earn More", "Explore earning opportunities and boost your income"),
            OnboardingItem(R.drawable.onboarding_compare, "Compare", "Join community of drivers and share your experiences")
        )
        val viewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        val adapter = OnboardingAdapter(onboardingItems)
        viewPager.adapter = adapter

        val tabDots = findViewById<TabLayout>(R.id.tabDots)
        TabLayoutMediator(tabDots, viewPager) { _, _ -> }.attach()

        val continueButton = findViewById<MaterialButton>(R.id.continueButton)
        continueButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
} 