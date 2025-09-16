package com.tapri.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

object AnimationUtils {
    
    /**
     * Apply button press animation
     */
    fun animateButtonPress(view: View, onAnimationEnd: (() -> Unit)? = null) {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.98f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.98f)
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.98f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.98f, 1f)
        
        scaleDown.duration = 80
        scaleDownY.duration = 80
        scaleUp.duration = 120
        scaleUpY.duration = 120
        
        scaleDown.interpolator = android.view.animation.AccelerateInterpolator()
        scaleUp.interpolator = DecelerateInterpolator()
        
        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleUp.start()
                scaleUpY.start()
            }
        })
        
        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }
        })
        
        scaleDown.start()
        scaleDownY.start()
    }
    
    /**
     * Fade in animation
     */
    fun fadeIn(view: View, duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { onAnimationEnd?.invoke() }
            .start()
    }
    
    /**
     * Fade out animation
     */
    fun fadeOut(view: View, duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { 
                view.visibility = View.GONE
                onAnimationEnd?.invoke()
            }
            .start()
    }
    
    /**
     * Slide in from right
     */
    fun slideInFromRight(view: View, duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        view.translationX = view.width.toFloat()
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction { onAnimationEnd?.invoke() }
            .start()
    }
    
    /**
     * Slide in from bottom
     */
    fun slideInFromBottom(view: View, duration: Long = 300, onAnimationEnd: (() -> Unit)? = null) {
        view.translationY = view.height.toFloat()
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(OvershootInterpolator(1.2f))
            .withEndAction { onAnimationEnd?.invoke() }
            .start()
    }
    
    /**
     * Bounce animation
     */
    fun bounce(view: View, duration: Long = 500, onAnimationEnd: (() -> Unit)? = null) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f)
        
        scaleX.duration = duration
        scaleY.duration = duration
        
        scaleX.interpolator = OvershootInterpolator(2f)
        scaleY.interpolator = OvershootInterpolator(2f)
        
        scaleX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }
        })
        
        scaleX.start()
        scaleY.start()
    }
    
    /**
     * Shake animation for errors
     */
    fun shake(view: View, onAnimationEnd: (() -> Unit)? = null) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 500
        shake.interpolator = DecelerateInterpolator()
        shake.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }
        })
        shake.start()
    }
    
    /**
     * Pulse animation
     */
    fun pulse(view: View, duration: Long = 1000, repeatCount: Int = -1) {
        val pulse = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f)
        val pulseY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f)
        
        pulse.duration = duration
        pulseY.duration = duration
        pulse.repeatCount = repeatCount
        pulseY.repeatCount = repeatCount
        
        pulse.interpolator = DecelerateInterpolator()
        pulseY.interpolator = DecelerateInterpolator()
        
        pulse.start()
        pulseY.start()
    }
    
    /**
     * Loading animation for buttons
     */
    fun showLoadingAnimation(view: View, isLoading: Boolean) {
        if (isLoading) {
            view.animate()
                .alpha(0.7f)
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(200)
                .start()
        } else {
            view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }
    }
    
    /**
     * Success animation
     */
    fun successAnimation(view: View, onAnimationEnd: (() -> Unit)? = null) {
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f)
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1f)
        
        scaleUp.duration = 200
        scaleUpY.duration = 200
        scaleDown.duration = 200
        scaleDownY.duration = 200
        
        scaleUp.interpolator = DecelerateInterpolator()
        scaleUpY.interpolator = DecelerateInterpolator()
        scaleDown.interpolator = OvershootInterpolator(1.5f)
        scaleDownY.interpolator = OvershootInterpolator(1.5f)
        
        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleDown.start()
                scaleDownY.start()
            }
        })
        
        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onAnimationEnd?.invoke()
            }
        })
        
        scaleUp.start()
        scaleUpY.start()
    }
    
    /**
     * Morph scale animation with overshoot effect
     */
    fun morphScale(view: View, duration: Long = 300) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.morph_scale)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Subtle press animation for important buttons
     */
    fun subtlePress(view: View, action: (() -> Unit)? = null) {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.96f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.96f)
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.96f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.96f, 1f)
        
        scaleDown.duration = 100
        scaleDownY.duration = 100
        scaleUp.duration = 150
        scaleUpY.duration = 150
        
        scaleDown.interpolator = DecelerateInterpolator()
        scaleUp.interpolator = DecelerateInterpolator()
        
        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleUp.start()
                scaleUpY.start()
            }
        })
        
        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                action?.invoke()
            }
        })
        
        scaleDown.start()
        scaleDownY.start()
    }
    
    /**
     * Slide up with morph effect
     */
    fun slideUpMorph(view: View, duration: Long = 400) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_up_morph)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Staggered entrance animation for list items
     */
    fun staggeredEntrance(view: View, position: Int, delay: Long = 100) {
        view.alpha = 0f
        view.translationY = 50f
        
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(position * delay)
            .setInterpolator(OvershootInterpolator(1.2f))
            .start()
    }
    
    /**
     * Slide in from bottom animation
     */
    fun slideInFromBottom(view: View, duration: Long = 300) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_from_bottom)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Slide out to bottom animation
     */
    fun slideOutToBottom(view: View, duration: Long = 250) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.slide_out_to_bottom)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Scale in animation with overshoot
     */
    fun scaleIn(view: View, duration: Long = 200) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.scale_in)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Scale out animation
     */
    fun scaleOut(view: View, duration: Long = 150) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.scale_out)
        animation.duration = duration
        view.startAnimation(animation)
    }
    
    /**
     * Like button animation with color change
     */
    fun likeAnimation(view: View, isLiked: Boolean, onComplete: (() -> Unit)? = null) {
        val scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.8f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.8f)
        val scaleUp = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1.2f)
        val scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1.2f)
        val scaleNormal = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f)
        val scaleNormalY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f)
        
        scaleDown.duration = 100
        scaleDownY.duration = 100
        scaleUp.duration = 100
        scaleUpY.duration = 100
        scaleNormal.duration = 100
        scaleNormalY.duration = 100
        
        scaleDown.interpolator = DecelerateInterpolator()
        scaleUp.interpolator = OvershootInterpolator(2f)
        scaleNormal.interpolator = DecelerateInterpolator()
        
        scaleDown.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleUp.start()
                scaleUpY.start()
            }
        })
        
        scaleUp.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                scaleNormal.start()
                scaleNormalY.start()
            }
        })
        
        scaleNormal.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete?.invoke()
            }
        })
        
        scaleDown.start()
        scaleDownY.start()
    }
    
    /**
     * Typing indicator animation
     */
    fun startTypingAnimation(view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.typing_dots)
        view.startAnimation(animation)
    }
    
    /**
     * Stop typing animation
     */
    fun stopTypingAnimation(view: View) {
        view.clearAnimation()
    }
    
    /**
     * Parallax scroll effect
     */
    fun parallaxScroll(view: View, scrollY: Float, maxTranslation: Float = 100f) {
        val translation = -scrollY * 0.5f
        val clampedTranslation = translation.coerceIn(-maxTranslation, maxTranslation)
        view.translationY = clampedTranslation
    }
    
    /**
     * Shared element transition for profile photos
     */
    fun sharedElementTransition(view: View, duration: Long = 300) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(duration / 2)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration / 2)
                    .start()
            }
            .start()
    }
}
