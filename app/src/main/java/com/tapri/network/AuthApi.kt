package com.tapri.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("otp")
    suspend fun sendOtp(@Body request: OtpRequest): Response<OtpResponse>
    
    @POST("verify")
    suspend fun verifyOtp(@Body request: VerifyRequest): Response<VerifyResponse>
    
    @POST("verify")
    suspend fun verifyOtpRaw(@Body request: Map<String, String>): Response<Map<String, Any>>
    
    @POST("signup/complete")
    suspend fun completeSignup(
        @Header("Temp-Token") tempToken: String,
        @Body request: SignupRequest
    ): Response<SignupResponse>
    
    @POST("signup/complete")
    suspend fun completeSignupRaw(
        @Header("Temp-Token") tempToken: String,
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    // New: direct signup without OTP
    @POST("signup")
    suspend fun directSignup(@Body request: DirectSignupRequest): Response<SignupResponse>
    
    @POST("signup")
    suspend fun directSignupRaw(@Body request: Map<String, String>): Response<Map<String, Any>>
    
    // Token refresh endpoint
    @POST("refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
}

data class OtpRequest(
    val phone: String,
    val purpose: String // "signup" or "login"
)

data class OtpResponse(
    val message: String
)

data class VerifyRequest(
    val phone: String,
    val code: String
)

data class VerifyResponse(
    val needsSignup: Boolean? = null,
    val tempToken: String? = null,
    val jwt: String? = null,
    val user: User? = null
)

data class SignupRequest(
    val name: String,
    val city: String? = null
)

data class SignupResponse(
    val jwt: String,
    val user: User
)

// New: direct signup request

data class DirectSignupRequest(
    val name: String,
    val phone: String,
    val city: String? = null,
    val state: String? = null
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val jwt: String,
    val refreshToken: String? = null,
    val expiresIn: Long? = null
)

data class User(
    val id: Long,
    val phone: String,
    val name: String,
    val city: String? = null,
    val state: String? = null,
    val bio: String? = null,
    val profilePhotoUrl: String? = null,
    val profilePicture: String? = null,
    val lastSeen: String? = null,
    val lastLogin: String? = null,
    val lastSeenVisible: Boolean? = null,
    val rating: Double? = null,
    val earnings: Double? = null,
    val isActive: Boolean? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)
