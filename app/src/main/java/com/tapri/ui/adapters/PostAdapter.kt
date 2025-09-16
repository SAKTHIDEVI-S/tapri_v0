package com.tapri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tapri.R
import com.tapri.ui.model.Post
import com.tapri.ui.model.MediaType
import com.tapri.utils.AnimationUtils
import com.tapri.utils.Config

class PostAdapter(
    private val context: Context,
    private val posts: MutableList<Post>,
    private val onCommentClick: (Post) -> Unit,
    private val onSaveClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onShareClick: (Post) -> Unit,
    private var postsRecyclerView: RecyclerView? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CREATE_POST_BANNER = 0
        private const val VIEW_TYPE_POST = 1
    }

    inner class CreatePostBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val createPostBanner: LinearLayout = itemView.findViewById(R.id.createPostBanner)
        val postButton: LinearLayout = itemView.findViewById(R.id.postButton)
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val postTime: TextView = itemView.findViewById(R.id.postTime)
        val moreOptions: ImageView = itemView.findViewById(R.id.moreOptions)
        
        // Layout containers
        val textOnlyContainer: LinearLayout = itemView.findViewById(R.id.textOnlyContainer)
        val mediaPostContainer: LinearLayout = itemView.findViewById(R.id.mediaPostContainer)
        
        // Media elements
        val mediaContainer: android.widget.FrameLayout = itemView.findViewById(R.id.mediaContainer)
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postVideo: VideoView = itemView.findViewById(R.id.postVideo)
        val mediaTypeIndicator: LinearLayout = itemView.findViewById(R.id.mediaTypeIndicator)
        val mediaTypeText: TextView = itemView.findViewById(R.id.mediaTypeText)
        val videoPlayButton: ImageView = itemView.findViewById(R.id.videoPlayButton)
        val videoMuteButton: ImageView = itemView.findViewById(R.id.videoMuteButton)
        
        // Store media player reference for volume control
        var mediaPlayer: android.media.MediaPlayer? = null
        
        // Cleanup method for proper resource management
        fun cleanup() {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        
        // Action buttons
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
        
        // Text captions
        val postCaption: TextView = itemView.findViewById(R.id.postCaption)
        val postCaptionMedia: TextView = itemView.findViewById(R.id.postCaptionMedia)
    }

    override fun getItemViewType(position: Int): Int {
        return if (posts[position].id == "create_post_banner") {
            VIEW_TYPE_CREATE_POST_BANNER
        } else {
            VIEW_TYPE_POST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CREATE_POST_BANNER -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_create_post_banner, parent, false)
                CreatePostBannerViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
                PostViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = posts[position]
        
        when (holder) {
            is CreatePostBannerViewHolder -> {
                // Set up create post banner click listener
                holder.createPostBanner.setOnClickListener {
                    val intent = android.content.Intent(context, com.tapri.ui.CreatePostActivity::class.java)
                    context.startActivity(intent)
                }
                
                holder.postButton.setOnClickListener {
                    val intent = android.content.Intent(context, com.tapri.ui.CreatePostActivity::class.java)
                    context.startActivity(intent)
                }
            }
            is PostViewHolder -> {
                // Debug logging for button initialization
                android.util.Log.d("PostAdapter", "Binding post ${post.id} at position $position")
                android.util.Log.d("PostAdapter", "Like button: ${holder.likeButton}, Comment button: ${holder.commentButton}, Share button: ${holder.shareButton}, Save button: ${holder.saveButton}")

                // Cleanup previous media player to prevent memory leaks
                holder.cleanup()

                // Add staggered animation for list items
                holder.itemView.alpha = 0f
                holder.itemView.translationY = 50f
                holder.itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(position * 100L)
                    .start()

                // Set post data
                holder.userName.text = post.userName
                holder.postTime.text = post.postTime
                holder.likeCount.text = post.likeCount.toString()
                holder.commentCount.text = post.commentCount.toString()
                holder.shareCount.text = post.shareCount.toString()
                
                // Set like icon state based on post.isLiked
                android.util.Log.d("PostAdapter", "Binding post ${post.id}: isLiked=${post.isLiked}, likeCount=${post.likeCount}")
                val likeIconResource = if (post.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
                holder.likeIcon.setImageResource(likeIconResource)
                android.util.Log.d("PostAdapter", "Set like icon resource: ${if (post.isLiked) "filled" else "outline"} for post ${post.id}")
                
                // Set profile picture
                if (!post.userAvatar.isNullOrEmpty() && post.userAvatar != "null") {
                    val fullAvatarUrl = convertToFullUrl(post.userAvatar)
                    Glide.with(context)
                        .load(fullAvatarUrl)
                        .apply(RequestOptions()
                            .circleCrop()
                            .placeholder(R.drawable.ic_profile)
                            .error(R.drawable.ic_profile))
                        .into(holder.userAvatar)
                } else {
                    holder.userAvatar.setImageResource(R.drawable.ic_profile)
                }

                // Handle different media types and layout
                setupPostLayout(holder, post)
                
                // Add staggered entrance animation
                AnimationUtils.staggeredEntrance(holder.itemView, position, 50)

                // Set save icon state
                if (post.isSaved) {
                    holder.saveIcon.setImageResource(R.drawable.ic_save_filled)
                } else {
                    holder.saveIcon.setImageResource(R.drawable.ic_save)
                }

                // Three dots menu click
                holder.moreOptions.setOnClickListener {
                    AnimationUtils.animateButtonPress(it) {
                        showOptionsPopup(it, post)
                    }
                }

                // Comment button click
                holder.commentButton?.setOnClickListener {
                    android.util.Log.d("PostAdapter", "Comment button clicked for post ${post.id}")
                    // Call callback immediately without animation wrapper
                    onCommentClick(post)
                }

                // Save button click
                holder.saveButton?.setOnClickListener {
                    android.util.Log.d("PostAdapter", "Save button clicked for post ${post.id}")
                    // Call callback immediately without animation wrapper
                    onSaveClick(post)
                }

                // Share button click
                holder.shareButton?.setOnClickListener {
                    android.util.Log.d("PostAdapter", "Share button clicked for post ${post.id}")
                    // Call callback immediately without animation wrapper
                    onShareClick(post)
                }

                // Like button click
                holder.likeButton?.setOnClickListener {
                    android.util.Log.d("PostAdapter", "Like button clicked for post ${post.id} - current state: isLiked=${post.isLiked}, likeCount=${post.likeCount}")
                    // Don't update UI immediately - wait for backend response
                    // Just call the API callback
                    onLikeClick(post)
                }

                // Video play button click
                holder.videoPlayButton.setOnClickListener {
                    AnimationUtils.animateButtonPress(it) {
                        if (post.mediaType == MediaType.VIDEO) {
                            toggleVideoPlayback(holder, post)
                        }
                    }
                }
            }
        }
    }

    private fun setupPostLayout(holder: PostViewHolder, post: Post) {
        // Reset all containers
        holder.textOnlyContainer.visibility = View.GONE
        holder.mediaPostContainer.visibility = View.GONE
        
        // Check if post has media
        val hasMedia = !post.mediaUrl.isNullOrEmpty() && post.mediaUrl != "null" && post.mediaUrl != "null"
        
        // Debug logging
        android.util.Log.d("PostAdapter", "Post ID: ${post.id}, Media URL: '${post.mediaUrl}', Has Media: $hasMedia, Media Type: ${post.mediaType}")
        android.util.Log.d("PostAdapter", "Full post data: userName=${post.userName}, caption='${post.caption}', likeCount=${post.likeCount}")
        
        // Log the converted URL for debugging
        if (!post.mediaUrl.isNullOrEmpty() && post.mediaUrl != "null") {
            val fullUrl = convertToFullUrl(post.mediaUrl)
            android.util.Log.d("PostAdapter", "Original URL: '${post.mediaUrl}' -> Full URL: '$fullUrl'")
        }
        
        if (hasMedia) {
            // Show media post layout: media -> text -> actions
            holder.mediaPostContainer.visibility = View.VISIBLE
            holder.postCaptionMedia.text = post.caption
            setupMediaContent(holder, post)
        } else {
            // Show text-only layout: text -> actions
            holder.textOnlyContainer.visibility = View.VISIBLE
            holder.postCaption.text = post.caption
        }
    }
    
    private fun setupMediaContent(holder: PostViewHolder, post: Post) {
        // Reset all media views
        holder.postImage.visibility = View.GONE
        holder.postVideo.visibility = View.GONE
        holder.mediaTypeIndicator.visibility = View.GONE
        holder.videoPlayButton.visibility = View.GONE
        holder.videoMuteButton.visibility = View.GONE

        // Show media container when there's media
        holder.mediaContainer.visibility = View.VISIBLE

        when (post.mediaType) {
            MediaType.IMAGE -> {
                holder.postImage.visibility = View.VISIBLE
                
                // Convert relative URL to full URL
                val fullImageUrl = convertToFullUrl(post.mediaUrl)
                android.util.Log.d("PostAdapter", "Loading IMAGE: ${post.mediaUrl} -> $fullImageUrl")
                
                // Load image from URL using Glide
                try {
                    Glide.with(context)
                        .load(fullImageUrl)
                        .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.sample_traffic)
                            .error(R.drawable.sample_traffic)
                            .timeout(10000))
                        .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                            override fun onLoadFailed(
                                e: com.bumptech.glide.load.engine.GlideException?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                android.util.Log.e("PostAdapter", "Glide load failed for $fullImageUrl: ${e?.message}")
                                // Set a fallback image when loading fails
                                holder.postImage.setImageResource(R.drawable.sample_traffic)
                                return true // Return true to prevent default error handling
                            }
                            
                            override fun onResourceReady(
                                resource: android.graphics.drawable.Drawable?,
                                model: Any?,
                                target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                                dataSource: com.bumptech.glide.load.DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                android.util.Log.d("PostAdapter", "Glide load successful for $fullImageUrl")
                                return false
                            }
                        })
                        .into(holder.postImage)
                } catch (e: Exception) {
                    android.util.Log.e("PostAdapter", "Exception loading image: ${e.message}")
                    // Fallback to sample image on error
                    holder.postImage.setImageResource(R.drawable.sample_traffic)
                }
            }
            MediaType.GIF -> {
                holder.postImage.visibility = View.VISIBLE
                holder.mediaTypeIndicator.visibility = View.VISIBLE
                holder.mediaTypeText.text = "GIF"
                // Load GIF from URL using Glide
                val fullGifUrl = convertToFullUrl(post.mediaUrl)
                if (fullGifUrl != null) {
                    Glide.with(context)
                        .asGif()
                        .load(fullGifUrl)
                        .apply(RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.sample_traffic)
                            .error(R.drawable.sample_traffic))
                        .into(holder.postImage)
                } else {
                    holder.postImage.setImageResource(R.drawable.sample_traffic)
                }
            }
            MediaType.VIDEO -> {
                holder.postVideo.visibility = View.VISIBLE
                holder.videoPlayButton.visibility = View.VISIBLE
                holder.videoMuteButton.visibility = View.VISIBLE
                holder.mediaTypeIndicator.visibility = View.VISIBLE
                holder.mediaTypeText.text = "VIDEO"
                
                val fullVideoUrl = convertToStreamingUrl(post.mediaUrl)
                android.util.Log.d("PostAdapter", "Loading VIDEO: ${post.mediaUrl} -> $fullVideoUrl")
                android.util.Log.d("PostAdapter", "Video URL details: original='${post.mediaUrl}', streaming='$fullVideoUrl'")
                
                if (fullVideoUrl != null && fullVideoUrl.isNotEmpty()) {
                    try {
                        // Set video URI to VideoView
                        val videoUri = android.net.Uri.parse(fullVideoUrl)
                        holder.postVideo.setVideoURI(videoUri)
                        
                        // Configure video settings
                        holder.postVideo.setOnPreparedListener { mediaPlayer ->
                            android.util.Log.d("PostAdapter", "Video prepared successfully for: $fullVideoUrl")
                            // Store media player reference for volume control
                            holder.mediaPlayer = mediaPlayer
                            
                            // Video is prepared, show first frame and configure for auto-loop
                            mediaPlayer.isLooping = true
                            holder.postVideo.seekTo(1)
                            
                            // Set mute button icon to muted state and mute by default
                            holder.videoMuteButton.setImageResource(R.drawable.ic_volume_off)
                            holder.videoMuteButton.tag = "muted"
                            mediaPlayer.setVolume(0f, 0f) // Mute by default
                            
                            // Auto-play when video is prepared (for better UX)
                            holder.postVideo.start()
                            holder.videoPlayButton.visibility = View.GONE
                        }
                        
                        holder.postVideo.setOnCompletionListener {
                            android.util.Log.d("PostAdapter", "Video completed, restarting for loop")
                            // Restart video for looping
                            holder.postVideo.start()
                        }
                        
                        holder.postVideo.setOnErrorListener { _, what, extra ->
                            // If video fails to load, show placeholder image
                            android.util.Log.e("PostAdapter", "Video error for $fullVideoUrl: what=$what, extra=$extra")
                            android.util.Log.e("PostAdapter", "Video error details: what=${android.media.MediaPlayer.MEDIA_ERROR_UNKNOWN}, extra=${android.media.MediaPlayer.MEDIA_ERROR_MALFORMED}")
                            
                            // Show error state with video icon
                            holder.postVideo.visibility = View.GONE
                            holder.postImage.visibility = View.VISIBLE
                            holder.postImage.setImageResource(R.drawable.video_placeholder) // Use video placeholder instead of sample image
                            holder.videoPlayButton.visibility = View.VISIBLE
                            holder.videoMuteButton.visibility = View.GONE
                            holder.mediaTypeIndicator.visibility = View.VISIBLE
                            holder.mediaTypeText.text = "VIDEO ERROR"
                            
                            // Set up play button to retry
                            holder.videoPlayButton.setOnClickListener {
                                android.util.Log.d("PostAdapter", "Retrying video load for: $fullVideoUrl")
                                // Retry loading the video
                                holder.postVideo.setVideoURI(videoUri)
                            }
                            
                            true
                        }
                        
                        // Set up mute button click listener
                        holder.videoMuteButton.setOnClickListener {
                            toggleVideoMute(holder)
                        }
                        
                        // Set up video tap to play/pause
                        holder.postVideo.setOnClickListener {
                            toggleVideoPlayback(holder, post)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("PostAdapter", "Exception setting up video: ${e.message}", e)
                        // Show error state
                        holder.postVideo.visibility = View.GONE
                        holder.postImage.visibility = View.VISIBLE
                        holder.postImage.setImageResource(R.drawable.video_placeholder)
                        holder.videoPlayButton.visibility = View.VISIBLE
                        holder.videoMuteButton.visibility = View.GONE
                        holder.mediaTypeIndicator.visibility = View.VISIBLE
                        holder.mediaTypeText.text = "VIDEO ERROR"
                    }
                } else {
                    android.util.Log.w("PostAdapter", "Video URL is null or empty: '$fullVideoUrl'")
                    holder.postVideo.visibility = View.GONE
                    holder.postImage.visibility = View.VISIBLE
                    holder.postImage.setImageResource(R.drawable.video_placeholder)
                    holder.videoPlayButton.visibility = View.VISIBLE
                    holder.videoMuteButton.visibility = View.GONE
                    holder.mediaTypeIndicator.visibility = View.VISIBLE
                    holder.mediaTypeText.text = "NO VIDEO"
                }
            }
            MediaType.AUDIO -> {
                holder.postImage.visibility = View.VISIBLE
                holder.mediaTypeIndicator.visibility = View.VISIBLE
                holder.mediaTypeText.text = "AUDIO"
                holder.videoPlayButton.visibility = View.VISIBLE
                
                // Show audio placeholder
                holder.postImage.setImageResource(R.drawable.audio_placeholder)
                
                // Set up audio play button
                holder.videoPlayButton.setOnClickListener {
                    AnimationUtils.animateButtonPress(it) {
                        toggleAudioPlayback(holder, post)
                    }
                }
            }
        }
    }

    private fun toggleVideoPlayback(holder: PostViewHolder, post: Post) {
        if (holder.postVideo.isPlaying) {
            holder.postVideo.pause()
            holder.videoPlayButton.visibility = View.VISIBLE
        } else {
            holder.postVideo.start()
            holder.videoPlayButton.visibility = View.GONE
        }
    }
    
    private fun toggleVideoMute(holder: PostViewHolder) {
        val mediaPlayer = holder.mediaPlayer
        if (mediaPlayer != null) {
            if (holder.videoMuteButton.tag == "muted") {
                // Unmute: set volume to full
                mediaPlayer.setVolume(1f, 1f)
                holder.videoMuteButton.setImageResource(R.drawable.ic_volume_on)
                holder.videoMuteButton.tag = "unmuted"
            } else {
                // Mute: set volume to zero
                mediaPlayer.setVolume(0f, 0f)
                holder.videoMuteButton.setImageResource(R.drawable.ic_volume_off)
                holder.videoMuteButton.tag = "muted"
            }
        }
    }
    
    private fun toggleAudioPlayback(holder: PostViewHolder, post: Post) {
        // For audio posts, we'll use a simple MediaPlayer approach
        // In a production app, you'd want to manage audio playback more sophisticatedly
        try {
            val audioUrl = convertToFullUrl(post.mediaUrl)
            if (audioUrl != null) {
                // Toggle play/pause for audio
                if (holder.mediaPlayer?.isPlaying == true) {
                    holder.mediaPlayer?.pause()
                    holder.videoPlayButton.setImageResource(R.drawable.ic_play)
                } else {
                    // Start playing audio
                    if (holder.mediaPlayer == null) {
                        holder.mediaPlayer = android.media.MediaPlayer().apply {
                            setDataSource(audioUrl)
                            prepare()
                        }
                    }
                    holder.mediaPlayer?.start()
                    holder.videoPlayButton.setImageResource(R.drawable.ic_pause)
                    
                    // Set completion listener to reset button
                    holder.mediaPlayer?.setOnCompletionListener {
                        holder.videoPlayButton.setImageResource(R.drawable.ic_play)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("PostAdapter", "Audio playback error: ${e.message}")
            holder.videoPlayButton.setImageResource(R.drawable.ic_play)
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
    
    private fun convertToFullUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty() || relativeUrl == "null") {
            android.util.Log.d("PostAdapter", "convertToFullUrl: URL is null or empty")
            return null
        }
        
        // If it's already a full URL, return as is
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            android.util.Log.d("PostAdapter", "convertToFullUrl: Already full URL: $relativeUrl")
            return relativeUrl
        }
        
        // Use Config to get absolute media URL
        val fullUrl = Config.getAbsoluteMediaUrl(relativeUrl)
        android.util.Log.d("PostAdapter", "convertToFullUrl: Converted '$relativeUrl' to '$fullUrl'")
        return fullUrl
    }
    
    private fun convertToStreamingUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty() || relativeUrl == "null") {
            android.util.Log.d("PostAdapter", "convertToStreamingUrl: URL is null or empty")
            return null
        }
        
        // If it's already a full URL, return as is
        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://")) {
            android.util.Log.d("PostAdapter", "convertToStreamingUrl: Already full URL: $relativeUrl")
            return relativeUrl
        }
        
        // Use Config to get streaming media URL for videos
        val streamingUrl = Config.getStreamingMediaUrl(relativeUrl)
        android.util.Log.d("PostAdapter", "convertToStreamingUrl: Converted '$relativeUrl' to '$streamingUrl'")
        return streamingUrl
    }
    
    // Helper methods for HomeActivity
    fun getPosts(): List<Post> {
        return posts
    }
    
    fun updatePost(index: Int, updatedPost: Post) {
        android.util.Log.d("PostAdapter", "updatePost called: index=$index, postId=${updatedPost.id}, isLiked=${updatedPost.isLiked}, likeCount=${updatedPost.likeCount}")
        if (index >= 0 && index < posts.size) {
            val oldPost = posts[index]
            android.util.Log.d("PostAdapter", "Old post: id=${oldPost.id}, isLiked=${oldPost.isLiked}, likeCount=${oldPost.likeCount}")
            
            posts[index] = updatedPost
            
            // Force complete rebind by using notifyDataSetChanged to ensure UI consistency
            // This is more reliable than notifyItemChanged for complex state changes
            notifyDataSetChanged()
            
            android.util.Log.d("PostAdapter", "Post updated successfully at index $index")
        } else {
            android.util.Log.e("PostAdapter", "Invalid index $index, posts size: ${posts.size}")
        }
    }
    
    // Set the RecyclerView reference for direct view updates
    fun setRecyclerView(recyclerView: RecyclerView) {
        postsRecyclerView = recyclerView
    }
    
    // Method to specifically update like button state for immediate UI feedback
    fun updateLikeState(index: Int, isLiked: Boolean, likeCount: Int) {
        if (index >= 0 && index < posts.size) {
            posts[index].isLiked = isLiked
            posts[index].likeCount = likeCount
            
            // Get the view holder and update the like button directly
            val viewHolder = postsRecyclerView?.findViewHolderForAdapterPosition(index) as? PostViewHolder
            viewHolder?.let { holder ->
                holder.likeIcon.setImageResource(
                    if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
                )
                holder.likeCount.text = likeCount.toString()
                android.util.Log.d("PostAdapter", "Like state updated directly for post ${posts[index].id}: isLiked=$isLiked, count=$likeCount")
            }
        }
    }
}
