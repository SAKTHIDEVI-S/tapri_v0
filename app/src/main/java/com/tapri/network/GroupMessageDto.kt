package com.tapri.network

data class GroupMessageDto(
    val id: Long? = null,
    val user: UserDto? = null,
    val content: String? = null,
    val mediaUrl: String? = null,
    val mediaType: String? = null,
    val isEdited: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)


data class GroupMessagesResponse(
    val messages: List<GroupMessageDto> = emptyList(),
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val size: Int = 0,
    val hasNext: Boolean = false
)

data class SendGroupMessageRequest(
    val content: String,
    val mediaUrl: String? = null,
    val mediaType: String? = null
)
