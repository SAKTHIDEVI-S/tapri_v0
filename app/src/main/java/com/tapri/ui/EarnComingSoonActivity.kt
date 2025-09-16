package com.tapri.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.ui.ComingSoonActivity
import com.tapri.utils.FeatureFlags
import kotlin.random.Random

class EarnComingSoonActivity : AppCompatActivity() {
    
    private lateinit var carIllustration: ImageView
    private lateinit var confettiContainer: FrameLayout
    private lateinit var titleText: TextView
    private lateinit var messageText: TextView
    private lateinit var comingSoonText: TextView
    
    // Bottom navigation
    private lateinit var homeNav: LinearLayout
    private lateinit var tapriNav: LinearLayout
    private lateinit var infoNav: LinearLayout
    private lateinit var tipsNav: LinearLayout
    private lateinit var earnButton: ImageView
    
    private val confettiHandler = Handler(Looper.getMainLooper())
    private val confettiRunnable = object : Runnable {
        override fun run() {
            // Create multiple confetti pieces for blasting effect
            repeat(3) { index ->
                confettiHandler.postDelayed({
                    createConfettiPiece()
                }, index * 50L) // Stagger the confetti pieces slightly
            }
            confettiHandler.postDelayed(this, 800) // Create new confetti burst every 800ms
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earn_coming_soon)
        
        initializeViews()
        setupAnimations()
        setupNavigation()
        startConfettiAnimation()
    }
    
    private fun initializeViews() {
        carIllustration = findViewById(R.id.carIllustration)
        confettiContainer = findViewById(R.id.confettiContainer)
        titleText = findViewById(R.id.titleText)
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
        // Text animations
        setupTextAnimations()
        
        // Start confetti animation immediately
        startConfettiAnimation()
    }
    
    
    private fun setupTextAnimations() {
        // Fade in animations for text
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        
        Handler(Looper.getMainLooper()).postDelayed({
            titleText.startAnimation(fadeInAnimation)
        }, 500)
        
        Handler(Looper.getMainLooper()).postDelayed({
            messageText.startAnimation(fadeInAnimation)
        }, 800)
        
        Handler(Looper.getMainLooper()).postDelayed({
            comingSoonText.startAnimation(fadeInAnimation)
        }, 1100)
    }
    
    private fun startConfettiAnimation() {
        confettiHandler.post(confettiRunnable)
    }
    
    private fun createConfettiPiece() {
        val confetti = View(this)
        val size = Random.nextInt(12, 24) // Larger confetti pieces for more impact
        confetti.layoutParams = FrameLayout.LayoutParams(size, size)
        
        // Random position at top of screen with some horizontal spread
        confetti.x = Random.nextFloat() * confettiContainer.width
        confetti.y = -size.toFloat()
        
        // Random color
        val colors = listOf(
            R.drawable.confetti_red,
            R.drawable.confetti_yellow,
            R.drawable.confetti_blue
        )
        confetti.setBackgroundResource(colors.random())
        
        confettiContainer.addView(confetti)
        
        // Create more dynamic falling animation
        val fallAnimation = AnimationUtils.loadAnimation(this, R.anim.confetti_fall)
        fallAnimation.duration = Random.nextLong(2000, 4000) // Faster fall for more blasting effect
        confetti.startAnimation(fallAnimation)
        
        // Remove confetti after animation
        fallAnimation.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                confettiContainer.removeView(confetti)
            }
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
        })
    }
    
    private fun setupNavigation() {
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
            if (FeatureFlags.SHOW_COMING_SOON_INFO) {
                val intent = Intent(this, ComingSoonActivity::class.java)
                intent.putExtra("screen_type", "info")
                startActivity(intent)
            } else {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
        
        tipsNav.setOnClickListener {
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        earnButton.setOnClickListener {
            // Already on earn page, do nothing
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        confettiHandler.removeCallbacks(confettiRunnable)
    }
}
