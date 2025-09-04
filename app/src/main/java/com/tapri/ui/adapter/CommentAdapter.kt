package com.tapri.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.Comment

class CommentAdapter(
    private val context: Context,
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentUserAvatar: ImageView = itemView.findViewById(R.id.commentUserAvatar)
        val commentUserName: TextView = itemView.findViewById(R.id.commentUserName)
        val commentTime: TextView = itemView.findViewById(R.id.commentTime)
        val commentText: TextView = itemView.findViewById(R.id.commentText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        
        holder.commentUserName.text = comment.userName
        holder.commentTime.text = comment.commentTime
        holder.commentText.text = comment.commentText
        
        // Set default avatar if no custom avatar
        if (comment.userAvatar != null) {
            // Load custom avatar here if needed
            // For now, using default profile icon
        }
    }

    override fun getItemCount(): Int = comments.size
} 