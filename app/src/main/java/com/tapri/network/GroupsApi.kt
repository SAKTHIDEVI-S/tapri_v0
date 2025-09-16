package com.tapri.network

import retrofit2.Response
import retrofit2.http.*

interface GroupsApi {
    @GET("test")
    suspend fun testAuth(): Response<String>
    
    @GET("my")
    suspend fun getGroups(): Response<List<GroupDto>>
    
    @POST("create")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<GroupDto>
    
    @POST("{id}/join")
    suspend fun joinGroup(@Path("id") id: Long): Response<GroupDto>
    
    @POST("{id}/leave")
    suspend fun leaveGroup(@Path("id") id: Long): Response<Void>
    
    @GET("{id}/members")
    suspend fun getGroupMembers(@Path("id") id: Long): Response<List<GroupMemberDto>>
    
    @GET("{id}/messages")
    suspend fun getGroupMessages(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<GroupMessagesResponse>
    
    @POST("{id}/messages")
    suspend fun sendGroupMessage(
        @Path("id") id: Long,
        @Body request: SendGroupMessageRequest
    ): Response<GroupMessageDto>
    
    @GET("explore")
    suspend fun exploreGroups(): Response<List<GroupDto>>
    
    @POST("{id}/mark-read")
    suspend fun markMessagesAsRead(@Path("id") id: Long): Response<Void>
}

data class GroupDto(
    val id: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val photoUrl: String? = null,
    val membersCount: Int = 0,
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isMember: Boolean = false,
    
    // Additional fields for frontend
    val avatarUrl: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int? = null,
    val isJoined: Boolean? = null,
    val category: String? = null
) {
    // Helper methods
    fun getDisplayName(): String = name ?: "Unknown Group"
    fun getDisplayDescription(): String = description ?: "No description available"
    fun getDisplayAvatarUrl(): String = avatarUrl ?: photoUrl ?: ""
    fun getDisplayMemberCount(): Int = membersCount
}

data class GroupMemberDto(
    val id: Long,
    val user: UserDto,
    val role: String,
    val joinedAt: String,
    val isActive: Boolean
)


