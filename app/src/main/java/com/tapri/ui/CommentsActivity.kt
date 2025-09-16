package com.tapri.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tapri.R
import com.tapri.network.PostCommentDto
import com.tapri.network.PostFeedDto
import com.tapri.network.PostsApi
import com.tapri.repository.PostsRepository
import com.tapri.utils.SessionManager
import com.tapri.network.ApiClient
import com.tapri.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsActivity : AppCompatActivity() {
    
    private lateinit var postsRepository: PostsRepository
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var post: PostFeedDto
    private var currentPage = 0
    private var isLoading = false
    private var hasMoreComments = true
    
    private lateinit var backButton: ImageButton
    private lateinit var postAuthorAvatar: ImageView
    private lateinit var postAuthorName: TextView
    private lateinit var postTime: TextView
    private lateinit var postCaption: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var loadingView: ImageView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        
        // Get post data from intent
        post = intent.getParcelableExtra("post") ?: return
        
        // Initialize repository
        val sessionManager = SessionManager(this)
        val postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
        postsRepository = PostsRepository(postsApi, sessionManager)
        
        initializeViews()
        setupRecyclerView()
        loadComments()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        backButton = findViewById(R.id.backButton)
        postAuthorAvatar = findViewById(R.id.postAuthorAvatar)
        postAuthorName = findViewById(R.id.postAuthorName)
        postTime = findViewById(R.id.postTime)
        postCaption = findViewById(R.id.postCaption)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentInput = findViewById(R.id.commentInput)
        sendButton = findViewById(R.id.sendButton)
        loadingView = findViewById(R.id.loadingView)
        
        // Set post header data
        postAuthorName.text = post.userName
        postTime.text = post.postTime
        postCaption.text = post.caption
        
        // Load author avatar
        if (!post.userAvatar.isNullOrEmpty() && post.userAvatar != "null") {
            val fullAvatarUrl = convertToFullUrl(post.userAvatar)
            Glide.with(this)
                .load(fullAvatarUrl)
                .apply(RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile))
                .into(postAuthorAvatar)
        } else {
            postAuthorAvatar.setImageResource(R.drawable.ic_profile)
        }
    }
    
    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter(emptyList())
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = commentsAdapter
        
        // Add scroll listener for pagination
        commentsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                
                if (!isLoading && hasMoreComments && lastVisibleItem >= totalItemCount - 3) {
                    loadMoreComments()
                }
            }
        })
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        sendButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            }
        }
    }
    
    private fun loadComments() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val result = postsRepository.getComments(post.id.toLong(), 0, 20)
            
            withContext(Dispatchers.Main) {
                showLoading(false)
                result.fold(
                    onSuccess = { response ->
                        commentsAdapter.updateComments(response.comments)
                        hasMoreComments = response.hasNext
                        currentPage = 0
                    },
                    onFailure = { error ->
                        Toast.makeText(this@CommentsActivity, 
                            "Failed to load comments: ${error.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
    
    private fun loadMoreComments() {
        if (isLoading || !hasMoreComments) return
        
        isLoading = true
        val nextPage = currentPage + 1
        
        CoroutineScope(Dispatchers.IO).launch {
            val result = postsRepository.getComments(post.id.toLong(), nextPage, 20)
            
            withContext(Dispatchers.Main) {
                isLoading = false
                result.fold(
                    onSuccess = { response ->
                        commentsAdapter.addComments(response.comments)
                        hasMoreComments = response.hasNext
                        currentPage = nextPage
                    },
                    onFailure = { error ->
                        Toast.makeText(this@CommentsActivity, 
                            "Failed to load more comments: ${error.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
    
    private fun addComment(content: String) {
        sendButton.isEnabled = false
        commentInput.text.clear()
        
        CoroutineScope(Dispatchers.IO).launch {
            val result = postsRepository.addComment(post.id.toLong(), content)
            
            withContext(Dispatchers.Main) {
                sendButton.isEnabled = true
                result.fold(
                    onSuccess = { comment ->
                        commentsAdapter.addComment(comment)
                        commentsRecyclerView.smoothScrollToPosition(0)
                    },
                    onFailure = { error ->
                        Toast.makeText(this@CommentsActivity, 
                            "Failed to add comment: ${error.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
    
    private fun showLoading(show: Boolean) {
        if (show) {
            // Load and start the GIF animation
            Glide.with(this)
                .load(R.drawable.loading)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(loadingView)
        }
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun convertToFullUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty()) return null
        
        return com.tapri.utils.Config.getAbsoluteMediaUrl(relativeUrl)
    }
}

class CommentsAdapter(
    private var comments: List<PostCommentDto>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: ImageView = itemView.findViewById(R.id.commentUserAvatar)
        val userName: TextView = itemView.findViewById(R.id.commentUserName)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
        val commentTime: TextView = itemView.findViewById(R.id.commentTime)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        
        holder.userName.text = comment.user.name
        holder.commentText.text = comment.content
        holder.commentTime.text = TimeUtils.getRelativeTime(comment.createdAt)
        
        // Load user avatar
        if (!comment.user.profilePictureUrl.isNullOrEmpty() && comment.user.profilePictureUrl != "null") {
            val fullAvatarUrl = convertToFullUrl(comment.user.profilePictureUrl)
            Glide.with(holder.itemView.context)
                .load(fullAvatarUrl)
                .apply(RequestOptions()
                    .circleCrop()
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile))
                .into(holder.userAvatar)
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_profile)
        }
    }
    
    override fun getItemCount(): Int = comments.size
    
    fun updateComments(newComments: List<PostCommentDto>) {
        comments = newComments
        notifyDataSetChanged()
    }
    
    fun addComments(newComments: List<PostCommentDto>) {
        val startPosition = comments.size
        comments = comments + newComments
        notifyItemRangeInserted(startPosition, newComments.size)
    }
    
    fun addComment(comment: PostCommentDto) {
        comments = listOf(comment) + comments
        notifyItemInserted(0)
    }
    
    private fun convertToFullUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty()) return null
        
        return com.tapri.utils.Config.getAbsoluteMediaUrl(relativeUrl)
    }
}
