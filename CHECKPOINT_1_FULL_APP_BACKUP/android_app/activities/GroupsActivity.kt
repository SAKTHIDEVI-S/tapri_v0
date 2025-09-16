package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.GroupsApi
import com.tapri.network.GroupDto
import com.tapri.ui.adapters.MyGroupsAdapter
import com.tapri.ui.adapters.DiscoverGroupsAdapter
import com.tapri.utils.SessionManager
import com.tapri.utils.FeatureFlags
import com.tapri.utils.AnimationUtils
import kotlinx.coroutines.*

class GroupsActivity : AppCompatActivity() {
    
    companion object {
        private const val CREATE_GROUP_REQUEST_CODE = 1001
    }
    
    private lateinit var sessionManager: SessionManager
    private lateinit var groupsApi: GroupsApi
    private lateinit var myGroupsRecyclerView: RecyclerView
    private lateinit var discoverGroupsRecyclerView: RecyclerView
    private lateinit var myGroupsAdapter: MyGroupsAdapter
    private lateinit var discoverGroupsAdapter: DiscoverGroupsAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingGifView: ImageView
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    private lateinit var emptyStateView: LinearLayout
    private lateinit var searchButton: ImageView
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isLoading = false
    private var retryCount = 0
    private val maxRetries = 3
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        // Initialize
        sessionManager = SessionManager(this)
        groupsApi = ApiClient.groupsRetrofit(sessionManager).create(GroupsApi::class.java)
        
        // Find views
        val backButton = findViewById<TextView>(R.id.backButton)
        searchButton = findViewById(R.id.searchButton)
        
        myGroupsRecyclerView = findViewById(R.id.myGroupsRecyclerView)
        discoverGroupsRecyclerView = findViewById(R.id.discoverGroupsRecyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        loadingGifView = findViewById(R.id.loadingGifView)
        errorMessageView = findViewById(R.id.errorMessageView)
        retryButton = findViewById(R.id.retryButton)
        emptyStateView = findViewById(R.id.emptyStateView)
        
        // Bottom navigation
        val homeNav = findViewById<LinearLayout>(R.id.homeNav)
        val tapriNav = findViewById<LinearLayout>(R.id.tapriNav)
        val earnNav = findViewById<LinearLayout>(R.id.earnNav)
        val infoNav = findViewById<LinearLayout>(R.id.infoNav)
        val tipsNav = findViewById<LinearLayout>(R.id.tipsNav)
        val earnButton = findViewById<ImageView>(R.id.earnButton)

        // Set up RecyclerViews
        myGroupsRecyclerView.layoutManager = LinearLayoutManager(this)
        myGroupsAdapter = MyGroupsAdapter(emptyList()) { group ->
            // Handle my group click - open group chat
            openGroupChat(group)
        }
        myGroupsRecyclerView.adapter = myGroupsAdapter
        
        discoverGroupsRecyclerView.layoutManager = LinearLayoutManager(this)
        discoverGroupsAdapter = DiscoverGroupsAdapter(emptyList()) { group ->
            // Handle join button click
            joinGroup(group)
        }
        discoverGroupsRecyclerView.adapter = discoverGroupsAdapter
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadGroups(isRefresh = true)
        }
        
        // Set up retry button
        retryButton.setOnClickListener {
            loadGroups()
        }

        // Set up button click listeners
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }

        searchButton.setOnClickListener {
            // TODO: Implement search functionality
            Toast.makeText(this, "Search functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        val createGroupButton = findViewById<LinearLayout>(R.id.createGroupButton)
        createGroupButton.setOnClickListener {
            AnimationUtils.animateButtonPress(createGroupButton) {
                val intent = Intent(this, CreateGroupActivity::class.java)
                startActivityForResult(intent, CREATE_GROUP_REQUEST_CODE)
            }
        }

        // Set up "See All" buttons
        val seeAllMyGroupsButton = findViewById<TextView>(R.id.seeAllMyGroupsButton)
        seeAllMyGroupsButton.setOnClickListener {
            loadMyGroups()
        }
        
        val seeAllDiscoverButton = findViewById<TextView>(R.id.seeAllDiscoverButton)
        seeAllDiscoverButton.setOnClickListener {
            loadExploreGroups()
        }

        // Set up bottom navigation
        setupBottomNavigation(homeNav, tapriNav, earnNav, infoNav, tipsNav, earnButton)
        
        // Load groups
        loadGroups()
    }
    
    private fun loadGroups(isRefresh: Boolean = false) {
        android.util.Log.d("GroupsActivity", "loadGroups() called with isRefresh = $isRefresh")
        if (isLoading) {
            android.util.Log.d("GroupsActivity", "Already loading, returning")
            return
        }
        
        val currentUser = sessionManager.getUserSession()
        val authToken = sessionManager.getAuthToken()
        
        android.util.Log.d("GroupsActivity", "Current user: $currentUser")
        android.util.Log.d("GroupsActivity", "Auth token: $authToken")
        
        if (currentUser == null) {
            android.util.Log.e("GroupsActivity", "User not logged in, redirecting to login")
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }
        
        // Check authentication status
        android.util.Log.d("GroupsActivity", "Token status: ${sessionManager.getTokenStatus()}")
        
        if (sessionManager.needsReauthentication()) {
            android.util.Log.w("GroupsActivity", "Token expired or missing, attempting refresh...")
            
            // Try to refresh token first
            com.tapri.utils.TokenRefreshHelper.refreshTokenAsync(
                sessionManager = sessionManager,
                coroutineScope = coroutineScope,
                onSuccess = {
                    android.util.Log.d("GroupsActivity", "Token refreshed successfully, loading groups")
                    loadGroupsData()
                },
                onFailure = {
                    android.util.Log.e("GroupsActivity", "Token refresh failed, redirecting to login")
                    Toast.makeText(this@GroupsActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                    sessionManager.clearSession()
                    val intent = Intent(this@GroupsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            )
            return
        }
        
        loadGroupsData()
    }
    
    private fun loadGroupsData() {
        android.util.Log.d("GroupsActivity", "loadGroupsData() called")
        if (isLoading) {
            android.util.Log.d("GroupsActivity", "Already loading, returning")
            return
        }
        
        android.util.Log.d("GroupsActivity", "Setting isLoading = true and showing loading state")
        isLoading = true
        showLoadingState()
        
        coroutineScope.launch {
            try {
                android.util.Log.d("GroupsActivity", "Making groups API call (attempt ${retryCount + 1})")
                android.util.Log.d("GroupsActivity", "Auth token: ${sessionManager.getAuthToken()}")
                android.util.Log.d("GroupsActivity", "Is logged in: ${sessionManager.isLoggedIn()}")
                android.util.Log.d("GroupsActivity", "Token status: ${sessionManager.getTokenStatus()}")
                android.util.Log.d("GroupsActivity", "User ID: ${sessionManager.getUserId()}")
                android.util.Log.d("GroupsActivity", "About to call groupsApi.getGroups()")
                android.util.Log.d("GroupsActivity", "Groups API base URL: ${com.tapri.utils.Config.getBaseUrl()}groups/")
                android.util.Log.d("GroupsActivity", "Full getGroups URL will be: ${com.tapri.utils.Config.getBaseUrl()}groups/my")
                
                val response = groupsApi.getGroups()
                android.util.Log.d("GroupsActivity", "getGroups() call completed")
                android.util.Log.d("GroupsActivity", "Groups API response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("GroupsActivity", "Groups API error: $errorBody")
                    android.util.Log.e("GroupsActivity", "Response headers: ${response.headers()}")
                    
                    // If 401/403, try to refresh token
                    if (response.code() == 401 || response.code() == 403) {
                        if (retryCount < maxRetries) {
                            android.util.Log.w("GroupsActivity", "Authentication error, attempting token refresh... (retry ${retryCount + 1}/$maxRetries)")
                            retryCount++
                            if (sessionManager.canRefreshToken()) {
                                attemptTokenRefreshAndRetry()
                                return@launch
                            } else {
                                android.util.Log.e("GroupsActivity", "No refresh token available, redirecting to login")
                                withContext(Dispatchers.Main) {
                                    sessionManager.clearSession()
                                    Toast.makeText(this@GroupsActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@GroupsActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                return@launch
                            }
                        } else {
                            android.util.Log.e("GroupsActivity", "Max retries reached, showing error state")
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                swipeRefreshLayout.isRefreshing = false
                                showErrorState("Authentication failed. Please try logging in again.")
                            }
                            return@launch
                        }
                    }
                }
                
                withContext(Dispatchers.Main) {
                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                    
                    if (response.isSuccessful) {
                        val myGroups = response.body() ?: emptyList()
                    android.util.Log.d("GroupsActivity", "Loaded ${myGroups.size} my groups")
                    android.util.Log.d("GroupsActivity", "My groups data: $myGroups")
                    
                    if (myGroups.isEmpty()) {
                        android.util.Log.w("GroupsActivity", "No groups returned from API - this might indicate a backend issue")
                    }
                        
                        // Load discover groups
                        coroutineScope.launch {
                            try {
                                val discoverResponse = groupsApi.exploreGroups()
                                withContext(Dispatchers.Main) {
                                    if (discoverResponse.isSuccessful) {
                                        val discoverGroups = discoverResponse.body() ?: emptyList()
                                        android.util.Log.d("GroupsActivity", "Loaded ${discoverGroups.size} discover groups")
                                        android.util.Log.d("GroupsActivity", "Discover groups data: $discoverGroups")
                        updateGroupsList(myGroups, discoverGroups)
                                        
                                        if (myGroups.isEmpty() && discoverGroups.isEmpty()) {
                                            Toast.makeText(this@GroupsActivity, "No groups found. Create a group to get started!", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        updateGroupsList(myGroups, emptyList())
                                    }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("GroupsActivity", "Exception loading discover groups: ${e.message}", e)
                                withContext(Dispatchers.Main) {
                                    updateGroupsList(myGroups, emptyList())
                                }
                            }
                        }
                    } else {
                        when (response.code()) {
                            401 -> {
                                // Try to refresh token and retry
                                android.util.Log.w("GroupsActivity", "Received 401, attempting token refresh...")
                                if (sessionManager.canRefreshToken()) {
                                    attemptTokenRefreshAndRetry()
            } else {
                                    android.util.Log.e("GroupsActivity", "No refresh token available, redirecting to login")
                                    sessionManager.clearSession()
                                    Toast.makeText(this@GroupsActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@GroupsActivity, LoginActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            403 -> {
                                android.util.Log.e("GroupsActivity", "Access forbidden (403)")
                                showErrorState("Access denied. Please check your permissions.")
                            }
                            else -> {
                                android.util.Log.e("GroupsActivity", "Groups API failed with code: ${response.code()}")
                                val errorBody = response.errorBody()?.string()
                                android.util.Log.e("GroupsActivity", "Error body: $errorBody")
                                showErrorState("Failed to load groups: ${response.code()}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupsActivity", "Groups API exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                    when {
                        e.message?.contains("401") == true || e.message?.contains("Unauthorized") == true -> {
                            attemptTokenRefreshAndRetry()
                        }
                        else -> {
                            showErrorState("Network error: ${e.message}")
                        }
                    }
                }
            }
        }
    }
    
    
    private fun loadMyGroups() {
        android.util.Log.d("GroupsActivity", "Loading my groups...")
        
        // Load my groups from backend
        coroutineScope.launch {
            try {
                val response = groupsApi.getGroups()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val myGroups = response.body() ?: emptyList()
                        android.util.Log.d("GroupsActivity", "Loaded ${myGroups.size} my groups")
                        
                        if (myGroups.isNotEmpty()) {
                            // Show only my groups in both sections to give more space
                            updateGroupsList(myGroups, emptyList())
                            Toast.makeText(this@GroupsActivity, "Showing all your groups (${myGroups.size} groups)", Toast.LENGTH_SHORT).show()
                        } else {
                            // If no groups, show discover groups instead
                            loadExploreGroups()
                            Toast.makeText(this@GroupsActivity, "You don't have any groups yet. Here are some to explore!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.util.Log.e("GroupsActivity", "Failed to load my groups: ${response.code()}")
                        Toast.makeText(this@GroupsActivity, "Failed to load your groups", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupsActivity", "Exception loading my groups: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GroupsActivity, "Network error loading your groups", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun loadExploreGroups() {
        android.util.Log.d("GroupsActivity", "Loading explore groups...")
        
        // Load explore groups from backend
        coroutineScope.launch {
            try {
                android.util.Log.d("GroupsActivity", "Making explore groups API call...")
                android.util.Log.d("GroupsActivity", "Auth token: ${sessionManager.getAuthToken()}")
                android.util.Log.d("GroupsActivity", "Is logged in: ${sessionManager.isLoggedIn()}")
                
                val response = groupsApi.exploreGroups()
                android.util.Log.d("GroupsActivity", "Explore groups response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val exploreGroups = response.body() ?: emptyList()
                        android.util.Log.d("GroupsActivity", "Loaded ${exploreGroups.size} explore groups")
                        android.util.Log.d("GroupsActivity", "Explore groups data: $exploreGroups")
                        
                        // Keep current my groups and update with new explore groups
                        val currentMyGroups = myGroupsAdapter.getGroups()
                        updateGroupsList(currentMyGroups, exploreGroups)
                        
                        if (exploreGroups.isEmpty()) {
                            Toast.makeText(this@GroupsActivity, "No more groups to explore. You're already a member of all available groups!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this@GroupsActivity, "Showing ${exploreGroups.size} groups to explore!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        android.util.Log.e("GroupsActivity", "Failed to load explore groups: ${response.code()}")
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("GroupsActivity", "Error body: $errorBody")
                        Toast.makeText(this@GroupsActivity, "Failed to load more groups", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupsActivity", "Exception loading explore groups: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GroupsActivity, "Network error loading groups", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    
    private fun attemptTokenRefreshAndRetry() {
        android.util.Log.d("GroupsActivity", "Attempting token refresh and retry...")
        com.tapri.utils.TokenRefreshHelper.refreshTokenAsync(
            sessionManager = sessionManager,
            coroutineScope = coroutineScope,
            onSuccess = {
                android.util.Log.d("GroupsActivity", "Token refreshed successfully, retrying groups load")
                retryCount = 0 // Reset retry count on successful refresh
                loadGroupsData()
            },
            onFailure = {
                android.util.Log.e("GroupsActivity", "Token refresh failed during retry, redirecting to login")
                sessionManager.clearSession()
                Toast.makeText(this@GroupsActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@GroupsActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        )
    }
    
    private fun updateGroupsList(myGroups: List<GroupDto>, discoverGroups: List<GroupDto>) {
        myGroupsAdapter.updateGroups(myGroups)
        discoverGroupsAdapter.updateGroups(discoverGroups)
        
        if (myGroups.isEmpty() && discoverGroups.isEmpty()) {
            showEmptyState("No groups found")
        } else {
            showContentState()
        }
    }
    
    private fun openGroupChat(group: GroupDto) {
        val intent = Intent(this, GroupChatActivity::class.java)
        intent.putExtra("groupId", group.id ?: 0L)
        intent.putExtra("groupName", group.getDisplayName())
        startActivity(intent)
    }
    
    private fun joinGroup(group: GroupDto) {
        coroutineScope.launch {
            try {
                android.util.Log.d("GroupsActivity", "Attempting to join group: ${group.id} - ${group.name}")
                android.util.Log.d("GroupsActivity", "Auth token: ${sessionManager.getAuthToken()}")
                android.util.Log.d("GroupsActivity", "Is logged in: ${sessionManager.isLoggedIn()}")
                android.util.Log.d("GroupsActivity", "Base URL: ${com.tapri.utils.Config.getBaseUrl()}")
                android.util.Log.d("GroupsActivity", "Groups API base URL: ${com.tapri.utils.Config.getBaseUrl()}groups/")
                android.util.Log.d("GroupsActivity", "Join group URL will be: ${com.tapri.utils.Config.getBaseUrl()}groups/${group.id}/join")
                val response = groupsApi.joinGroup(group.id ?: 0L)
                android.util.Log.d("GroupsActivity", "Join group response: code=${response.code()}, isSuccessful=${response.isSuccessful()}")
                android.util.Log.d("GroupsActivity", "Response body: ${response.body()}")
                android.util.Log.d("GroupsActivity", "Response headers: ${response.headers()}")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@GroupsActivity, "Successfully joined ${group.name}", Toast.LENGTH_SHORT).show()
                        // Refresh the groups list
                        loadGroups(isRefresh = true)
                    } else {
                        when (response.code()) {
                            401 -> {
                                Toast.makeText(this@GroupsActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                startActivity(Intent(this@GroupsActivity, LoginActivity::class.java))
                                finish()
                            }
                            else -> {
                                Toast.makeText(this@GroupsActivity, "Failed to join group. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupsActivity", "Exception while joining group: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@GroupsActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    
    private fun showLoadingState() {
        Glide.with(this)
            .load(R.drawable.loading)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(loadingGifView)
            
        loadingGifView.visibility = View.VISIBLE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
        myGroupsRecyclerView.visibility = View.GONE
        discoverGroupsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
    }
    
    private fun showContentState() {
        loadingGifView.visibility = View.GONE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
        myGroupsRecyclerView.visibility = View.VISIBLE
        discoverGroupsRecyclerView.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE
    }
    
    private fun showErrorState(message: String) {
        loadingGifView.visibility = View.GONE
        errorMessageView.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        myGroupsRecyclerView.visibility = View.GONE
        discoverGroupsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        errorMessageView.text = message
    }
    
    private fun showEmptyState(message: String) {
        loadingGifView.visibility = View.GONE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
        myGroupsRecyclerView.visibility = View.GONE
        discoverGroupsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.VISIBLE
        
        val emptyMessageView = findViewById<TextView>(R.id.emptyMessageView)
        emptyMessageView?.text = message
    }
    
    private fun showCreateGroupDialog() {
        Toast.makeText(this, "Create Group - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun setupBottomNavigation(
        homeNav: LinearLayout,
        tapriNav: LinearLayout,
        earnNav: LinearLayout,
        infoNav: LinearLayout,
        tipsNav: LinearLayout,
        earnButton: ImageView
    ) {
        // Set Tapri as selected by default
        updateNavigationSelection(tapriNav, true)

        homeNav.setOnClickListener {
            updateNavigationSelection(homeNav, true)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        tapriNav.setOnClickListener {
            // Already on Tapri screen
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, true)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
        }

        earnButton.setOnClickListener {
            if (FeatureFlags.SHOW_COMING_SOON_EARN) {
                val intent = Intent(this, EarnComingSoonActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, EarnActivity::class.java)
                startActivity(intent)
            }
            finish()
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
            finish()
        }

        infoNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, true)
            updateNavigationSelection(tipsNav, false)
            if (FeatureFlags.SHOW_COMING_SOON_INFO) {
                val intent = Intent(this, ComingSoonActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
            finish()
        }

        tipsNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, true)
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateNavigationSelection(navItem: LinearLayout, isSelected: Boolean) {
        val icon = navItem.getChildAt(0) as ImageView
        val text = navItem.getChildAt(1) as TextView

        if (isSelected) {
            icon.alpha = 1.0f
            text.setTextColor(resources.getColor(R.color.primary_red, null))
        } else {
            icon.alpha = 0.6f
            text.setTextColor(resources.getColor(R.color.text_secondary, null))
        }
    }
    
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == CREATE_GROUP_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Group was created successfully, refresh the groups list
                android.util.Log.d("GroupsActivity", "Group created successfully, refreshing groups list")
                loadGroups(isRefresh = true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
