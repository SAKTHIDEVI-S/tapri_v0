package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.tapri.R
import com.tapri.utils.AnimationUtils

class SplashActivity : AppCompatActivity() {
    
    private lateinit var lottieAnimationView: LottieAnimationView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Make fullscreen - simplified approach to avoid crashes
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            
            setContentView(R.layout.activity_splash)
            
            // Initialize Lottie animation
            lottieAnimationView = findViewById(R.id.lottieAnimationView)
            
            // Set animation speed to be slower (0.5x speed)
            lottieAnimationView.speed = 0.5f
            
            // Set up animation listener
            lottieAnimationView.addAnimatorListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {
                    // Add entrance animation to the logo with scale effect
                    lottieAnimationView.alpha = 0f
                    lottieAnimationView.scaleX = 0.8f
                    lottieAnimationView.scaleY = 0.8f
                    
                    lottieAnimationView.animate()
                        .alpha(1f)
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(800)
                        .setInterpolator(android.view.animation.OvershootInterpolator(1.2f))
                        .start()
                }
                
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // Add smooth exit animation before navigation
                    lottieAnimationView.animate()
                        .alpha(0f)
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(400)
                        .setInterpolator(android.view.animation.DecelerateInterpolator())
                        .withEndAction {
                            navigateToMainActivity()
                        }
                        .start()
                }
                
                override fun onAnimationCancel(animation: android.animation.Animator) {
                    // Animation cancelled - still navigate after delay
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        navigateToMainActivity()
                    }, 1000)
                }
                
                override fun onAnimationRepeat(animation: android.animation.Animator) {
                    // Animation repeated
                }
            })
            
            // Start animation
            lottieAnimationView.playAnimation()
            
        } catch (e: Exception) {
            // If anything fails, just navigate to the main activity
            android.util.Log.e("SplashActivity", "Error in splash screen", e)
            navigateToMainActivity()
        }
    }
    
    private fun navigateToMainActivity() {
        try {
            // Check if user has completed onboarding
            val sharedPrefs = getSharedPreferences("tapri_prefs", MODE_PRIVATE)
            val hasCompletedOnboarding = sharedPrefs.getBoolean("has_completed_onboarding", false)
            
            val intent = if (hasCompletedOnboarding) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, OnboardingActivity::class.java)
            }
            
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // If navigation fails, try to go to onboarding as fallback
            android.util.Log.e("SplashActivity", "Error navigating from splash", e)
            try {
                val fallbackIntent = Intent(this, OnboardingActivity::class.java)
                startActivity(fallbackIntent)
                finish()
            } catch (e2: Exception) {
                android.util.Log.e("SplashActivity", "Fallback navigation also failed", e2)
                finish()
            }
        }
    }
    
    override fun onBackPressed() {
        // Disable back button during splash
        // Do nothing
    }
}
