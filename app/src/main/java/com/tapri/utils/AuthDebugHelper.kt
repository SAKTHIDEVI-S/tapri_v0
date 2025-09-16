package com.tapri.utils

import android.util.Log
import com.tapri.network.AuthApi
import com.tapri.network.RefreshTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object AuthDebugHelper {
    
    fun testRefreshEndpoint(sessionManager: SessionManager, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val refreshToken = sessionManager.getRefreshToken()
            if (refreshToken.isNullOrEmpty()) {
                Log.e("AuthDebugHelper", "No refresh token available for testing")
                return@launch
            }
            
            Log.d("AuthDebugHelper", "Testing refresh endpoint...")
            
            try {
                val tempClient = OkHttpClient.Builder()
                    .connectTimeout(Config.getApiTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(Config.getApiTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(Config.getApiTimeout(), TimeUnit.MILLISECONDS)
                    .build()
                
                val baseUrl = Config.getBaseUrl() + "auth/"
                Log.d("AuthDebugHelper", "Testing endpoint: ${baseUrl}refresh")
                
                val tempRetrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(tempClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                
                val authApi = tempRetrofit.create(AuthApi::class.java)
                
                val response = withContext(Dispatchers.IO) {
                    authApi.refreshToken(RefreshTokenRequest(refreshToken))
                }
                
                Log.d("AuthDebugHelper", "Refresh test response: code=${response.code()}, isSuccessful=${response.isSuccessful()}")
                
                if (response.isSuccessful) {
                    val refreshResponse = response.body()
                    Log.d("AuthDebugHelper", "Refresh test successful: jwt=${!refreshResponse?.jwt.isNullOrEmpty()}, refreshToken=${!refreshResponse?.refreshToken.isNullOrEmpty()}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthDebugHelper", "Refresh test failed: code=${response.code()}, error=$errorBody")
                }
                
            } catch (e: Exception) {
                Log.e("AuthDebugHelper", "Refresh test exception: ${e.message}", e)
            }
        }
    }
    
    fun logTokenStatus(sessionManager: SessionManager) {
        Log.d("AuthDebugHelper", "=== TOKEN STATUS ===")
        Log.d("AuthDebugHelper", sessionManager.getTokenStatus())
        Log.d("AuthDebugHelper", "Has valid tokens: ${sessionManager.hasValidTokens()}")
        Log.d("AuthDebugHelper", "Can refresh: ${sessionManager.canRefreshToken()}")
        Log.d("AuthDebugHelper", "Needs reauth: ${sessionManager.needsReauthentication()}")
        Log.d("AuthDebugHelper", "==================")
    }
}
