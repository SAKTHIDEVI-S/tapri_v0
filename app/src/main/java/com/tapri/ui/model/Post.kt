package com.tapri.ui.model

enum class MediaType {
    IMAGE, GIF, VIDEO, AUDIO
}

data class Post(
    val id: String,
    val userName: String,
    val userAvatar: String? = null,
    val postTime: String,
    val caption: String,
    val mediaUrl: String?,
    val mediaType: MediaType = MediaType.IMAGE,
    var likeCount: Int,
    var commentCount: Int,
    var shareCount: Int,
    var isLiked: Boolean = false,
    var isSaved: Boolean = false
) 