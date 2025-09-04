package com.tapri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.Post

class PostAdapter(
    private val context: Context,
    private val posts: List<Post>,
    private val onCommentClick: (Post) -> Unit,
    private val onSaveClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val postTime: TextView = itemView.findViewById(R.id.postTime)
        val moreOptions: ImageView = itemView.findViewById(R.id.moreOptions)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val likeButton: LinearLayout = itemView.findViewById(R.id.likeButton)
        val likeIcon: ImageView = itemView.findViewById(R.id.likeIcon)
        val likeCount: TextView = itemView.findViewById(R.id.likeCount)
        val commentButton: LinearLayout = itemView.findViewById(R.id.commentButton)
        val commentIcon: ImageView = itemView.findViewById(R.id.commentIcon)
        val commentCount: TextView = itemView.findViewById(R.id.commentCount)
        val shareButton: LinearLayout = itemView.findViewById(R.id.shareButton)
        val shareIcon: ImageView = itemView.findViewById(R.id.shareIcon)
        val shareCount: TextView = itemView.findViewById(R.id.shareCount)
        val saveButton: LinearLayout = itemView.findViewById(R.id.saveButton)
        val saveIcon: ImageView = itemView.findViewById(R.id.saveIcon)
        val postCaption: TextView = itemView.findViewById(R.id.postCaption)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        // Set post data
        holder.userName.text = post.userName
        holder.postTime.text = post.postTime
        holder.likeCount.text = post.likeCount.toString()
        holder.commentCount.text = post.commentCount.toString()
        holder.shareCount.text = post.shareCount.toString()
        holder.postCaption.text = post.caption

        // Set save icon state
        if (post.isSaved) {
            holder.saveIcon.setImageResource(R.drawable.ic_save_filled)
        } else {
            holder.saveIcon.setImageResource(R.drawable.ic_save)
        }

        // Three dots menu click
        holder.moreOptions.setOnClickListener {
            showOptionsPopup(it, post)
        }

        // Comment button click
        holder.commentButton.setOnClickListener {
            onCommentClick(post)
        }

        // Save button click
        holder.saveButton.setOnClickListener {
            onSaveClick(post)
        }

        // Like button click
        holder.likeButton.setOnClickListener {
            post.isLiked = !post.isLiked
            if (post.isLiked) {
                post.likeCount++
                holder.likeIcon.setImageResource(R.drawable.ic_like_filled)
            } else {
                post.likeCount--
                holder.likeIcon.setImageResource(R.drawable.ic_like)
            }
            holder.likeCount.text = post.likeCount.toString()
        }
    }

    override fun getItemCount(): Int = posts.size

    private fun showOptionsPopup(anchorView: View, post: Post) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_post_options, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Set up popup options
        popupView.findViewById<LinearLayout>(R.id.interestedOption).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.reportOption).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.shareOption).setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.findViewById<LinearLayout>(R.id.copyLinkOption).setOnClickListener {
            popupWindow.dismiss()
        }

        // Show popup below the anchor view
        popupWindow.showAsDropDown(anchorView, 0, 0)
    }
}
