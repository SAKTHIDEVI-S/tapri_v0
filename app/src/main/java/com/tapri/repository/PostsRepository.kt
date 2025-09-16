package com.tapri.repository

import com.tapri.network.PostsApi
import com.tapri.network.PostFeedDto
import com.tapri.network.PostDto
import com.tapri.network.PostCommentDto
import com.tapri.network.CommentsResponse
import com.tapri.network.AddCommentRequest
import com.tapri.utils.SessionManager
import retrofit2.Response

class PostsRepository(
    private val postsApi: PostsApi,
    private val sessionManager: SessionManager
) {
    
    suspend fun getPostsFeed(): Result<List<PostFeedDto>> {
        return try {
            val response = postsApi.getPostsFeed()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch posts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getPost(postId: Long): Result<PostDto> {
        return try {
            val response = postsApi.getPost(postId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun likePost(postId: Long): Result<PostDto> {
        return try {
            val response = postsApi.likePost(postId)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to like post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sharePost(postId: Long): Result<Map<String, Any>> {
        return try {
            val response = postsApi.sharePost(postId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Failed to share post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun savePost(postId: Long): Result<Map<String, Any>> {
        return try {
            val response = postsApi.savePost(postId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyMap())
            } else {
                Result.failure(Exception("Failed to save post: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getComments(postId: Long, page: Int = 0, size: Int = 20): Result<CommentsResponse> {
        return try {
            val response = postsApi.getComments(postId, page, size)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch comments: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addComment(postId: Long, content: String): Result<PostCommentDto> {
        return try {
            val request = AddCommentRequest(content)
            val response = postsApi.addComment(postId, request)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to add comment: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSavedPosts(): Result<List<PostDto>> {
        return try {
            val response = postsApi.getSavedPosts()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch saved posts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserPosts(userId: Long): Result<List<PostDto>> {
        return try {
            val response = postsApi.getUserPosts(userId)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch user posts: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
