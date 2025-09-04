package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

data class ClaimedJob(
    val title: String,
    val timeLeft: String,
    val jobType: String
)

class EarnActivity : AppCompatActivity() {
    
    private lateinit var backButton: TextView
    private lateinit var nowTab: TextView
    private lateinit var tomorrowTab: TextView
    private lateinit var nextWeekTab: TextView
    private lateinit var postJobButton: LinearLayout
    private lateinit var referNowButton: TextView
    private lateinit var bottomPopupCard: LinearLayout
    private lateinit var ongoingJobTitle: TextView
    private lateinit var ongoingJobTime: TextView
    private lateinit var viewJobButton: TextView
    
    private val claimedJobs = mutableListOf<ClaimedJob>()
    
    // Track which specific jobs are claimed
    private val claimedJobIds = mutableSetOf<Int>()
    
    // Job card and view details references
    private lateinit var jobCard1: LinearLayout
    private lateinit var jobCard2: LinearLayout
    private lateinit var jobCard3: LinearLayout
    private lateinit var viewJobDetails1: TextView
    private lateinit var viewJobDetails2: TextView
    private lateinit var viewJobDetails3: TextView
    
    // Bottom navigation references
    private lateinit var homeNav: LinearLayout
    private lateinit var tapriNav: LinearLayout
    private lateinit var infoNav: LinearLayout
    private lateinit var tipsNav: LinearLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earn)

        try {
            // Find views
            backButton = findViewById(R.id.backButton)
            nowTab = findViewById(R.id.nowTab)
            tomorrowTab = findViewById(R.id.tomorrowTab)
            nextWeekTab = findViewById(R.id.nextWeekTab)
            postJobButton = findViewById(R.id.postJobButton)
            referNowButton = findViewById(R.id.referNowButton)
            bottomPopupCard = findViewById(R.id.bottomPopupCard)
            ongoingJobTitle = findViewById(R.id.ongoingJobTitle)
            ongoingJobTime = findViewById(R.id.ongoingJobTime)
            viewJobButton = findViewById(R.id.viewJobButton)
            
            // Find job cards and view details
            jobCard1 = findViewById(R.id.jobCard1)
            jobCard2 = findViewById(R.id.jobCard2)
            jobCard3 = findViewById(R.id.jobCard3)
            viewJobDetails1 = findViewById(R.id.viewJobDetails1)
            viewJobDetails2 = findViewById(R.id.viewJobDetails2)
            viewJobDetails3 = findViewById(R.id.viewJobDetails3)
            
            // Find bottom navigation
            homeNav = findViewById(R.id.homeNav)
            tapriNav = findViewById(R.id.tapriNav)
            infoNav = findViewById(R.id.infoNav)
            tipsNav = findViewById(R.id.tipsNav)

            // Set up click listeners
            setupClickListeners()
            
            // Set initial tab state
            setTabSelected(nowTab)
            
            // Initially hide the bottom popup card
            bottomPopupCard.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading Earn screen: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupClickListeners() {
        try {
            // Back button
            backButton.setOnClickListener {
                finish()
            }
            
            // Tab clicks
            nowTab.setOnClickListener {
                setTabSelected(nowTab)
                loadJobsForTab("now")
            }
            
            tomorrowTab.setOnClickListener {
                setTabSelected(tomorrowTab)
                loadJobsForTab("tomorrow")
            }
            
            nextWeekTab.setOnClickListener {
                setTabSelected(nextWeekTab)
                loadJobsForTab("next_week")
            }
            
            // Post Job button
            postJobButton.setOnClickListener {
                val intent = Intent(this, PostJobActivity::class.java)
                startActivity(intent)
            }
            
            // Refer Now button
            referNowButton.setOnClickListener {
                Toast.makeText(this, "Refer Now", Toast.LENGTH_SHORT).show()
            }
            
            // View Job button in popup (always goes to submit proof screen for claimed jobs)
            viewJobButton.setOnClickListener {
                val intent = Intent(this, JobDetailsActivity::class.java)
                startActivity(intent)
            }
            
            // Set up claim button click listeners
            setupClaimButtonListeners()
            
            // Set up job card and view details click listeners
            setupJobCardListeners()
            
            // Set up bottom navigation
            setupBottomNavigation()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up click listeners: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupClaimButtonListeners() {
        // Find all claim buttons by their IDs
        val claimButtonIds = listOf(
            R.id.claimButton1,
            R.id.claimButton2,
            R.id.claimButton3
        )
        
        claimButtonIds.forEachIndexed { index, buttonId ->
            try {
                val claimButton = findViewById<TextView>(buttonId)
                claimButton.setOnClickListener {
                    handleClaimButtonClick(claimButton, index + 1) // Job ID is index + 1
                }
            } catch (e: Exception) {
                // Button might not exist, continue
            }
        }
    }
    
    private fun setupJobCardListeners() {
        // Job card 1
        jobCard1.setOnClickListener {
            navigateToJobScreen(1)
        }
        viewJobDetails1.setOnClickListener {
            navigateToJobScreen(1)
        }
        
        // Job card 2
        jobCard2.setOnClickListener {
            navigateToJobScreen(2)
        }
        viewJobDetails2.setOnClickListener {
            navigateToJobScreen(2)
        }
        
        // Job card 3
        jobCard3.setOnClickListener {
            navigateToJobScreen(3)
        }
        viewJobDetails3.setOnClickListener {
            navigateToJobScreen(3)
        }
    }
    
    private fun navigateToJobScreen(jobId: Int) {
        val isJobClaimed = claimedJobIds.contains(jobId)
        
        if (isJobClaimed) {
            // Job is claimed - go to submit proof screen
            val intent = Intent(this, JobDetailsActivity::class.java)
            startActivity(intent)
        } else {
            // Job is not claimed - go to job details screen with claim button
            val intent = Intent(this, UnclaimedJobDetailsActivity::class.java)
            startActivity(intent)
        }
    }
    
    companion object {
        private const val CLAIM_REQUEST_CODE = 1001
    }
    
    private fun handleClaimButtonClick(claimButton: TextView, jobId: Int) {
        // Change text from "Claim" to "Claimed"
        claimButton.text = "Claimed"
        
        // Mark this specific job as claimed
        claimedJobIds.add(jobId)
        
        // Add job to claimed jobs list
        val claimedJob = ClaimedJob(
            title = "Ongoing : Grocery Delivery",
            timeLeft = "45 mins left",
            jobType = "Grocery Delivery"
        )
        claimedJobs.add(claimedJob)
        
        // Show the bottom popup card with the most recent job
        showBottomPopupCard(claimedJob)
        
        Toast.makeText(this, "Job claimed successfully!", Toast.LENGTH_SHORT).show()
    }
    
    private fun showBottomPopupCard(claimedJob: ClaimedJob) {
        // Show the popup card
        bottomPopupCard.visibility = View.VISIBLE
        
        // Update popup to show multiple jobs if more than one
        if (claimedJobs.size > 1) {
            updatePopupForMultipleJobs()
        } else {
            // Show single job view
            val singleJobView = findViewById<LinearLayout>(R.id.singleJobView)
            val multipleJobsScrollView = findViewById<HorizontalScrollView>(R.id.multipleJobsScrollView)
            val dotsContainer = findViewById<LinearLayout>(R.id.carouselDotsContainer)
            
            singleJobView.visibility = View.VISIBLE
            multipleJobsScrollView.visibility = View.GONE
            dotsContainer.visibility = View.GONE
            
            // Set the job details in the popup
            ongoingJobTitle.text = claimedJob.title
            ongoingJobTime.text = claimedJob.timeLeft
        }
    }
    
    private fun updatePopupForMultipleJobs() {
        // Create a carousel-style layout for multiple jobs
        val singleJobView = findViewById<LinearLayout>(R.id.singleJobView)
        val multipleJobsScrollView = findViewById<HorizontalScrollView>(R.id.multipleJobsScrollView)
        val container = findViewById<LinearLayout>(R.id.claimedJobsContainer)
        val dotsContainer = findViewById<LinearLayout>(R.id.carouselDotsContainer)
        
        // Hide single job view, show multiple jobs scroll view
        singleJobView.visibility = View.GONE
        multipleJobsScrollView.visibility = View.VISIBLE
        dotsContainer.visibility = View.VISIBLE
        
        // Clear existing views
        container.removeAllViews()
        dotsContainer.removeAllViews()
        
        // Add views for each claimed job
        claimedJobs.forEachIndexed { index, job ->
            val jobView = createJobView(job)
            container.addView(jobView)
            
            // Add dot indicator
            val dot = createDotIndicator(index == 0) // First dot is active
            dotsContainer.addView(dot)
        }
        
        // Set up scroll listener to update active dot
        setupScrollListener(multipleJobsScrollView, container, dotsContainer)
    }
    
    private fun setupScrollListener(scrollView: HorizontalScrollView, container: LinearLayout, dotsContainer: LinearLayout) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrollX = scrollView.scrollX
            val cardWidth = (resources.displayMetrics.widthPixels * 0.85).toInt() + 16 // card width + margin
            
            // Calculate which card is currently most visible with better precision
            val activeIndex = if (claimedJobs.size > 1) {
                val normalizedScroll = scrollX.toFloat() / cardWidth
                val roundedIndex = Math.round(normalizedScroll).toInt()
                roundedIndex.coerceIn(0, claimedJobs.size - 1)
            } else {
                0
            }
            
            // Update dot indicators
            for (i in 0 until dotsContainer.childCount) {
                val dot = dotsContainer.getChildAt(i)
                dot.background = getDrawable(if (i == activeIndex) R.drawable.dot_active else R.drawable.dot_inactive)
            }
        }
    }
    
    private fun createDotIndicator(isActive: Boolean): View {
        val dot = View(this)
        val size = (8 * resources.displayMetrics.density).toInt() // 8dp
        val margin = (4 * resources.displayMetrics.density).toInt() // 4dp
        
        val params = LinearLayout.LayoutParams(size, size)
        params.marginEnd = margin
        
        dot.layoutParams = params
        dot.background = getDrawable(if (isActive) R.drawable.dot_active else R.drawable.dot_inactive)
        
        return dot
    }
    
    private fun createJobView(job: ClaimedJob): LinearLayout {
        val jobView = LinearLayout(this)
        val screenWidth = resources.displayMetrics.widthPixels
        val cardWidth = (screenWidth * 0.85).toInt() // 85% of screen width
        
        jobView.layoutParams = LinearLayout.LayoutParams(
            cardWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginEnd = 16
        }
        jobView.orientation = LinearLayout.HORIZONTAL
        jobView.gravity = android.view.Gravity.CENTER_VERTICAL
        jobView.setPadding(12, 12, 12, 12)
        jobView.background = getDrawable(R.drawable.job_card_background)
        jobView.elevation = 16f
        
        // Job icon
        val icon = ImageView(this)
        icon.layoutParams = LinearLayout.LayoutParams(32, 32).apply {
            marginEnd = 12
        }
        icon.setImageResource(R.drawable.ongoing)
        icon.scaleType = ImageView.ScaleType.FIT_CENTER
        jobView.addView(icon)
        
        // Job details
        val detailsLayout = LinearLayout(this)
        detailsLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            marginStart = 0
        }
        detailsLayout.orientation = LinearLayout.VERTICAL
        
        val titleText = TextView(this)
        titleText.text = job.title
        titleText.textSize = 16f
        titleText.setTextColor(resources.getColor(android.R.color.black, null))
        titleText.setTypeface(null, android.graphics.Typeface.BOLD)
        titleText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = 2
        }
        detailsLayout.addView(titleText)
        
        val timeText = TextView(this)
        timeText.text = job.timeLeft
        timeText.textSize = 14f
        timeText.setTextColor(resources.getColor(android.R.color.darker_gray, null))
        detailsLayout.addView(timeText)
        
        jobView.addView(detailsLayout)
        
        // Add View button
        val viewButton = TextView(this)
        viewButton.text = "View"
        viewButton.textSize = 14f
        viewButton.setTextColor(resources.getColor(android.R.color.white, null))
        viewButton.setTypeface(null, android.graphics.Typeface.BOLD)
        viewButton.background = getDrawable(R.drawable.claim_button_background)
        viewButton.gravity = android.view.Gravity.CENTER
        viewButton.isClickable = true
        viewButton.isFocusable = true
        viewButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            36
        ).apply {
            marginStart = 16
        }
        viewButton.setPadding(16, 0, 16, 0)
        viewButton.minWidth = 80
        
        viewButton.setOnClickListener {
            val intent = Intent(this, JobDetailsActivity::class.java)
            startActivity(intent)
        }
        
        jobView.addView(viewButton)
        
        return jobView
    }
    
    private fun setTabSelected(selectedTab: TextView) {
        // Reset all tabs to unselected state
        nowTab.setBackgroundResource(R.drawable.tab_unselected_background)
        nowTab.setTextColor(resources.getColor(android.R.color.black, null))
        nowTab.textSize = 16f
        
        tomorrowTab.setBackgroundResource(R.drawable.tab_unselected_background)
        tomorrowTab.setTextColor(resources.getColor(android.R.color.black, null))
        tomorrowTab.textSize = 16f
        
        nextWeekTab.setBackgroundResource(R.drawable.tab_unselected_background)
        nextWeekTab.setTextColor(resources.getColor(android.R.color.black, null))
        nextWeekTab.textSize = 16f
        
        // Set selected tab
        selectedTab.setBackgroundResource(R.drawable.tab_selected_background)
        selectedTab.setTextColor(resources.getColor(android.R.color.white, null))
        selectedTab.textSize = 16f
    }
    
    private fun loadJobsForTab(tabType: String) {
        // This would load different jobs based on the selected tab
        // For now, we'll just show a toast
        val tabName = when(tabType) {
            "now" -> "Now"
            "tomorrow" -> "Tomorrow"
            "next_week" -> "Next Week"
            else -> "Unknown"
        }
        Toast.makeText(this, "Loading jobs for $tabName", Toast.LENGTH_SHORT).show()
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == CLAIM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Job was claimed from detailed view
            handleJobClaimedFromDetailView()
        }
    }
    
    private fun handleJobClaimedFromDetailView() {
        // Find the first unclaimed job and mark it as claimed
        val claimButtons = listOf(
            findViewById<TextView>(R.id.claimButton1),
            findViewById<TextView>(R.id.claimButton2),
            findViewById<TextView>(R.id.claimButton3)
        )
        
        for ((index, claimButton) in claimButtons.withIndex()) {
            if (claimButton.text == "Claim") {
                handleClaimButtonClick(claimButton, index + 1)
                break
            }
        }
    }
    
    private fun setupBottomNavigation() {
        // Set earn as selected by default
        updateNavigationSelection(homeNav, false)
        updateNavigationSelection(tapriNav, false)
        updateNavigationSelection(infoNav, false)
        updateNavigationSelection(tipsNav, false)
        
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
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            finish()
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