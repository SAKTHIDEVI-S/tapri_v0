package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.EarnApi
import com.tapri.network.JobItem
import com.tapri.ui.adapter.JobsAdapter
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.Duration
import java.time.format.DateTimeFormatter

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

    private lateinit var jobsAdapter: JobsAdapter
    
    // New properties for claim flow
    private var claimedJobId: Long? = null
    private var currentClaimId: Long? = null
    private var timer: CountDownTimer? = null
    private val claimedJobs = mutableListOf<ClaimedJob>()
    
    // Networking
    private lateinit var sessionManager: SessionManager
    private lateinit var earnApi: EarnApi
    private var jobs: List<JobItem> = emptyList()
    
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

            // Bottom nav views
            homeNav = findViewById(R.id.homeNav)
            tapriNav = findViewById(R.id.tapriNav)
            infoNav = findViewById(R.id.infoNav)
            tipsNav = findViewById(R.id.tipsNav)

            // Recycler
            val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.jobsRecycler)
            recycler.layoutManager = LinearLayoutManager(this)
            jobsAdapter = JobsAdapter(emptyList(), emptySet(), onDetails = { item ->
                handleJobDetails(item)
            }, onClaim = { item ->
                claimJob(item)
            })
            recycler.adapter = jobsAdapter
            
            // Setup network
            sessionManager = SessionManager(this)
            earnApi = ApiClient.earnRetrofit(sessionManager).create(EarnApi::class.java)

            // Set up click listeners
            setupClickListeners()
            
            // Set initial tab state
            setTabSelected(nowTab)
            
            // Initially hide the bottom popup card
            bottomPopupCard.visibility = View.GONE

            // Load jobs from backend
            fetchJobs()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading Earn screen: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun handleJobDetails(item: JobItem) {
        if (claimedJobId == item.id) {
            // Go to submit proof screen
            val intent = Intent(this, JobDetailsActivity::class.java)
            intent.putExtra("claimId", currentClaimId)
            intent.putExtra("jobId", item.id)
            startActivity(intent)
        } else {
            // Go to unclaimed job details
            val intent = Intent(this, UnclaimedJobDetailsActivity::class.java)
            intent.putExtra("jobId", item.id)
            startActivity(intent)
        }
    }

    private fun claimJob(item: JobItem) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = earnApi.claimJob(item.id)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val claimResponse = response.body()
                        claimedJobId = item.id
                        currentClaimId = claimResponse?.claimId
                        
                        // Show bottom popup
                        showBottomPopup(item, claimResponse?.dueAt)
                        
                        // Refresh job list to show claimed state
                        fetchJobs()
                        
                        Toast.makeText(this@EarnActivity, "Claimed ${item.title}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@EarnActivity, "Failed to claim", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EarnActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showBottomPopup(job: JobItem, dueAt: String?) {
        ongoingJobTitle.text = job.title
        bottomPopupCard.visibility = View.VISIBLE
        
        dueAt?.let { startCountdown(it) }
    }

    private fun startCountdown(dueAt: String) {
        try {
            val dueTime = LocalDateTime.parse(dueAt)
            val now = LocalDateTime.now()
            val duration = Duration.between(now, dueTime)
            
            if (duration.isNegative) {
                ongoingJobTime.text = "Expired"
                return
            }
            
            timer?.cancel()
            timer = object : CountDownTimer(duration.toMillis(), 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val hours = millisUntilFinished / (1000 * 60 * 60)
                    val minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60)
                    ongoingJobTime.text = "${hours}h ${minutes}m left"
                }
                
                override fun onFinish() {
                    ongoingJobTime.text = "Time's up!"
                    bottomPopupCard.visibility = View.GONE
                }
            }.start()
        } catch (e: Exception) {
            ongoingJobTime.text = "Time unknown"
        }
    }

    private fun fetchJobs() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jobsResponse = earnApi.listJobs()
                val ongoingResponse = earnApi.getOngoingClaim()
                
                withContext(Dispatchers.Main) {
                    if (jobsResponse.isSuccessful) {
                        jobs = jobsResponse.body() ?: emptyList()
                        
                        val claimedJobs = mutableSetOf<Long>()
                        if (ongoingResponse.isSuccessful) {
                            val ongoing = ongoingResponse.body()
                            ongoing?.job?.let { 
                                claimedJobs.add(it.id)
                                claimedJobId = it.id
                                currentClaimId = ongoing.id
                                showBottomPopup(it, ongoing.dueAt)
                            }
                        }
                        
                        applyTabFilter("now", claimedJobs)
                    } else {
                        Toast.makeText(this@EarnActivity, "Failed to fetch jobs", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EarnActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        try {
            backButton.setOnClickListener { finish() }
            
            nowTab.setOnClickListener { setTabSelected(nowTab); applyTabFilter("now") }
            tomorrowTab.setOnClickListener { setTabSelected(tomorrowTab); applyTabFilter("tomorrow") }
            nextWeekTab.setOnClickListener { setTabSelected(nextWeekTab); applyTabFilter("next_week") }
            
            postJobButton.setOnClickListener {
                val intent = Intent(this, PostJobActivity::class.java)
                startActivity(intent)
            }
            
            referNowButton.setOnClickListener {
                Toast.makeText(this, "Refer Now", Toast.LENGTH_SHORT).show()
            }
            
            viewJobButton.setOnClickListener {
                if (claimedJobId != null && currentClaimId != null) {
                    val intent = Intent(this, JobDetailsActivity::class.java)
                    intent.putExtra("claimId", currentClaimId)
                    intent.putExtra("jobId", claimedJobId)
                    startActivity(intent)
                }
            }
            
            setupBottomNavigation()
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error setting up click listeners: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setTabSelected(selectedTab: TextView) {
        nowTab.setBackgroundResource(R.drawable.tab_unselected_background)
        nowTab.setTextColor(resources.getColor(android.R.color.black, null))
        nowTab.textSize = 16f
        
        tomorrowTab.setBackgroundResource(R.drawable.tab_unselected_background)
        tomorrowTab.setTextColor(resources.getColor(android.R.color.black, null))
        tomorrowTab.textSize = 16f
        
        nextWeekTab.setBackgroundResource(R.drawable.tab_unselected_background)
        nextWeekTab.setTextColor(resources.getColor(android.R.color.black, null))
        nextWeekTab.textSize = 16f
        
        selectedTab.setBackgroundResource(R.drawable.tab_selected_background)
        selectedTab.setTextColor(resources.getColor(android.R.color.white, null))
        selectedTab.textSize = 16f
    }

    private fun parseStartsAt(startsAt: String?): LocalDate? {
        if (startsAt.isNullOrBlank()) return null
        return try {
            // Try ISO first
            LocalDate.parse(startsAt.substring(0, 10))
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(startsAt).toLocalDate()
            } catch (_: Exception) {
                null
            }
        }
    }

    private fun applyTabFilter(tab: String, claimedJobs: Set<Long> = emptySet()) {
        val today = LocalDate.now()
        val items = jobs.filter { job ->
            val date = parseStartsAt(job.startsAt) ?: today
            when (tab) {
                "now" -> date.isEqual(today)
                "tomorrow" -> date.isEqual(today.plusDays(1))
                "next_week" -> date.isAfter(today.plusDays(1))
                else -> true
            }
        }
        Log.d("EarnActivity", "Tab=$tab items=${items.size}")
        jobsAdapter.update(items, claimedJobs)
        if (items.isEmpty()) Toast.makeText(this, "No jobs for selected tab", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation() {
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

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}
