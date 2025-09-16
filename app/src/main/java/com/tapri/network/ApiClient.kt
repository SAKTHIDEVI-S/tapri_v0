package com.tapri.network

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type
import com.tapri.utils.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody

object ApiClient {
    // Use Config for environment-specific URLs
    private fun getBaseUrl() = com.tapri.utils.Config.getBaseUrl()
    private fun getAuthBaseUrl() = "${getBaseUrl()}auth/"
    private fun getEarnBaseUrl() = "${getBaseUrl()}earn/"
    private fun getPostsBaseUrl() = "${getBaseUrl()}posts/"
    private fun getGroupsBaseUrl() = "${getBaseUrl()}groups/"
    private fun getChatBaseUrl() = "${getBaseUrl()}chat/"
    private fun getProfileBaseUrl() = "${getBaseUrl()}profile/"
    private fun getImagesBaseUrl() = "${getBaseUrl()}images/"
    
    private val gson = GsonBuilder()
        .setLenient() // This enables lenient JSON parsing
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
    }
    
    private val responseInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        
        // Log the response details
        android.util.Log.d("API_RESPONSE", "URL: ${request.url}")
        android.util.Log.d("API_RESPONSE", "Status: ${response.code}")
        android.util.Log.d("API_RESPONSE", "Content-Type: ${response.header("Content-Type")}")
        
        val contentType = response.header("Content-Type") ?: ""
        if (!contentType.contains("application/json")) {
            val responseBody = response.body?.string()
            android.util.Log.e("API_RESPONSE", "Non-JSON response: $responseBody")
            val mediaType = response.body?.contentType() ?: "text/plain".toMediaTypeOrNull()
            return@Interceptor response.newBuilder()
                .body((responseBody ?: "").toResponseBody(mediaType))
                .build()
        }
        
        response
    }

    private fun authInterceptor(sessionManager: SessionManager) = Interceptor { chain ->
        val original = chain.request()
        val token = sessionManager.getAuthToken()
        
        // Debug logging
        android.util.Log.d("ApiClient", "Auth interceptor - Token: $token")
        android.util.Log.d("ApiClient", "Request URL: ${original.url}")
        
        val builder = original.newBuilder()
        if (!token.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")
            android.util.Log.d("ApiClient", "Added Authorization header: Bearer $token")
        } else {
            android.util.Log.w("ApiClient", "No auth token available")
        }
        
        val newRequest = builder.build()
        android.util.Log.d("ApiClient", "Final request headers: ${newRequest.headers}")
        
        chain.proceed(newRequest)
    }
    
    private fun buildClient(sessionManager: SessionManager): OkHttpClient {
        val timeout = com.tapri.utils.Config.getApiTimeout()
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor(sessionManager))
            .addInterceptor(responseInterceptor)
            .connectTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .readTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .writeTimeout(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)
            .build()
    }

    fun authRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getAuthBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create()) // Handle plain text responses
            .addConverterFactory(GsonConverterFactory.create(gson)) // Handle JSON responses
            .build()
    }

    fun earnRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getEarnBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun postsRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getPostsBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun groupsRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getGroupsBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun chatRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getChatBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun profileRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getProfileBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    fun imageRetrofit(sessionManager: SessionManager): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getImagesBaseUrl())
            .client(buildClient(sessionManager))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
} 