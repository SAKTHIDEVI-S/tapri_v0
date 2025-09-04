package com.tapri.ui.model

data class Post(
    val id: String,
    val userName: String,
    val postTime: String,
    val caption: String,
    val imageUrl: String?,
    var likeCount: Int,
    var commentCount: Int,
    var shareCount: Int,
    var isLiked: Boolean = false,
    var isSaved: Boolean = false
) 