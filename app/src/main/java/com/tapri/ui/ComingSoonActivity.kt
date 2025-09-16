package com.tapri.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.utils.AnimationUtils

class ComingSoonActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var titleText: TextView
    private lateinit var rocketIcon: ImageView
    private lateinit var messageText: TextView
    private lateinit var comingSoonText: TextView
    
    // Bottom navigation
    private lateinit var homeNav: LinearLayout
    private lateinit var tapriNav: LinearLayout
    private lateinit var infoNav: LinearLayout
    private lateinit var tipsNav: LinearLayout
    private lateinit var earnButton: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coming_soon)
        
        initializeViews()
        setupAnimations()
        setupClickListeners()
        setupBottomNavigation()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        titleText = findViewById(R.id.titleText)
        rocketIcon = findViewById(R.id.rocketIcon)
        messageText = findViewById(R.id.messageText)
        comingSoonText = findViewById(R.id.comingSoonText)
        
        // Bottom navigation
        homeNav = findViewById(R.id.homeNav)
        tapriNav = findViewById(R.id.tapriNav)
        infoNav = findViewById(R.id.infoNav)
        tipsNav = findViewById(R.id.tipsNav)
        earnButton = findViewById(R.id.earnButton)
    }
    
    private fun setupAnimations() {
        // Set message based on screen type
        val currentScreen = intent.getStringExtra("screen_type") ?: "earn"
        when (currentScreen) {
            "earn" -> {
                messageText.text = "Earnings section is coming soon 🚀"
            }
            "info" -> {
                messageText.text = "Info section is coming soon 🚀"
            }
        }
        
        // Start rocket animation with scale and floating effect
        rocketIcon.alpha = 0f
        rocketIcon.scaleX = 0.5f
        rocketIcon.scaleY = 0.5f
        rocketIcon.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setInterpolator(android.view.animation.OvershootInterpolator(1.5f))
            .withEndAction {
                // Start continuous floating animation
                startFloatingAnimation(rocketIcon)
            }
            .start()
        
        // Start text fade-in animation with slide up
        titleText.alpha = 0f
        titleText.translationY = 50f
        titleText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()
        
        // Delayed animations for other elements with staggered effect
        Handler(Looper.getMainLooper()).postDelayed({
            messageText.alpha = 0f
            messageText.translationY = 30f
            messageText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .start()
        }, 300)
        
        Handler(Looper.getMainLooper()).postDelayed({
            comingSoonText.alpha = 0f
            comingSoonText.translationY = 30f
            comingSoonText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(android.view.animation.DecelerateInterpolator())
                .withEndAction {
                    // Start pulsing animation for "Stay Tuned" text
                    startPulsingAnimation(comingSoonText)
                }
                .start()
        }, 600)
    }
    
    private fun startFloatingAnimation(view: android.view.View) {
        val floatUp = ObjectAnimator.ofFloat(view, "translationY", 0f, -20f)
        val floatDown = ObjectAnimator.ofFloat(view, "translationY", -20f, 0f)
        
        floatUp.duration = 2000
        floatDown.duration = 2000
        
        floatUp.interpolator = android.view.animation.DecelerateInterpolator()
        floatDown.interpolator = android.view.animation.AccelerateInterpolator()
        
        floatUp.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                floatDown.start()
            }
        })
        
        floatDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                floatUp.start()
            }
        })
        
        floatUp.start()
    }
    
    private fun startPulsingAnimation(view: android.view.View) {
        val pulseUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f)
        val pulseUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f)
        val pulseDown = ObjectAnimator.ofFloat(view, "scaleX", 1.05f, 1f)
        val pulseDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.05f, 1f)
        
        pulseUp.duration = 1000
        pulseUpY.duration = 1000
        pulseDown.duration = 1000
        pulseDownY.duration = 1000
        
        pulseUp.interpolator = android.view.animation.DecelerateInterpolator()
        pulseUpY.interpolator = android.view.animation.DecelerateInterpolator()
        pulseDown.interpolator = android.view.animation.AccelerateInterpolator()
        pulseDownY.interpolator = android.view.animation.AccelerateInterpolator()
        
        pulseUp.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                pulseDown.start()
                pulseDownY.start()
            }
        })
        
        pulseDown.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                pulseUp.start()
                pulseUpY.start()
            }
        })
        
        pulseUp.start()
        pulseUpY.start()
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
    }
    
    private fun setupBottomNavigation() {
        // Set appropriate tab as selected based on which screen this is
        val currentScreen = intent.getStringExtra("screen_type") ?: "earn"
        
        when (currentScreen) {
            "earn" -> updateNavigationSelection(earnButton, true)
            "info" -> updateNavigationSelection(infoNav, true)
        }
        
        homeNav.setOnClickListener {
            AnimationUtils.animateButtonPress(homeNav) {
                val intent = Intent(this@ComingSoonActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        tapriNav.setOnClickListener {
            AnimationUtils.animateButtonPress(tapriNav) {
                val intent = Intent(this@ComingSoonActivity, GroupsActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        earnButton.setOnClickListener {
            AnimationUtils.animateButtonPress(earnButton) {
                // Already on earn screen (coming soon)
            }
        }
        
        infoNav.setOnClickListener {
            AnimationUtils.animateButtonPress(infoNav) {
                val intent = Intent(this@ComingSoonActivity, ComingSoonActivity::class.java)
                intent.putExtra("screen_type", "info")
                startActivity(intent)
                finish()
            }
        }
        
        tipsNav.setOnClickListener {
            AnimationUtils.animateButtonPress(tipsNav) {
                val intent = Intent(this@ComingSoonActivity, TipsActivity::class.java)
                startActivity(intent)
                finish()
            }
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
    
    private fun updateNavigationSelection(earnButton: ImageView, isSelected: Boolean) {
        if (isSelected) {
            earnButton.setColorFilter(resources.getColor(R.color.red, null))
        } else {
            earnButton.setColorFilter(resources.getColor(android.R.color.white, null))
        }
    }
}
