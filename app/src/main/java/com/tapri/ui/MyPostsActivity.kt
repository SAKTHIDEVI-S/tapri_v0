package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ProgressBar
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
import com.tapri.network.ApiClient
import com.tapri.network.PostsApi
import com.tapri.utils.SessionManager
import com.tapri.utils.AnimationUtils
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale

class MyPostsActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageView
    private lateinit var titleText: TextView
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var emptyStateView: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var loadingGifView: ImageView
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    
    // API and session management
    private lateinit var sessionManager: SessionManager
    private lateinit var postsApi: PostsApi
    private var postsAdapter: PostAdapter? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var isLoading = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)
        
        // Initialize session manager and API
        sessionManager = SessionManager(this)
        postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
        
        initializeViews()
        setupClickListeners()
        loadMyPosts()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        titleText = findViewById(R.id.titleText)
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        emptyStateView = findViewById(R.id.emptyStateView)
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        loadingGifView = findViewById(R.id.loadingGifView)
        errorMessageView = findViewById(R.id.errorMessageView)
        retryButton = findViewById(R.id.retryButton)
        
        titleText.text = "My Posts"
        
        // Set up pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener {
            loadMyPosts(isRefresh = true)
        }
        
        // Set up retry button
        retryButton.setOnClickListener {
            loadMyPosts()
        }
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
    }
    
    private fun loadMyPosts(isRefresh: Boolean = false) {
        if (isLoading) return
        
        isLoading = true
        
        // Show loading state
        if (!isRefresh) {
            showLoadingState()
        }
        
        coroutineScope.launch {
            try {
                // Get current user ID from session
                val currentUserId = sessionManager.getUserId()
                if (currentUserId == 0L) {
                    withContext(Dispatchers.Main) {
                        showEmptyState()
                        Toast.makeText(this@MyPostsActivity, "Please login first", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                
                // Use the dedicated getUserPosts API endpoint
                val response = postsApi.getUserPosts(currentUserId)
                if (response.isSuccessful) {
                    val postsList = response.body()
                    postsList?.let { posts ->
                        // Convert PostDto to UI Post and sort by most recent
                        val myPosts = posts.sortedByDescending { postDto ->
                            // Sort by creation time (most recent first)
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val date = sdf.parse(postDto.createdAt)
                                date?.time ?: 0L
                            } catch (e: Exception) {
                                0L
                            }
                        }.map { postDto ->
                            convertToUiPost(postDto)
                        }
                        
                        withContext(Dispatchers.Main) {
                            if (myPosts.isNotEmpty()) {
                                postsAdapter = PostAdapter(
                                    this@MyPostsActivity,
                                    myPosts.toMutableList(),
                                    onCommentClick = { post ->
                                        Toast.makeText(this@MyPostsActivity, "Comment clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                                    },
                                    onSaveClick = { post ->
                                        post.isSaved = !post.isSaved
                                        Toast.makeText(this@MyPostsActivity, if (post.isSaved) "Saved" else "Unsaved", Toast.LENGTH_SHORT).show()
                                    },
                                    onLikeClick = { post ->
                                        Toast.makeText(this@MyPostsActivity, "Like clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                                    },
                                    onShareClick = { post ->
                                        Toast.makeText(this@MyPostsActivity, "Share clicked on ${post.userName}'s post", Toast.LENGTH_SHORT).show()
                                    }
                                )
                                postsRecyclerView.adapter = postsAdapter
                                showContentState()
                            } else {
                                showEmptyState()
                            }
                        }
                    } ?: run {
                        withContext(Dispatchers.Main) {
                            showEmptyState()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        when (response.code()) {
                            401 -> {
                                sessionManager.clearSession()
                                val intent = Intent(this@MyPostsActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                showErrorState("Failed to load posts: ${response.code()}")
                            }
                        }
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
            userName = postDto.user.name ?: "Unknown User",
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
    
    fun onCreatePostClick(view: android.view.View) {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivity(intent)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
    
    private fun showLoadingState() {
        // Load and start the GIF animation
        Glide.with(this)
            .load(R.drawable.loading)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(loadingGifView)
        
        loadingGifView.visibility = View.VISIBLE
        postsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
    
    private fun showContentState() {
        loadingGifView.visibility = View.GONE
        postsRecyclerView.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
    
    private fun showEmptyState() {
        loadingGifView.visibility = View.GONE
        postsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.VISIBLE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
    
    private fun showErrorState(message: String) {
        loadingGifView.visibility = View.GONE
        postsRecyclerView.visibility = View.GONE
        emptyStateView.visibility = View.GONE
        errorMessageView.visibility = View.VISIBLE
        errorMessageView.text = message
        retryButton.visibility = View.VISIBLE
    }
}
