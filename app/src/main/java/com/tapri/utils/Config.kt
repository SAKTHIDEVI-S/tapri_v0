package com.tapri.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Configuration manager for Tapri app
 * Handles environment-specific settings and base URLs
 */
object Config {
    
    // Environment types
    enum class Environment {
        DEVELOPMENT,
        STAGING,
        PRODUCTION
    }
    
    // Current environment - change this for different builds
    private val currentEnvironment = Environment.DEVELOPMENT
    
    // Base URLs for different environments
    private const val DEV_BASE_URL = "http://192.168.1.6:8080/api/"
    private const val STAGING_BASE_URL = "https://staging-api.tapri.com/api/"
    private const val PROD_BASE_URL = "https://api.tapri.com/api/"
    
    // Media base URLs
    private const val DEV_MEDIA_BASE_URL = "http://192.168.1.6:8080"
    private const val STAGING_MEDIA_BASE_URL = "https://staging-api.tapri.com"
    private const val PROD_MEDIA_BASE_URL = "https://api.tapri.com"
    
    /**
     * Get the base API URL for the current environment
     */
    fun getBaseUrl(): String {
        return when (currentEnvironment) {
            Environment.DEVELOPMENT -> DEV_BASE_URL
            Environment.STAGING -> STAGING_BASE_URL
            Environment.PRODUCTION -> PROD_BASE_URL
        }
    }
    
    /**
     * Get the media base URL for the current environment
     */
    fun getMediaBaseUrl(): String {
        return when (currentEnvironment) {
            Environment.DEVELOPMENT -> DEV_MEDIA_BASE_URL
            Environment.STAGING -> STAGING_MEDIA_BASE_URL
            Environment.PRODUCTION -> PROD_MEDIA_BASE_URL
        }
    }
    
    /**
     * Convert relative media URL to absolute URL
     */
    fun getAbsoluteMediaUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty()) return null
        
        // If already absolute, return as is
        if (relativeUrl.startsWith("http")) return relativeUrl
        
        // If relative, prepend base URL
        val cleanUrl = if (relativeUrl.startsWith("/")) relativeUrl else "/$relativeUrl"
        return getMediaBaseUrl() + cleanUrl
    }
    
    /**
     * Convert relative media URL to streaming URL for videos
     */
    fun getStreamingMediaUrl(relativeUrl: String?): String? {
        if (relativeUrl.isNullOrEmpty()) {
            android.util.Log.d("Config", "getStreamingMediaUrl: URL is null or empty")
            return null
        }
        
        // If already absolute, return as is
        if (relativeUrl.startsWith("http")) {
            android.util.Log.d("Config", "getStreamingMediaUrl: Already absolute URL: $relativeUrl")
            return relativeUrl
        }
        
        // If relative, convert to streaming URL
        val cleanUrl = if (relativeUrl.startsWith("/")) relativeUrl else "/$relativeUrl"
        
        // Convert /api/images/posts/filename.mp4 to /api/images/stream/posts/filename.mp4
        val streamingUrl = cleanUrl.replace("/api/images/", "/api/images/stream/")
        val finalUrl = getMediaBaseUrl() + streamingUrl
        
        android.util.Log.d("Config", "getStreamingMediaUrl: Converting '$relativeUrl' -> '$cleanUrl' -> '$streamingUrl' -> '$finalUrl'")
        return finalUrl
    }
    
    /**
     * Get API timeout in milliseconds
     */
    fun getApiTimeout(): Long {
        return when (currentEnvironment) {
            Environment.DEVELOPMENT -> 30000L // 30 seconds for dev
            Environment.STAGING -> 15000L // 15 seconds for staging
            Environment.PRODUCTION -> 10000L // 10 seconds for production
        }
    }
    
    /**
     * Check if debug logging is enabled
     */
    fun isDebugLoggingEnabled(): Boolean {
        return currentEnvironment != Environment.PRODUCTION
    }
    
    /**
     * Get current environment name
     */
    fun getEnvironmentName(): String {
        return currentEnvironment.name
    }
    
    /**
     * Check if this is a production build
     */
    fun isProduction(): Boolean {
        return currentEnvironment == Environment.PRODUCTION
    }
    
    /**
     * Get file upload size limit in bytes
     */
    fun getMaxFileSize(): Long {
        return when (currentEnvironment) {
            Environment.DEVELOPMENT -> 50 * 1024 * 1024L // 50MB for dev
            Environment.STAGING -> 25 * 1024 * 1024L // 25MB for staging
            Environment.PRODUCTION -> 10 * 1024 * 1024L // 10MB for production
        }
    }
}
