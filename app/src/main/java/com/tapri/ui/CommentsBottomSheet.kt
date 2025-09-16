package com.tapri.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
// import de.hdodenhof.circleimageview.CircleImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tapri.R
import com.tapri.network.PostCommentDto
import com.tapri.network.PostFeedDto
import com.tapri.network.PostsApi
import com.tapri.repository.PostsRepository
import com.tapri.utils.SessionManager
import com.tapri.network.ApiClient
import com.tapri.utils.TimeUtils
import com.tapri.ui.CommentsAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentsBottomSheet : BottomSheetDialogFragment() {
    
    private lateinit var postsRepository: PostsRepository
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var post: PostFeedDto
    private var currentPage = 0
    private var isLoading = false
    private var hasMoreComments = true
    
    // Callback to notify parent when comment is added
    private var onCommentAddedCallback: (() -> Unit)? = null
    
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var postButton: TextView
    private lateinit var loadingView: ImageView
    private lateinit var noCommentsState: LinearLayout
    private lateinit var userAvatar: ImageView
    
    companion object {
        fun newInstance(post: PostFeedDto, onCommentAdded: (() -> Unit)? = null): CommentsBottomSheet {
            val fragment = CommentsBottomSheet()
            val args = Bundle()
            args.putParcelable("post", post)
            fragment.arguments = args
            fragment.onCommentAddedCallback = onCommentAdded
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = arguments?.getParcelable("post", PostFeedDto::class.java) ?: return
        
        // Initialize repository
        val sessionManager = SessionManager(requireContext())
        val postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)
        postsRepository = PostsRepository(postsApi, sessionManager)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_comments, container, false)
        
        // Handle system window insets for navigation bar
        view.setOnApplyWindowInsetsListener { v, insets ->
            val navigationBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.navigationBars()).bottom
            val inputContainer = v.findViewById<LinearLayout>(R.id.commentInputContainer)
            if (inputContainer != null) {
                // Set bottom margin to account for navigation bar + extra space
                val layoutParams = inputContainer.layoutParams as android.widget.LinearLayout.LayoutParams
                layoutParams.bottomMargin = maxOf(48, navigationBarHeight + 32) // Ensure minimum 48dp margin
                inputContainer.layoutParams = layoutParams
            }
            insets
        }
        
        return view
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        return object : com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }.apply {
            // Make the dialog extend to the very bottom but respect system windows
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            
            // Enable system window insets to handle navigation bar
            window?.decorView?.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }
    
    override fun onStart() {
        super.onStart()
        
        // Ensure the bottom sheet extends to the very bottom
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeViews(view)
        setupRecyclerView()
        loadComments()
        setupClickListeners()
    }
    
    private fun initializeViews(view: View) {
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
        commentInput = view.findViewById(R.id.commentInput)
        postButton = view.findViewById(R.id.postButton)
        loadingView = view.findViewById(R.id.loadingView)
        noCommentsState = view.findViewById(R.id.noCommentsState)
        userAvatar = view.findViewById(R.id.userAvatar)
        
        // Set user avatar (current logged in user)
        userAvatar.setImageResource(R.drawable.ic_profile)
        
        // Auto-focus comment input and show keyboard
        commentInput.requestFocus()
        commentInput.post {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, 0)
        }
    }
    
    private fun setupRecyclerView() {
        commentsAdapter = CommentsAdapter(emptyList())
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        postButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
            }
        }
        
        // Enable/disable post button based on text input
        commentInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val hasText = s?.toString()?.trim()?.isNotEmpty() == true
                postButton.alpha = if (hasText) 1.0f else 0.3f
                postButton.isClickable = hasText
            }
        })
    }
    
    private fun loadComments() {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            val result = postsRepository.getComments(post.id, 0, 20)
            
            withContext(Dispatchers.Main) {
                showLoading(false)
                result.fold(
                    onSuccess = { response ->
                        commentsAdapter.updateComments(response.comments)
                        hasMoreComments = response.hasNext
                        currentPage = 0
                        
                        // Show/hide no comments state
                        if (response.comments.isEmpty()) {
                            noCommentsState.visibility = View.VISIBLE
                            commentsRecyclerView.visibility = View.GONE
                        } else {
                            noCommentsState.visibility = View.GONE
                            commentsRecyclerView.visibility = View.VISIBLE
                        }
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), 
                            "Failed to load comments: ${error.message}", 
                            Toast.LENGTH_SHORT).show()
                        noCommentsState.visibility = View.VISIBLE
                        commentsRecyclerView.visibility = View.GONE
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
            val result = postsRepository.getComments(post.id, nextPage, 20)
            
            withContext(Dispatchers.Main) {
                isLoading = false
                result.fold(
                    onSuccess = { response ->
                        commentsAdapter.addComments(response.comments)
                        hasMoreComments = response.hasNext
                        currentPage = nextPage
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), 
                            "Failed to load more comments: ${error.message}", 
                            Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
    
    private fun addComment(content: String) {
        postButton.isEnabled = false
        commentInput.text.clear()
        
        CoroutineScope(Dispatchers.IO).launch {
            val result = postsRepository.addComment(post.id, content)
            
            withContext(Dispatchers.Main) {
                postButton.isEnabled = true
                result.fold(
                    onSuccess = { comment ->
                        commentsAdapter.addComment(comment)
                        commentsRecyclerView.smoothScrollToPosition(0)
                        
                        // Notify parent activity that comment was added
                        onCommentAddedCallback?.invoke()
                    },
                    onFailure = { error ->
                        Toast.makeText(requireContext(), 
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
            Glide.with(requireContext())
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

