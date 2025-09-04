package com.tapri.ui.model

data class Comment(
    val id: String,
    val userName: String,
    val userAvatar: String?,
    val commentText: String,
    val commentTime: String
) 