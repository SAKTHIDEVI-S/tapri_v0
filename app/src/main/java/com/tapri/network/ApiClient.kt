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

object ApiClient {
    private const val BASE_URL = "http://172.20.10.4:8080/api/auth/"
    
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
        
        // If response is not JSON, log the body for debugging
        val contentType = response.header("Content-Type") ?: ""
        if (!contentType.contains("application/json")) {
            val responseBody = response.body?.string()
            android.util.Log.e("API_RESPONSE", "Non-JSON response: $responseBody")
            // Recreate response since we consumed the body
            return@Interceptor response.newBuilder()
                .body(okhttp3.ResponseBody.create(response.body?.contentType(), responseBody ?: ""))
                .build()
        }
        
        response
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(responseInterceptor)
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create()) // Handle plain text responses
        .addConverterFactory(GsonConverterFactory.create(gson)) // Handle JSON responses
        .build()
} 