package com.tapri.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tapri.network.User

class SessionManager(context: Context) {
	private val sharedPreferences: SharedPreferences = context.getSharedPreferences("TapriPrefs", Context.MODE_PRIVATE)
	private val gson = Gson()
	private val KEY_AUTH_TOKEN = "auth_token"
	private val KEY_REFRESH_TOKEN = "refresh_token"
	private val KEY_TOKEN_EXPIRY = "token_expiry"
	
	companion object {
		private const val KEY_USER_ID = "user_id"
		private const val KEY_USER_MOBILE = "user_mobile"
		private const val KEY_USER_NAME = "user_name"
		private const val KEY_USER_EMAIL = "user_email"
		private const val KEY_USER_CITY = "user_city"
		private const val KEY_USER_STATE = "user_state"
		private const val KEY_USER_PROFILE_PICTURE = "user_profile_picture"
		private const val KEY_USER_RATING = "user_rating"
		private const val KEY_USER_TOTAL_RIDES = "user_total_rides"
		private const val KEY_USER_TOTAL_EARNINGS = "user_total_earnings"
		private const val KEY_USER_VEHICLE_TYPE = "user_vehicle_type"
		private const val KEY_USER_VEHICLE_NUMBER = "user_vehicle_number"
		private const val KEY_IS_LOGGED_IN = "is_logged_in"
		private const val KEY_USER_JSON = "user_json"
	}
	
	fun saveUserSession(user: User) {
		val editor = sharedPreferences.edit()
		editor.putLong(KEY_USER_ID, user.id)
		editor.putString(KEY_USER_MOBILE, user.phone)
		editor.putString(KEY_USER_NAME, user.name)
		editor.putString(KEY_USER_CITY, user.city)
		editor.putBoolean(KEY_IS_LOGGED_IN, true)
		// Save minimal user JSON matching network.User
		editor.putString(KEY_USER_JSON, gson.toJson(user))
		editor.apply()
	}

	fun saveUser(name: String, phone: String, city: String?) {
		val userId = sharedPreferences.getLong(KEY_USER_ID, 0)
		val finalId = if (userId == 0L) System.currentTimeMillis() else userId
		
		val user = User(
			id = finalId,
			phone = phone,
			name = name,
			city = city
		)
		saveUserSession(user)
	}
	
	fun getUserSession(): User? {
		if (!isLoggedIn()) {
			return null
		}
		val userJson = sharedPreferences.getString(KEY_USER_JSON, null)
		return if (userJson != null) {
			try {
				gson.fromJson(userJson, User::class.java)
			} catch (e: Exception) {
				createUserFromPreferences()
			}
		} else {
			createUserFromPreferences()
		}
	}
	
	private fun createUserFromPreferences(): User {
		return User(
			id = sharedPreferences.getLong(KEY_USER_ID, 0),
			phone = sharedPreferences.getString(KEY_USER_MOBILE, "") ?: "",
			name = sharedPreferences.getString(KEY_USER_NAME, "") ?: "",
			city = sharedPreferences.getString(KEY_USER_CITY, null)
		)
	}
	
	fun updateUserProfile(user: User) {
		saveUserSession(user)
	}
	
	fun updateProfilePicture(profilePictureUrl: String) {
		sharedPreferences.edit().putString(KEY_USER_PROFILE_PICTURE, profilePictureUrl).apply()
	}
	
	fun updateRating(rating: Double) {
		sharedPreferences.edit().putFloat(KEY_USER_RATING, rating.toFloat()).apply()
	}
	
	fun updateEarnings(earnings: Double) {
		val currentEarnings = sharedPreferences.getFloat(KEY_USER_TOTAL_EARNINGS, 0f)
		val newEarnings = currentEarnings + earnings.toFloat()
		sharedPreferences.edit().putFloat(KEY_USER_TOTAL_EARNINGS, newEarnings).apply()
	}
	
	fun isLoggedIn(): Boolean {
		val hasToken = !getAuthToken().isNullOrEmpty()
		val notExpired = !isTokenExpired()
		val isLoggedInFlag = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
		
		// User is logged in only if they have a valid, non-expired token AND the flag is set
		return hasToken && notExpired && isLoggedInFlag
	}
	
	fun setLoggedIn(isLoggedIn: Boolean) {
		sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
	}
	
	
	fun getUserId(): Long {
		return sharedPreferences.getLong(KEY_USER_ID, 0)
	}
	
	fun getUserMobile(): String {
		return sharedPreferences.getString(KEY_USER_MOBILE, "") ?: ""
	}
	
	fun getUserName(): String {
		return sharedPreferences.getString(KEY_USER_NAME, "") ?: ""
	}
	
	fun getUserProfilePicture(): String? {
		return sharedPreferences.getString(KEY_USER_PROFILE_PICTURE, null)
	}
	
	fun getUserRating(): Double {
		return sharedPreferences.getFloat(KEY_USER_RATING, 0f).toDouble()
	}
	
	fun getUserTotalEarnings(): Double {
		return sharedPreferences.getFloat(KEY_USER_TOTAL_EARNINGS, 0f).toDouble()
	}
	
	fun clearSession() {
		val editor = sharedPreferences.edit()
		editor.clear()
		editor.apply()
		android.util.Log.d("SessionManager", "Session cleared - user logged out")
	}
	
	// JWT helpers
	fun saveAuthToken(token: String?) {
		val editor = sharedPreferences.edit()
		if (token.isNullOrEmpty()) {
			editor.remove(KEY_AUTH_TOKEN)
		} else {
			editor.putString(KEY_AUTH_TOKEN, token)
		}
		editor.apply()
	}
	
	fun getAuthToken(): String? {
		return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
	}
	
	// JWT Refresh functionality
	fun saveTokens(authToken: String?, refreshToken: String?, expiryTime: Long?) {
		val editor = sharedPreferences.edit()
		if (authToken.isNullOrEmpty()) {
			editor.remove(KEY_AUTH_TOKEN)
		} else {
			editor.putString(KEY_AUTH_TOKEN, authToken)
		}
		if (refreshToken.isNullOrEmpty()) {
			editor.remove(KEY_REFRESH_TOKEN)
		} else {
			editor.putString(KEY_REFRESH_TOKEN, refreshToken)
		}
		if (expiryTime != null) {
			editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
		}
		// Set logged in flag when saving tokens
		if (!authToken.isNullOrEmpty()) {
			editor.putBoolean(KEY_IS_LOGGED_IN, true)
		}
		editor.apply()
		android.util.Log.d("SessionManager", "Tokens saved - authToken: ${!authToken.isNullOrEmpty()}, refreshToken: ${!refreshToken.isNullOrEmpty()}")
	}
	
	fun getRefreshToken(): String? {
		return sharedPreferences.getString(KEY_REFRESH_TOKEN, null)
	}
	
	fun getTokenExpiry(): Long {
		return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0)
	}
	
	fun isTokenExpired(): Boolean {
		val expiryTime = getTokenExpiry()
		if (expiryTime == 0L) return true // No expiry time means expired
		return System.currentTimeMillis() >= expiryTime
	}
	
	fun isTokenExpiringSoon(): Boolean {
		val expiryTime = getTokenExpiry()
		if (expiryTime == 0L) return true
		// Consider token expiring if it expires within next 5 minutes
		return System.currentTimeMillis() >= (expiryTime - 5 * 60 * 1000)
	}
	
	fun clearTokens() {
		val editor = sharedPreferences.edit()
		editor.remove(KEY_AUTH_TOKEN)
		editor.remove(KEY_REFRESH_TOKEN)
		editor.remove(KEY_TOKEN_EXPIRY)
		editor.apply()
	}
	
	// Check if user needs to re-authenticate
	fun needsReauthentication(): Boolean {
		val token = getAuthToken()
		val refreshToken = getRefreshToken()
		
		// If no auth token, definitely need reauthentication
		if (token.isNullOrEmpty()) {
			android.util.Log.w("SessionManager", "No auth token found")
			return true
		}
		
		// If token is expired and no refresh token, need reauthentication
		if (isTokenExpired() && refreshToken.isNullOrEmpty()) {
			android.util.Log.w("SessionManager", "Token expired and no refresh token")
			return true
		}
		
		// If token is expired but we have refresh token, try refresh first
		if (isTokenExpired()) {
			android.util.Log.w("SessionManager", "Token expired but refresh token available")
			return true // Will trigger refresh attempt
		}
		
		// If token is expiring soon (within 5 minutes), try refresh proactively
		if (isTokenExpiringSoon()) {
			android.util.Log.w("SessionManager", "Token expiring soon, should refresh proactively")
			return true // Will trigger refresh attempt
		}
		
		return false
	}
	
	// Get token status for debugging
	fun getTokenStatus(): String {
		val token = getAuthToken()
		val refreshToken = getRefreshToken()
		val isExpired = isTokenExpired()
		val isExpiringSoon = isTokenExpiringSoon()
		
		return "Token: ${if (token.isNullOrEmpty()) "Missing" else "Present"}, " +
				"Refresh: ${if (refreshToken.isNullOrEmpty()) "Missing" else "Present"}, " +
				"Expired: $isExpired, Expiring Soon: $isExpiringSoon"
	}
	
	// Validate if tokens are properly set
	fun hasValidTokens(): Boolean {
		val hasAuthToken = !getAuthToken().isNullOrEmpty()
		val hasRefreshToken = !getRefreshToken().isNullOrEmpty()
		val authTokenNotExpired = !isTokenExpired()
		
		return hasAuthToken && hasRefreshToken && authTokenNotExpired
	}
	
	// Check if refresh is possible
	fun canRefreshToken(): Boolean {
		return !getRefreshToken().isNullOrEmpty()
	}
	
}