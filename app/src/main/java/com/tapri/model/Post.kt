package com.tapri.model

data class Post(
    val id: Long,
    val userName: String,
    val userAvatar: Int, // drawable resource for now
    val postTime: String,
    val postImage: Int, // drawable resource for now
    val postContent: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int
)