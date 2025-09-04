package com.tapri.model

data class TapriGroup(
    val id: Long,
    val name: String,
    val description: String,
    val followerCount: String,
    val groupAvatar: Int, // drawable resource for now
    val isJoined: Boolean = false
)