package com.tapri.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

// Data class for user - moved outside interface for accessibility
data class User(
    val id: Long? = null,
    val name: String,
    val mobile: String,
    val city: String? = null,
    val state: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val rating: Double? = null,
    val totalRides: Int? = null,
    val totalEarnings: Double? = null,
    val vehicleType: String? = null,
    val vehicleNumber: String? = null,
    val createdAt: String? = null,
    val lastLogin: String? = null,
    val isVerified: Boolean = false,
    val referralCode: String? = null,
    val referredBy: String? = null
)

interface AuthApi {
    @POST("signup")
    fun signup(@Body user: User): Call<User>
    
    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    
    @POST("send-otp")
    fun sendOtp(@Body otpRequest: OtpRequest): Call<OtpResponse>
    
    @POST("verify-otp")
    fun verifyOtp(@Body verifyRequest: VerifyRequest): Call<VerifyResponse>
    
    // User profile endpoints
    @GET("users/profile/{userId}")
    fun getUserProfile(@Path("userId") userId: Long): Call<User>
    
    @GET("users/profile/mobile/{mobile}")
    fun getUserProfileByMobile(@Path("mobile") mobile: String): Call<User>
    
    @PUT("users/profile/{userId}")
    fun updateUserProfile(@Path("userId") userId: Long, @Body user: User): Call<User>
    
    @Multipart
    @POST("users/profile-picture/{userId}")
    fun uploadProfilePicture(
        @Path("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): Call<Map<String, String>>
    
    @POST("users/login/{userId}")
    fun updateLastLogin(@Path("userId") userId: Long): Call<Map<String, String>>
    
    @POST("users/rating/{userId}")
    fun updateRating(@Path("userId") userId: Long, @Body request: Map<String, Double>): Call<Map<String, String>>
    
    @POST("users/earnings/{userId}")
    fun updateEarnings(@Path("userId") userId: Long, @Body request: Map<String, Double>): Call<Map<String, String>>
}

data class LoginRequest(
    val mobile: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class OtpRequest(
    val mobile: String
)

data class OtpResponse(
    val success: Boolean,
    val message: String
)

data class VerifyRequest(
    val mobile: String,
    val otp: String
)

data class VerifyResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
) 