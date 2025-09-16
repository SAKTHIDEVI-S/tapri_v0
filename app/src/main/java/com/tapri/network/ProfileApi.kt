package com.tapri.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Body

interface ProfileApi {
    @GET("api/profile")
    suspend fun getProfile(): Response<UserDto>
    
    @PUT("api/profile")
    suspend fun updateProfile(@Body user: UserDto): Response<UserDto>
}
