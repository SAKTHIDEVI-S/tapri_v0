package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tapri.R
import com.tapri.ui.adapters.PostAdapter
import com.tapri.network.ApiClient
import com.tapri.network.PostsApi
import com.tapri.network.PostDto
import com.tapri.utils.SessionManager
import com.tapri.utils.AnimationUtils
import com.tapri.ui.model.Post
import com.tapri.ui.model.MediaType
import com.tapri.utils.TimeUtils
import kotlinx.coroutines.*

class SavedPostsActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var postsApi: PostsApi
    private lateinit var savedPostsRecyclerView: RecyclerView
    private lateinit var loadingGifView: ImageView
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    private lateinit var backButton: TextView
    private var postsAdapter: PostAdapter? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)
        
        // Initialize
        sessionManager = SessionManager(this)
        postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
        
        // Find views
        savedPostsRecyclerView = findViewById(R.id.postsRecyclerView)
        loadingGifView = findViewById(R.id.loadingGifView)
        errorMessageView = findViewById(R.id.errorMessageView)
        retryButton = findViewById(R.id.retryButton)
        backButton = findViewById(R.id.backButton)
        
        // Set title
        findViewById<TextView>(R.id.titleText).text = "Saved Posts"
        
        // Set up retry button
        retryButton.setOnClickListener {
            loadSavedPosts()
        }
        
        // Set up back button
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
        
        // Set up RecyclerView
        savedPostsRecyclerView.layoutManager = LinearLayoutManager(this)
        
        // Load saved posts
        loadSavedPosts()
    }
    
    private fun loadSavedPosts() {
        val currentUser = sessionManager.getUserSession()
        
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        // Show loading state
        showLoadingState()
        
        coroutineScope.launch {
            try {
                android.util.Log.d("SavedPostsActivity", "Loading saved posts...")
                val response = postsApi.getSavedPosts()
                android.util.Log.d("SavedPostsActivity", "Saved posts API response: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val savedPosts = response.body()
                        android.util.Log.d("SavedPostsActivity", "Received ${savedPosts?.size ?: 0} saved posts")
                        
                        savedPosts?.let { posts ->
                            val uiPosts = posts.map { postDto ->
                                convertPostDtoToUiPost(postDto)
                            }
                            
                            postsAdapter = PostAdapter(
                                this@SavedPostsActivity,
                                uiPosts.toMutableList(),
                                onCommentClick = { post ->
                                    // Open comments activity
                                    val intent = Intent(this@SavedPostsActivity, CommentsActivity::class.java)
                                    intent.putExtra("postId", post.id.toLong())
                                    intent.putExtra("postCaption", post.caption)
                                    intent.putExtra("postUserName", post.userName)
                                    intent.putExtra("postUserAvatar", post.userAvatar)
                                    intent.putExtra("postMediaUrl", post.mediaUrl)
                                    intent.putExtra("postMediaType", post.mediaType.toString())
                                    startActivity(intent)
                                },
                                onLikeClick = { post ->
                                    handleLikePost(post)
                                },
                                onSaveClick = { post ->
                                    handleSavePost(post)
                                },
                                onShareClick = { post ->
                                    handleSharePost(post)
                                }
                            )
                            
                            savedPostsRecyclerView.adapter = postsAdapter
                            showContentState()
                            
                        } ?: run {
                            android.util.Log.e("SavedPostsActivity", "Saved posts response body is null")
                            showErrorState("Failed to load saved posts")
                        }
                    } else {
                        android.util.Log.e("SavedPostsActivity", "Saved posts API failed with code: ${response.code()}")
                        when (response.code()) {
                            401 -> {
                                sessionManager.clearSession()
                                val intent = Intent(this@SavedPostsActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                showErrorState("Failed to load saved posts: ${response.code()}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SavedPostsActivity", "Saved posts API exception: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    showErrorState("Network error: ${e.message}")
                    e.printStackTrace()
                }
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
    
    private fun handleLikePost(post: Post) {
        // Similar to HomeActivity implementation
        coroutineScope.launch {
            try {
                val response = postsApi.likePost(post.id.toLong())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val updatedPost = response.body()
                        updatedPost?.let {
                            val uiPost = convertPostDtoToUiPost(it)
                            val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                            if (postIndex != null && postIndex >= 0) {
                                postsAdapter?.updatePost(postIndex, uiPost)
                            }
                        }
                    } else {
                        Toast.makeText(this@SavedPostsActivity, "Failed to update like", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedPostsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun handleSavePost(post: Post) {
        // Similar to HomeActivity implementation
        coroutineScope.launch {
            try {
                val response = postsApi.savePost(post.id.toLong())
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.let {
                            val isSaved = it["isSaved"] as? Boolean ?: false
                            post.isSaved = isSaved
                            val postIndex = postsAdapter?.getPosts()?.indexOfFirst { it.id == post.id }
                            if (postIndex != null && postIndex >= 0) {
                                postsAdapter?.updatePost(postIndex, post)
                            }
                            
                            Toast.makeText(this@SavedPostsActivity, 
                                if (isSaved) "Post saved" else "Post unsaved", 
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SavedPostsActivity, "Failed to update save", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SavedPostsActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun handleSharePost(post: Post) {
        // Similar to HomeActivity implementation
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this post on Tapri!\n\n${post.caption}\n\nDownload Tapri app to see more!")
            putExtra(Intent.EXTRA_SUBJECT, "Tapri Post")
        }
        
        try {
            startActivity(Intent.createChooser(shareIntent, "Share post"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error sharing post: ${e.message}", Toast.LENGTH_SHORT).show()
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
        savedPostsRecyclerView.visibility = View.GONE
    }
    
    private fun showContentState() {
        loadingGifView.visibility = View.GONE
        errorMessageView.visibility = View.GONE
        retryButton.visibility = View.GONE
        savedPostsRecyclerView.visibility = View.VISIBLE
    }
    
    private fun showErrorState(message: String) {
        loadingGifView.visibility = View.GONE
        errorMessageView.text = message
        errorMessageView.visibility = View.VISIBLE
        retryButton.visibility = View.VISIBLE
        savedPostsRecyclerView.visibility = View.GONE
    }
    
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}
