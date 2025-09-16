package com.tapri.utils

import android.util.Log
import com.tapri.network.ApiClient
import com.tapri.network.AuthApi
import com.tapri.network.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object TokenRefreshHelper {
    
    suspend fun attemptTokenRefresh(sessionManager: SessionManager): Boolean {
        return try {
            val refreshToken = sessionManager.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                Log.w("TokenRefreshHelper", "No refresh token available")
                return false
            }
            
            Log.d("TokenRefreshHelper", "Attempting token refresh with refresh token: ${refreshToken.take(10)}...")
            
            // Create a temporary retrofit client without auth interceptor
            val tempClient = okhttp3.OkHttpClient.Builder()
                .connectTimeout(Config.getApiTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .readTimeout(Config.getApiTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .writeTimeout(Config.getApiTimeout(), java.util.concurrent.TimeUnit.MILLISECONDS)
                .build()
            
            val baseUrl = Config.getBaseUrl() + "auth/"
            Log.d("TokenRefreshHelper", "Using refresh endpoint: ${baseUrl}refresh")
            
            val tempRetrofit = retrofit2.Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(tempClient)
                .addConverterFactory(retrofit2.converter.scalars.ScalarsConverterFactory.create())
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            
            val authApi = tempRetrofit.create(AuthApi::class.java)
            
            Log.d("TokenRefreshHelper", "Making refresh token request...")
            val response = authApi.refreshToken(RefreshTokenRequest(refreshToken))
            Log.d("TokenRefreshHelper", "Refresh response received: code=${response.code()}, isSuccessful=${response.isSuccessful()}")
            
            if (response.isSuccessful) {
                val refreshResponse = response.body()
                if (refreshResponse != null) {
                    val newToken = refreshResponse.jwt
                    val newRefreshToken = refreshResponse.refreshToken
                    val expiresIn = refreshResponse.expiresIn
                    
                    if (!newToken.isNullOrEmpty()) {
                        // Calculate expiry time (default to 1 hour if not provided)
                        val expiryTime = if (expiresIn != null) {
                            System.currentTimeMillis() + (expiresIn * 1000)
                        } else {
                            System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour default
                        }
                        
                        // Save the new tokens
                        sessionManager.saveTokens(newToken, newRefreshToken, expiryTime)
                        
                        Log.d("TokenRefreshHelper", "Token refreshed successfully")
                        true
                    } else {
                        Log.e("TokenRefreshHelper", "Received empty JWT token")
                        false
                    }
                } else {
                    Log.e("TokenRefreshHelper", "Refresh response body is null")
                    false
                }
            } else {
                Log.e("TokenRefreshHelper", "Token refresh failed with code: ${response.code()}")
                val errorBody = response.errorBody()?.string()
                Log.e("TokenRefreshHelper", "Error body: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("TokenRefreshHelper", "Token refresh exception: ${e.message}")
            false
        }
    }
    
    fun refreshTokenAsync(
        sessionManager: SessionManager,
        coroutineScope: CoroutineScope,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) {
                attemptTokenRefresh(sessionManager)
            }
            
            withContext(Dispatchers.Main) {
                if (success) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
        }
    }
}
