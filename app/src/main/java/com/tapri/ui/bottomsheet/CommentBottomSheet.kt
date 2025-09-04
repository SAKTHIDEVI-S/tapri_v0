package com.tapri.ui.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tapri.R
import com.tapri.ui.adapter.CommentAdapter
import com.tapri.ui.model.Comment
import com.tapri.ui.model.Post

class CommentBottomSheet : BottomSheetDialogFragment() {

    private var post: Post? = null
    private var onCommentAdded: ((String) -> Unit)? = null
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentInput: EditText
    private lateinit var sendCommentButton: ImageView
    private lateinit var commentCountText: TextView

    companion object {
        fun newInstance(post: Post, onCommentAdded: (String) -> Unit): CommentBottomSheet {
            val fragment = CommentBottomSheet()
            fragment.post = post
            fragment.onCommentAdded = onCommentAdded
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadComments()
    }

    private fun initViews(view: View) {
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
        commentInput = view.findViewById(R.id.commentInput)
        sendCommentButton = view.findViewById(R.id.sendCommentButton)
        commentCountText = view.findViewById(R.id.commentCount)
        
        // Set comment count
        post?.let { post ->
            commentCountText.text = "${post.commentCount} comments"
        }
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter(requireContext(), getSampleComments())
        commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }
    }

    private fun setupClickListeners() {
        sendCommentButton.setOnClickListener {
            val commentText = commentInput.text.toString().trim()
            if (commentText.isNotEmpty()) {
                addComment(commentText)
                commentInput.text.clear()
            }
        }
    }

    private fun addComment(commentText: String) {
        // Add new comment to the list
        val newComment = Comment(
            id = System.currentTimeMillis().toString(),
            userName = "You",
            userAvatar = null,
            commentText = commentText,
            commentTime = "Just now"
        )
        
        // Update the adapter with new comment
        val updatedComments = getSampleComments().toMutableList()
        updatedComments.add(0, newComment)
        commentAdapter = CommentAdapter(requireContext(), updatedComments)
        commentsRecyclerView.adapter = commentAdapter
        
        // Update comment count
        post?.commentCount = (post?.commentCount ?: 0) + 1
        commentCountText.text = "${post?.commentCount} comments"
        
        // Notify parent about new comment
        onCommentAdded?.invoke(commentText)
    }

    private fun getSampleComments(): List<Comment> {
        return listOf(
            Comment(
                id = "1",
                userName = "John Driver",
                userAvatar = null,
                commentText = "Great information! This will help a lot of drivers.",
                commentTime = "2 hrs ago"
            ),
            Comment(
                id = "2",
                userName = "Sarah Driver",
                userAvatar = null,
                commentText = "Thanks for sharing this update!",
                commentTime = "1 hr ago"
            ),
            Comment(
                id = "3",
                userName = "Mike Driver",
                userAvatar = null,
                commentText = "I've been experiencing the same issue.",
                commentTime = "30 mins ago"
            )
        )
    }

    private fun loadComments() {
        // In a real app, you would load comments from API/database
        // For now, using sample data
    }
} 