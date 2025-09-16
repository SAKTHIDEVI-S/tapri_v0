package com.tapri.network

data class CreateGroupRequest(
    val name: String,
    val description: String,
    val photoUrl: String? = null,
    val category: String? = "General"
)
