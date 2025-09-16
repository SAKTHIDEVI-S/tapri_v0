package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tapri.R
import com.tapri.ui.model.Post
import com.tapri.ui.model.MediaType
import com.tapri.ui.adapters.PostAdapter
import com.tapri.ui.ProfileActivity
import com.tapri.ui.GroupsActivity
import com.tapri.ui.EarnActivity
import com.tapri.ui.InfoActivity
import com.tapri.ui.TipsActivity
import com.tapri.ui.ComingSoonActivity
import com.tapri.ui.EarnComingSoonActivity
import com.tapri.ui.CommentsActivity
import com.tapri.ui.ShareToGroupDialog
import com.tapri.ui.LoginActivity
import com.tapri.network.PostFeedDto
import com.tapri.network.PostDto
import com.tapri.repository.PostsRepository
import com.tapri.utils.TimeUtils
import com.tapri.utils.FeatureFlags
import com.tapri.network.ApiClient
import com.tapri.network.PostsApi
import com.tapri.utils.SessionManager
import com.tapri.utils.AnimationUtils
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
// Add these imports for animations
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.animation.*
import android.view.View

class HomeActivity : AppCompatActivity() {
    
    // Add these properties for create post functionality
    
    private var pulseAnimator: ValueAnimator? = null
    
    // API and session management
    private lateinit var sessionManager: SessionManager
    private lateinit var postsApi: PostsApi
    private lateinit var postsRepository: PostsRepository
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingGifView: ImageView
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    private var postsAdapter: PostAdapter? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isLoading = false
    private val pendingApiCalls = mutableSetOf<String>() // Track pending API calls by post ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize session manager and API
        sessionManager = SessionManager(this)
        
        // Check if user is logged in
        android.util.Log.d("HomeActivity", "Checking login status...")
        val isLoggedIn = sessionManager.isLoggedIn()
        val authToken = sessionManager.getAuthToken()
        android.util.Log.d("HomeActivity", "Login status: $isLoggedIn")
        android.util.Log.d("HomeActivity", "Auth token: $authToken")
        
        if (!isLoggedIn || authToken.isNullOrEmpty()) {
            android.util.Log.d("HomeActivity", "User not logged in or no auth token, redirecting to login")
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        android.util.Log.d("HomeActivity", "Initializing API and repository...")
        try {
            postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
            postsRepository = PostsRepository(postsApi, sessionManager)
            android.util.Log.d("HomeActivity", "API and repository initialized successfully")
        } catch (e: Exception) {
            android.util.Log.e("HomeActivity", "Error initializing API: ${e.message}", e)
            Toast.makeText(this, "Failed to initialize API. Please restart the app.", Toast.LENGTH_SHORT).show()
            finish()
        }
        
        // Find views by ID
        android.util.Log.d("HomeActivity", "Finding views...")
        try {
            postsRecyclerView = findViewById<RecyclerView>(R.id.postsRecyclerView)
            swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
            loadingGifView = findViewById<ImageView>(R.id.loadingGifView)
            errorMessageView = findViewById<TextView>(R.id.errorMessageView)
            retryButton = findViewById<TextView>(R.id.retryButton)
            android.util.Log.d("HomeActivity", "Views found successfully")
        } catch (e: Exception) {
            android.util.Log.e("HomeActivity", "Error finding views: ${e.message}", e)
            Toast.makeText(this, "Error loading interface", Toast.LENGTH_SHORT).show()
            return
        }
        val notificationIcon = findViewById<ImageView>(R.id.notificationIcon)
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        val freeNowSwitch = findViewById<Switch>(R.id.freeNowSwitch)
        val earningsCard = findViewById<androidx.cardview.widget.CardView>(R.id.earningsCard)
        val todayEarningsText = findViewById<TextView>(R.id.todayEarningsText)
        
        // Hide earnings features for v0 release
        if (!FeatureFlags.EARNINGS_FEATURES) {
            freeNowSwitch?.visibility = View.GONE
            earningsCard.visibility = View.GONE
        }

        // Custom navigation views
        val homeNav = findViewById<LinearLayout>(R.id.homeNav)
        val tapriNav = findViewById<LinearLayout>(R.id.tapriNav)
        val earnNav = findViewById<LinearLayout>(R.id.earnNav)
        val infoNav = findViewById<LinearLayout>(R.id.infoNav)
        val tipsNav = findViewById<LinearLayout>(R.id.tipsNav)
        val earnButton = findViewById<ImageView>(R.id.earnButton)

        // Create post banner is now handled by PostAdapter

        // Set up RecyclerView for posts
        android.util.Log.d("HomeActivity", "Setting up RecyclerView...")
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        android.util.Log.d("HomeActivity", "RecyclerView layout manager set")
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadPosts(isRefresh = true)
        }
        android.util.Log.d("HomeActivity", "SwipeRefresh listener set")
        
        // Set up retry button
        retryButton.setOnClickListener {
            loadPosts()
        }
        android.util.Log.d("HomeActivity", "Retry button listener set")
        
        // Load posts from API
        android.util.Log.d("HomeActivity", "Starting to load posts...")
        loadPosts()
        android.util.Log.d("HomeActivity", "Posts loading initiated")

        // Initialize earnings data
        updateEarningsDisplay(todayEarningsText)

        // Set up click listeners
        notificationIcon.setOnClickListener {
            AnimationUtils.animateButtonPress(notificationIcon) {
                val intent = Intent(this@HomeActivity, NotificationsActivity::class.java)
                startActivity(intent)
            }
        }

        // Set profile picture
        profileIcon.setImageResource(R.drawable.ic_profile)
        
        profileIcon.setOnClickListener {
            AnimationUtils.subtlePress(profileIcon) {
                val intent = Intent(this@HomeActivity, ProfileActivity::class.java)
                startActivity(intent)
            }
        }

        // Set up earnings card sections
        setupEarningsCardSections()

        earningsCard.setOnClickListener {
            AnimationUtils.animateButtonPress(earningsCard) {
                val intent = Intent(this@HomeActivity, InfoActivity::class.java)
                startActivity(intent)
            }
        }

        // Create post banner is now handled by PostAdapter

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

        freeNowSwitch?.setOnCheckedChangeListener { _, isChecked ->
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
            if (FeatureFlags.SHOW_COMING_SOON_EARN) {
                val intent = Intent(this, EarnComingSoonActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, EarnActivity::class.java)
                startActivity(intent)
            }
        }

        earnNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            if (FeatureFlags.SHOW_COMING_SOON_EARN) {
                val intent = Intent(this, EarnComingSoonActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, EarnActivity::class.java)
                startActivity(intent)
            }
        }

        infoNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, true)
            updateNavigationSelection(tipsNav, false)
            if (FeatureFlags.SHOW_COMING_SOON_INFO) {
                val intent = Intent(this, ComingSoonActivity::class.java)
                intent.putExtra("screen_type", "info")
                startActivity(intent)
            } else {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
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

    // Create post banner functionality is now handled by PostAdapter

    override fun onDestroy() {
        super.onDestroy()
        pulseAnimator?.cancel()
        coroutineScope.cancel()
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
        // Get user-specific earnings from session
        val userEarnings = sessionManager.getUserTotalEarnings()
        val formattedEarnings = String.format("%.0f", userEarnings)
        
        // Update the main earnings text with user's actual earnings
        earningsText.text = "$formattedEarnings Earned Today"

        // In a real app, you would also update the platform-specific earnings
        // by finding the TextViews for each platform and updating them
        // For now, the layout has hardcoded values that match this data
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh posts when activity comes back into focus
        // This ensures new posts appear after creating a post
        loadPosts()
    }
    
    private fun loadPosts(isRefresh: Boolean = false) {
        if (isLoading) return
        
        isLoading = true
        
        // Show loading state
        if (!isRefresh) {
            showLoadingState()
        }
        
        coroutineScope.launch {
            try {
                // Check if API is available
                if (::postsApi.isInitialized) {
                    val response = postsApi.getPostsFeed()
                withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val postsList = response.body()
                    postsList?.let { posts ->
                        // Debug logging for API response
                        android.util.Log.d("HomeActivity", "Received ${posts.size} posts from API")
                        posts.forEachIndexed { index, post ->
                            android.util.Log.d("HomeActivity", "Post $index: id=${post.id}, mediaUrl='${post.mediaUrl}', mediaType=${post.mediaType}")
                        }
                        
                        val uiPosts = posts.map { postFeedDto ->
                            val uiPost = convertPostFeedToUiPost(postFeedDto)
                            android.util.Log.d("HomeActivity", "Converted post ${uiPost.id}: isLiked=${uiPost.isLiked}, likeCount=${uiPost.likeCount}")
                            uiPost
                        }
                        
                        // Create a special "create post banner" post and add it to the beginning
                        val createPostBanner = Post(
                            id = "create_post_banner",
                            userName = "",
                            userAvatar = null,
                            postTime = "",
                            caption = "CREATE_POST_BANNER",
                            mediaUrl = null,
                            mediaType = MediaType.IMAGE,
                            likeCount = 0,
                            commentCount = 0,
                            shareCount = 0,
                            isLiked = false,
                            isSaved = false
                        )
                        
                        val postsWithBanner = mutableListOf<Post>().apply {
                            add(createPostBanner)
                            addAll(uiPosts)
                        }
                        
                        postsAdapter = PostAdapter(
                            this@HomeActivity,
                            postsWithBanner,
                            onCommentClick = { post ->
                                if (post.id != "create_post_banner") {
                                    android.util.Log.d("HomeActivity", "Comment callback triggered for post ${post.id}")
                                    // Show comments bottom sheet with callback to update comment count
                                    val bottomSheet = CommentsBottomSheet.newInstance(
                                        convertUiPostToPostFeed(post)
                                    ) {
                                        // Update comment count when a comment is added
                                        updateCommentCount(post.id, true)
                                    }
                                    bottomSheet.show(supportFragmentManager, "CommentsBottomSheet")
                                }
                            },
                            onSaveClick = { post ->
                                if (post.id != "create_post_banner") {
                                    android.util.Log.d("HomeActivity", "Save callback triggered for post ${post.id}")
                                    handleSavePost(post)
                                }
                            },
                            onLikeClick = { post ->
                                if (post.id != "create_post_banner") {
                                    android.util.Log.d("HomeActivity", "Like callback triggered for post ${post.id}")
                                    handleLikePost(post)
                                }
                            },
                            onShareClick = { post ->
                                if (post.id != "create_post_banner") {
                                    android.util.Log.d("HomeActivity", "Share callback triggered for post ${post.id}")
                                    handleSharePost(post)
                                }
                            }
                        )
                        postsRecyclerView.adapter = postsAdapter
                        
                        // Set RecyclerView reference for direct view updates
                        postsAdapter?.setRecyclerView(postsRecyclerView)
                            
                            showContentState()
                            
                            if (isRefresh) {
                                Toast.makeText(this@HomeActivity, "Refreshed ${uiPosts.size} posts", Toast.LENGTH_SHORT).show()
                            }
                    } ?: run {
                            showErrorState("No posts available")
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            sessionManager.clearSession()
                            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        403 -> {
                                showErrorState("Access denied. Please check your permissions.")
                            }
                            500 -> {
                                showErrorState("Server error. Please try again later.")
                        }
                        else -> {
                                showErrorState("Failed to load posts: ${response.code()}")
                            }
                        }
                    }
                }
                } else {
                    // API not available
                    withContext(Dispatchers.Main) {
                        showErrorState("API not available. Please check your connection.")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorState("Network error: ${e.message}")
                e.printStackTrace()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    swipeRefreshLayout.setRefreshing(false)
                }
            }
        }
    }
    
    private fun showLoadingState() {
        // Load and start the GIF animation
        Glide.with(this)
            .load(R.drawable.loading)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(loadingGifView)
        
        AnimationUtils.fadeIn(loadingGifView, 300)
        AnimationUtils.fadeOut(postsRecyclerView, 200)
        AnimationUtils.fadeOut(findViewById<LinearLayout>(R.id.errorContainer), 200)
    }
    
    private fun showContentState() {
        AnimationUtils.fadeOut(loadingGifView, 200)
        AnimationUtils.slideInFromBottom(postsRecyclerView, 400) {
            postsRecyclerView.visibility = View.VISIBLE
        }
        AnimationUtils.fadeOut(findViewById<LinearLayout>(R.id.errorContainer), 200)
    }
    
    private fun showErrorState(message: String) {
        AnimationUtils.fadeOut(loadingGifView, 200)
        AnimationUtils.fadeOut(postsRecyclerView, 200)
        errorMessageView.text = message
        AnimationUtils.slideInFromBottom(findViewById<LinearLayout>(R.id.errorContainer), 400) {
            findViewById<LinearLayout>(R.id.errorContainer).visibility = View.VISIBLE
            retryButton.visibility = View.VISIBLE
        }
        // Add shake animation to indicate error
        AnimationUtils.shake(findViewById<LinearLayout>(R.id.errorContainer))
    }
    
    private fun loadDummyPosts() {
        val posts = listOf(
            Post("1", "Ramesh", null, "2 hrs ago", "Heavy traffic on MG Road : Consider alternate routes to avoid delays...", com.tapri.utils.Config.getAbsoluteMediaUrl("/api/images/posts/00b4db9a-1666-42c9-913e-8d22587fdcae.jpg"), MediaType.IMAGE, 900, 5, 1),
            Post("2", "Suresh", null, "4 hrs ago", "Check out this cool traffic animation! 🚗", com.tapri.utils.Config.getAbsoluteMediaUrl("/api/images/posts/6e67a119-506e-457a-92fd-c6fe2a8cec6f.jpg"), MediaType.IMAGE, 450, 3, 2),
            Post("3", "Mahesh", null, "6 hrs ago", "Just wanted to share that the new route is much faster now! No traffic at all.", null, MediaType.IMAGE, 320, 2, 0),
            Post("4", "Rajesh", null, "8 hrs ago", "Best time to drive is during peak hours!", com.tapri.utils.Config.getAbsoluteMediaUrl("/api/images/posts/fa8da67f-791e-4c12-b254-f70167c8237c.jpg"), MediaType.IMAGE, 780, 8, 3),
            Post("5", "Amit", null, "10 hrs ago", "Avoid this route during rush hour - animated guide", "https://picsum.photos/400/300?random=5", MediaType.IMAGE, 210, 1, 0),
            Post("6", "Priya", null, "12 hrs ago", "Thanks everyone for the helpful tips! Really appreciate this community.", null, MediaType.IMAGE, 150, 4, 1)
        )
        
        // This fallback PostAdapter is only used if API loading fails
        // Don't override the real adapter that loads from API
        if (postsAdapter == null) {
            postsAdapter = PostAdapter(
                this,
                posts.toMutableList(),
                onCommentClick = { post ->
                    Toast.makeText(this, "Comment clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                },
                onSaveClick = { post ->
                    post.isSaved = !post.isSaved
                    Toast.makeText(this, if (post.isSaved) "Saved" else "Unsaved", Toast.LENGTH_SHORT).show()
                },
                onLikeClick = { post ->
                    Toast.makeText(this, "Like clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                },
                onShareClick = { post ->
                    Toast.makeText(this, "Share clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                }
            )
            postsRecyclerView.adapter = postsAdapter
        }
    }
    
    private fun convertToUiPost(postDto: com.tapri.network.PostDto): Post {
        val mediaType = when (postDto.mediaType?.uppercase()) {
            "IMAGE" -> MediaType.IMAGE
            "GIF" -> MediaType.GIF
            "VIDEO" -> MediaType.VIDEO
            else -> MediaType.IMAGE
        }
        
        val timeAgo = formatTimeAgo(postDto.createdAt)
        
        return Post(
            id = postDto.id.toString(),
            userName = postDto.user.name,
            userAvatar = postDto.user.profilePictureUrl,
            postTime = timeAgo,
            caption = postDto.text,
            mediaUrl = postDto.mediaUrl,
            mediaType = mediaType,
            likeCount = postDto.likeCount,
            commentCount = postDto.commentCount,
            shareCount = postDto.shareCount,
            isLiked = postDto.isLiked,
            isSaved = postDto.isSaved
        )
    }
    
    private fun convertPostFeedToUiPost(postFeedDto: com.tapri.network.PostFeedDto): Post {
        val mediaType = when (postFeedDto.mediaType?.uppercase()) {
            "IMAGE" -> MediaType.IMAGE
            "GIF" -> MediaType.GIF
            "VIDEO" -> MediaType.VIDEO
            else -> MediaType.IMAGE
        }
        
        return Post(
            id = postFeedDto.id.toString(),
            userName = postFeedDto.userName,
            userAvatar = postFeedDto.userAvatar,
            postTime = postFeedDto.postTime,
            caption = postFeedDto.caption,
            mediaUrl = postFeedDto.mediaUrl,
            mediaType = mediaType,
            likeCount = postFeedDto.likeCount,
            commentCount = postFeedDto.commentCount,
            shareCount = postFeedDto.shareCount,
            isLiked = postFeedDto.isLiked,
            isSaved = postFeedDto.isSaved
        )
    }
    
    private fun formatTimeAgo(createdAt: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(createdAt)
            val now = Date()
            val diff = now.time - (date?.time ?: 0)
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            
            when {
                days > 0 -> "${days}d ago"
                hours > 0 -> "${hours}h ago"
                minutes > 0 -> "${minutes}m ago"
                else -> "Just now"
            }
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    
    
    
    // Post interaction handlers
    private fun handleLikePost(post: Post) {
        // Prevent multiple simultaneous API calls for the same post
        if (pendingApiCalls.contains("like_${post.id}")) {
            android.util.Log.d("HomeActivity", "Like API call already in progress for post ${post.id}")
            return
        }
        
        pendingApiCalls.add("like_${post.id}")
        android.util.Log.d("HomeActivity", "handleLikePost called for post ${post.id}, current like state: ${post.isLiked}")
        
        // OPTIMISTIC UI UPDATE - Update UI immediately for better UX
        val optimisticPost = post.copy(
            isLiked = !post.isLiked,
            likeCount = if (post.isLiked) post.likeCount - 1 else post.likeCount + 1
        )
        
        // Update the post in the adapter immediately
        val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
        if (postIndex != null && postIndex >= 0) {
            postsAdapter?.updatePost(postIndex, optimisticPost)
            android.util.Log.d("HomeActivity", "Optimistic update applied: likeCount=${optimisticPost.likeCount}, isLiked=${optimisticPost.isLiked}")
        }
        
        coroutineScope.launch {
            try {
                val response = postsApi.likePost(post.id.toLong())
                android.util.Log.d("HomeActivity", "Like API response: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val updatedPost = response.body()
                        android.util.Log.d("HomeActivity", "Updated post received: $updatedPost")
                        updatedPost?.let {
                            android.util.Log.d("HomeActivity", "Raw backend response: id=${it.id}, likeCount=${it.likeCount}, isLiked=${it.isLiked}, commentCount=${it.commentCount}")
                            
                            // Convert backend response to UI model
                            val uiPost = convertPostDtoToUiPost(it)
                            android.util.Log.d("HomeActivity", "Converted UI post: id=${uiPost.id}, isLiked=${uiPost.isLiked}, likeCount=${uiPost.likeCount}")
                            
                            // Update the post in the adapter
                            val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                            if (postIndex != null && postIndex >= 0) {
                                postsAdapter?.updatePost(postIndex, uiPost)
                                android.util.Log.d("HomeActivity", "Post updated at index $postIndex with likeCount=${uiPost.likeCount}, isLiked=${uiPost.isLiked}")
                            } else {
                                android.util.Log.e("HomeActivity", "Post index not found for post ${post.id}")
                            }
                        }
                    } else {
                        android.util.Log.e("HomeActivity", "Like API failed with code: ${response.code()}")
                        
                        // REVERT OPTIMISTIC UPDATE on failure
                        val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                        if (postIndex != null && postIndex >= 0) {
                            postsAdapter?.updatePost(postIndex, post) // Revert to original state
                            android.util.Log.d("HomeActivity", "Reverted optimistic update for post ${post.id}")
                        }
                        
                        when (response.code()) {
                            401, 403 -> {
                                Toast.makeText(this@HomeActivity, "Authentication required. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@HomeActivity, "Failed to update like", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("HomeActivity", "Like API exception: ${e.message}")
                withContext(Dispatchers.Main) {
                    // REVERT OPTIMISTIC UPDATE on exception
                    val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                    if (postIndex != null && postIndex >= 0) {
                        postsAdapter?.updatePost(postIndex, post) // Revert to original state
                        android.util.Log.d("HomeActivity", "Reverted optimistic update for post ${post.id} due to exception")
                    }
                    Toast.makeText(this@HomeActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                // Always remove from pending calls
                pendingApiCalls.remove("like_${post.id}")
            }
        }
    }
    
    // Method to update comment count when a comment is added
    fun updateCommentCount(postId: String, increment: Boolean = true) {
        val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == postId }
        if (postIndex != null && postIndex >= 0) {
            val currentPost = postsAdapter?.getPosts()?.get(postIndex)
            currentPost?.let { post ->
                val updatedPost = post.copy(
                    commentCount = if (increment) post.commentCount + 1 else post.commentCount - 1
                )
                postsAdapter?.updatePost(postIndex, updatedPost)
                android.util.Log.d("HomeActivity", "Comment count updated for post $postId: ${updatedPost.commentCount}")
            }
        }
    }
    
    private fun handleSavePost(post: Post) {
        android.util.Log.d("HomeActivity", "handleSavePost called for post ${post.id}, current save state: ${post.isSaved}")
        
        // OPTIMISTIC UI UPDATE - Update UI immediately for better UX
        val optimisticPost = post.copy(isSaved = !post.isSaved)
        
        // Update the post in the adapter immediately
        val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
        if (postIndex != null && postIndex >= 0) {
            postsAdapter?.updatePost(postIndex, optimisticPost)
            android.util.Log.d("HomeActivity", "Optimistic save update applied: isSaved=${optimisticPost.isSaved}")
        }
        
        coroutineScope.launch {
            try {
                val response = postsApi.savePost(post.id.toLong())
                android.util.Log.d("HomeActivity", "Save API response: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val isSaved = it["isSaved"] as? Boolean ?: false
                            post.isSaved = isSaved
                            
                            // Update only the specific post instead of refreshing entire list
                            val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                            if (postIndex != null && postIndex >= 0) {
                                postsAdapter?.updatePost(postIndex, post)
                                android.util.Log.d("HomeActivity", "Save post updated at index $postIndex with isSaved=$isSaved")
                            }
                            
                            Toast.makeText(this@HomeActivity, 
                                if (isSaved) "Post saved" else "Post unsaved", 
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.util.Log.e("HomeActivity", "Save API failed with code: ${response.code()}")
                        
                        // REVERT OPTIMISTIC UPDATE on failure
                        val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                        if (postIndex != null && postIndex >= 0) {
                            postsAdapter?.updatePost(postIndex, post) // Revert to original state
                            android.util.Log.d("HomeActivity", "Reverted optimistic save update for post ${post.id}")
                        }
                        
                        when (response.code()) {
                            401, 403 -> {
                                Toast.makeText(this@HomeActivity, "Authentication required. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@HomeActivity, "Failed to save post", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // REVERT OPTIMISTIC UPDATE on exception
                    val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                    if (postIndex != null && postIndex >= 0) {
                        postsAdapter?.updatePost(postIndex, post) // Revert to original state
                        android.util.Log.d("HomeActivity", "Reverted optimistic save update for post ${post.id} due to exception")
                    }
                    Toast.makeText(this@HomeActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun handleSharePost(post: Post) {
        android.util.Log.d("HomeActivity", "handleSharePost called for post ${post.id}")
        
        // Create options for sharing
        val shareOptions = arrayOf("Share to Group", "Share via Apps")
        
        AlertDialog.Builder(this)
            .setTitle("Share Post")
            .setItems(shareOptions) { _, which ->
                when (which) {
                    0 -> {
                        // Share to Group
                        val dialog = ShareToGroupDialog(this, post.id.toLong()) {
                            // Update share count after successful share to group
                            updateShareCount(post)
                        }
                        dialog.show()
                    }
                    1 -> {
                        // Share via system apps
                        shareViaSystemApps(post)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun shareViaSystemApps(post: Post) {
        // Create share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this post on Tapri!\n\n${post.caption}\n\nDownload Tapri app to see more!")
            putExtra(Intent.EXTRA_SUBJECT, "Tapri Post")
        }
        
        try {
            startActivity(Intent.createChooser(shareIntent, "Share post"))
            
            // Update share count after successful share
            updateShareCount(post)
        } catch (e: Exception) {
            Toast.makeText(this, "No apps available to share", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun updateShareCount(post: Post) {
        coroutineScope.launch {
            try {
                val response = postsApi.sharePost(post.id.toLong())
                android.util.Log.d("HomeActivity", "Share API response: ${response.code()}, isSuccessful: ${response.isSuccessful}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val shareCount = it["shareCount"] as? Int ?: post.shareCount
                            post.shareCount = shareCount
                            
                            // Update only the specific post instead of refreshing entire list
                            val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                            if (postIndex != null && postIndex >= 0) {
                                postsAdapter?.updatePost(postIndex, post)
                                android.util.Log.d("HomeActivity", "Share post updated at index $postIndex with shareCount=$shareCount")
                            }
                        }
                    }
                    // Don't show error for share failures as it's not critical
                }
            } catch (e: Exception) {
                // Silently handle share count errors
            }
        }
    }
    
    private fun convertPostDtoToUiPost(postDto: PostDto): Post {
        return Post(
            id = postDto.id.toString(),
            userName = postDto.user.name,
            userAvatar = postDto.user.profilePictureUrl,
            postTime = TimeUtils.getRelativeTime(postDto.createdAt),
            caption = postDto.text,
            mediaUrl = postDto.mediaUrl,
            mediaType = when (postDto.mediaType?.toString()) {
                "IMAGE" -> MediaType.IMAGE
                "VIDEO" -> MediaType.VIDEO
                "AUDIO" -> MediaType.AUDIO
                else -> MediaType.IMAGE
            },
            likeCount = postDto.likeCount,
            commentCount = postDto.commentCount,
            shareCount = postDto.shareCount,
            isLiked = postDto.isLiked,
            isSaved = postDto.isSaved
        )
    }
    
    private fun convertUiPostToPostFeed(post: Post): PostFeedDto {
        return PostFeedDto(
            id = post.id.toLong(),
            userName = post.userName,
            userAvatar = post.userAvatar,
            postTime = post.postTime,
            caption = post.caption,
            mediaUrl = post.mediaUrl,
            mediaType = post.mediaType.name,
            postType = "GENERAL",
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            shareCount = post.shareCount,
            isLiked = post.isLiked,
            isSaved = post.isSaved
        )
    }
}