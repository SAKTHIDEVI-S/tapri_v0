package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.Post
import com.tapri.ui.adapters.PostAdapter
import com.tapri.ui.ProfileActivity
import com.tapri.ui.GroupsActivity
import com.tapri.ui.EarnActivity
import com.tapri.ui.InfoActivity
import com.tapri.ui.TipsActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Find views by ID
        val postsRecyclerView = findViewById<RecyclerView>(R.id.postsRecyclerView)
        val notificationIcon = findViewById<ImageView>(R.id.notificationIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        val freeNowSwitch = findViewById<Switch>(R.id.freeNowSwitch)
        val earningsCard = findViewById<androidx.cardview.widget.CardView>(R.id.earningsCard)
        val todayEarningsText = findViewById<TextView>(R.id.todayEarningsText)

        // Custom navigation views
        val homeNav = findViewById<LinearLayout>(R.id.homeNav)
        val tapriNav = findViewById<LinearLayout>(R.id.tapriNav)
        val earnNav = findViewById<LinearLayout>(R.id.earnNav)
        val infoNav = findViewById<LinearLayout>(R.id.infoNav)
        val tipsNav = findViewById<LinearLayout>(R.id.tipsNav)
        val earnButton = findViewById<ImageView>(R.id.earnButton)

        // Dummy data for posts
        val posts = listOf(
            Post("1", "Ramesh", "2 hrs ago", "Heavy traffic on MG Road : Consider alternate routes to avoid delays...", null, 900, 5, 1),
            Post("2", "Suresh", "4 hrs ago", "Great earnings today! Uber is paying well.", null, 450, 3, 2),
            Post("3", "Mahesh", "6 hrs ago", "New surge pricing in downtown area.", null, 320, 2, 0),
            Post("4", "Rajesh", "8 hrs ago", "Best time to drive is during peak hours!", null, 780, 8, 3),
            Post("5", "Amit", "10 hrs ago", "Avoid this route during rush hour.", null, 210, 1, 0),
            Post("6", "Priya", "12 hrs ago", "BigBasket delivery opportunities in JP Nagar!", null, 650, 4, 1),
            Post("7", "Kumar", "1 day ago", "Hotspot alert: High demand in Koramangala!", null, 890, 6, 2),
            Post("8", "Lakshmi", "1 day ago", "Tips for maximizing earnings during festivals", null, 420, 3, 0)
        )

        // Set up RecyclerView for posts
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        postsRecyclerView.adapter = PostAdapter(
            this,
            posts,
            onCommentClick = { post ->
                Toast.makeText(this, "Comment clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
            },
            onSaveClick = { post ->
                post.isSaved = !post.isSaved
                Toast.makeText(this, if (post.isSaved) "Saved" else "Unsaved", Toast.LENGTH_SHORT).show()
            }
        )

        // Initialize earnings data
        updateEarningsDisplay(todayEarningsText)

        // Set up click listeners
        notificationIcon.setOnClickListener {
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        profileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Set up earnings card sections
        setupEarningsCardSections()

        earningsCard.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        // Job listings section
        val jobListingsSection = findViewById<LinearLayout>(R.id.jobListingsSection)

        // Filter buttons
        val nearbyFilter = findViewById<TextView>(R.id.nearbyFilter)
        val shortJobsFilter = findViewById<TextView>(R.id.shortJobsFilter)
        val highPayFilter = findViewById<TextView>(R.id.highPayFilter)

        // Job cards and buttons
        val groceryJobCard1 = findViewById<LinearLayout>(R.id.groceryJobCard1)
        val groceryJobCard2 = findViewById<LinearLayout>(R.id.groceryJobCard2)
        val groceryJobCard3 = findViewById<LinearLayout>(R.id.groceryJobCard3)
        val foodJobCard1 = findViewById<LinearLayout>(R.id.foodJobCard1)
        val foodJobCard2 = findViewById<LinearLayout>(R.id.foodJobCard2)

        val groceryClaimButton1 = findViewById<TextView>(R.id.groceryClaimButton1)
        val groceryClaimButton2 = findViewById<TextView>(R.id.groceryClaimButton2)
        val groceryClaimButton3 = findViewById<TextView>(R.id.groceryClaimButton3)
        val foodClaimButton1 = findViewById<TextView>(R.id.foodClaimButton1)
        val foodClaimButton2 = findViewById<TextView>(R.id.foodClaimButton2)

        val groceryViewDetails1 = findViewById<TextView>(R.id.groceryViewDetails1)
        val groceryViewDetails2 = findViewById<TextView>(R.id.groceryViewDetails2)
        val groceryViewDetails3 = findViewById<TextView>(R.id.groceryViewDetails3)
        val foodViewDetails1 = findViewById<TextView>(R.id.foodViewDetails1)
        val foodViewDetails2 = findViewById<TextView>(R.id.foodViewDetails2)

        val findJobsButton = findViewById<TextView>(R.id.findJobsButton)

        freeNowSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Show job listings, hide posts and earnings
                jobListingsSection.visibility = android.view.View.VISIBLE
                postsRecyclerView.visibility = android.view.View.GONE
                earningsCard.visibility = android.view.View.GONE
                Toast.makeText(this, "You're now available for rides", Toast.LENGTH_SHORT).show()
            } else {
                // Hide job listings, show posts and earnings
                jobListingsSection.visibility = android.view.View.GONE
                postsRecyclerView.visibility = android.view.View.VISIBLE
                earningsCard.visibility = android.view.View.VISIBLE
                Toast.makeText(this, "You're now offline", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up filter button click listeners
        nearbyFilter.setOnClickListener {
            setFilterSelected(nearbyFilter, shortJobsFilter, highPayFilter, nearbyFilter)
        }

        shortJobsFilter.setOnClickListener {
            setFilterSelected(nearbyFilter, shortJobsFilter, highPayFilter, shortJobsFilter)
        }

        highPayFilter.setOnClickListener {
            setFilterSelected(nearbyFilter, shortJobsFilter, highPayFilter, highPayFilter)
        }

        // Set up job card click listeners
        setupJobCardListeners(groceryJobCard1, groceryViewDetails1, groceryClaimButton1)
        setupJobCardListeners(groceryJobCard2, groceryViewDetails2, groceryClaimButton2)
        setupJobCardListeners(groceryJobCard3, groceryViewDetails3, groceryClaimButton3)
        setupJobCardListeners(foodJobCard1, foodViewDetails1, foodClaimButton1)
        setupJobCardListeners(foodJobCard2, foodViewDetails2, foodClaimButton2)

        // Set up find jobs button
        findJobsButton.setOnClickListener {
            Toast.makeText(this, "Finding jobs in Koramangala...", Toast.LENGTH_SHORT).show()
        }

        // Set up custom bottom navigation
        // Set home as selected by default
        updateNavigationSelection(homeNav, true)

        homeNav.setOnClickListener {
            updateNavigationSelection(homeNav, true)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
        }

        tapriNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, true)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
        }

        earnButton.setOnClickListener {
            val intent = Intent(this, EarnActivity::class.java)
            startActivity(intent)
        }

        earnNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, EarnActivity::class.java)
            startActivity(intent)
        }

        infoNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, true)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }

        tipsNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, true)
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
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

    private fun setFilterSelected(nearby: TextView, shortJobs: TextView, highPay: TextView, selected: TextView) {
        // Reset all filters
        nearby.setBackgroundResource(R.drawable.tab_unselected_background)
        nearby.setTextColor(resources.getColor(android.R.color.black, null))
        shortJobs.setBackgroundResource(R.drawable.tab_unselected_background)
        shortJobs.setTextColor(resources.getColor(android.R.color.black, null))
        highPay.setBackgroundResource(R.drawable.tab_unselected_background)
        highPay.setTextColor(resources.getColor(android.R.color.black, null))

        // Set selected filter
        selected.setBackgroundResource(R.drawable.tab_selected_background)
        selected.setTextColor(resources.getColor(android.R.color.white, null))
    }

    private fun setupJobCardListeners(jobCard: LinearLayout, viewDetails: TextView, claimButton: TextView) {
        // Job card click
        jobCard.setOnClickListener {
            val intent = Intent(this, ClaimActivity::class.java)
            startActivity(intent)
        }

        // View details click
        viewDetails.setOnClickListener {
            val intent = Intent(this, ClaimActivity::class.java)
            startActivity(intent)
        }

        // Claim button click
        claimButton.setOnClickListener {
            claimButton.text = "Claimed"
            claimButton.isEnabled = false
            Toast.makeText(this, "Job claimed successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupEarningsCardSections() {
        // Find the gift box and hotspot sections
        val giftBox = findViewById<ImageView>(R.id.giftBox)
        val hotspotSection = findViewById<LinearLayout>(R.id.hotspotSection)
        val missedOpportunitiesSection = findViewById<LinearLayout>(R.id.missedOpportunitiesSection)

        // Set up gift box click listener
        giftBox?.setOnClickListener {
            Toast.makeText(this, "Check out inDrive opportunities!", Toast.LENGTH_SHORT).show()
        }

        // Set up hotspot section click listener
        hotspotSection?.setOnClickListener {
            Toast.makeText(this, "Navigate to HSR Layout hotspot", Toast.LENGTH_SHORT).show()
        }

        // Set up missed opportunities section click listener
        missedOpportunitiesSection?.setOnClickListener {
            Toast.makeText(this, "Explore inDrive opportunities", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEarningsDisplay(earningsText: TextView) {
        // This would typically fetch from your backend or local storage
        // For now, using sample data
        val totalEarnings = 1500
        val rapidoEarnings = 900 // 60%
        val olaEarnings = 375   // 25%
        val uberEarnings = 225  // 15%

        // Update the main earnings text to match the image format
        earningsText.text = "1,500 Earned Today"

        // In a real app, you would also update the platform-specific earnings
        // by finding the TextViews for each platform and updating them
        // For now, the layout has hardcoded values that match this data
    }
}