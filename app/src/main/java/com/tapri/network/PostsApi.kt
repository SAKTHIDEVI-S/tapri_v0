package com.tapri.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody

interface PostsApi {
    @GET("feed")
    suspend fun getPostsFeed(): Response<List<PostFeedDto>>

    @GET("{id}")
    suspend fun getPost(@Path("id") id: Long): Response<PostDto>

    @POST("create")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostDto>

    @Multipart
    @POST("with-image")
    suspend fun createPostWithImage(
        @Part("text") text: String,
        @Part image: MultipartBody.Part
    ): Response<Map<String, Any>>


    @POST("{id}/like")
    suspend fun likePost(@Path("id") id: Long): Response<PostDto>

    @POST("{id}/share")
    suspend fun sharePost(@Path("id") id: Long): Response<Map<String, Any>>

    @POST("{id}/save")
    suspend fun savePost(@Path("id") id: Long): Response<Map<String, Any>>

    @GET("{id}/comments")
    suspend fun getComments(
        @Path("id") id: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<CommentsResponse>

    @POST("{id}/comment")
    suspend fun addComment(
        @Path("id") id: Long,
        @Body request: AddCommentRequest
    ): Response<PostCommentDto>

    @GET("me/saved")
    suspend fun getSavedPosts(): Response<List<PostDto>>

    @GET("user/{userId}")
    suspend fun getUserPosts(@Path("userId") userId: Long): Response<List<PostDto>>
    
    @POST("{id}/share-to-group")
    suspend fun sharePostToGroup(
        @Path("id") id: Long,
        @Body request: ShareToGroupRequest
    ): Response<Map<String, Any>>
}

data class ShareToGroupRequest(
    val groupId: Long
)

data class PostsFeedResponse(
    val content: List<PostDto>,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val last: Boolean
)

data class PostDto(
    val id: Long,
    val text: String,
    val mediaUrl: String?,
    val mediaType: String?,
    val postType: String?,
    val audience: String?,
    val shareCount: Int,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val user: UserDto,
    @SerializedName("likesCount")
    val likeCount: Int,
    @SerializedName("commentsCount") 
    val commentCount: Int,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

data class UserDto(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val profilePictureUrl: String?,
    val city: String? = null,
    val state: String? = null,
    val bio: String? = null,
    val rating: Double? = null,
    val earnings: Double? = null,
    val lastSeen: String? = null,
    val lastLogin: String? = null,
    val lastSeenVisible: Boolean? = null,
    val createdAt: String? = null
)

@Parcelize
data class PostFeedDto(
    val id: Long,
    val userName: String,
    val userAvatar: String?,
    val postTime: String,
    val caption: String,
    val mediaUrl: String?,
    val mediaType: String?,
    val postType: String?,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int,
    val isLiked: Boolean,
    val isSaved: Boolean
) : Parcelable

data class CreatePostRequest(
    val text: String,
    val mediaUrl: String?,
    val mediaType: String?,
    val postType: String?,
    val audience: String?
)

data class CommentsResponse(
    val comments: List<PostCommentDto>,
    val totalElements: Int,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int,
    val hasNext: Boolean
)

data class PostCommentDto(
    val id: Long,
    val content: String,
    val createdAt: String,
    val user: UserDto
)

data class AddCommentRequest(
    val content: String
)
