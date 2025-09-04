package com.tapri.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tapri.network.User

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("TapriPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
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
        editor.putLong(KEY_USER_ID, user.id ?: 0)
        editor.putString(KEY_USER_MOBILE, user.mobile)
        editor.putString(KEY_USER_NAME, user.name)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_CITY, user.city)
        editor.putString(KEY_USER_STATE, user.state)
        editor.putString(KEY_USER_PROFILE_PICTURE, user.profilePictureUrl)
        editor.putFloat(KEY_USER_RATING, user.rating?.toFloat() ?: 0f)
        editor.putInt(KEY_USER_TOTAL_RIDES, user.totalRides ?: 0)
        editor.putFloat(KEY_USER_TOTAL_EARNINGS, user.totalEarnings?.toFloat() ?: 0f)
        editor.putString(KEY_USER_VEHICLE_TYPE, user.vehicleType)
        editor.putString(KEY_USER_VEHICLE_NUMBER, user.vehicleNumber)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        
        // Also save complete user object as JSON
        editor.putString(KEY_USER_JSON, gson.toJson(user))
        editor.apply()
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
                // Fallback to individual fields if JSON parsing fails
                createUserFromPreferences()
            }
        } else {
            createUserFromPreferences()
        }
    }
    
    private fun createUserFromPreferences(): User {
        return User(
            id = sharedPreferences.getLong(KEY_USER_ID, 0),
            name = sharedPreferences.getString(KEY_USER_NAME, "") ?: "",
            mobile = sharedPreferences.getString(KEY_USER_MOBILE, "") ?: "",
            email = sharedPreferences.getString(KEY_USER_EMAIL, null),
            city = sharedPreferences.getString(KEY_USER_CITY, null),
            state = sharedPreferences.getString(KEY_USER_STATE, null),
            profilePictureUrl = sharedPreferences.getString(KEY_USER_PROFILE_PICTURE, null),
            rating = sharedPreferences.getFloat(KEY_USER_RATING, 0f).toDouble(),
            totalRides = sharedPreferences.getInt(KEY_USER_TOTAL_RIDES, 0),
            totalEarnings = sharedPreferences.getFloat(KEY_USER_TOTAL_EARNINGS, 0f).toDouble(),
            vehicleType = sharedPreferences.getString(KEY_USER_VEHICLE_TYPE, null),
            vehicleNumber = sharedPreferences.getString(KEY_USER_VEHICLE_NUMBER, null)
        )
    }
    
    fun updateUserProfile(user: User) {
        saveUserSession(user)
    }
    
    fun updateProfilePicture(profilePictureUrl: String) {
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_PROFILE_PICTURE, profilePictureUrl)
        editor.apply()
        
        // Update the JSON as well
        val currentUser = getUserSession()
        currentUser?.let {
            val updatedUser = it.copy(profilePictureUrl = profilePictureUrl)
            editor.putString(KEY_USER_JSON, gson.toJson(updatedUser))
            editor.apply()
        }
    }
    
    fun updateRating(rating: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat(KEY_USER_RATING, rating.toFloat())
        editor.apply()
        
        // Update the JSON as well
        val currentUser = getUserSession()
        currentUser?.let {
            val updatedUser = it.copy(rating = rating)
            editor.putString(KEY_USER_JSON, gson.toJson(updatedUser))
            editor.apply()
        }
    }
    
    fun updateEarnings(earnings: Double) {
        val currentEarnings = sharedPreferences.getFloat(KEY_USER_TOTAL_EARNINGS, 0f)
        val newEarnings = currentEarnings + earnings.toFloat()
        
        val editor = sharedPreferences.edit()
        editor.putFloat(KEY_USER_TOTAL_EARNINGS, newEarnings)
        editor.apply()
        
        // Update the JSON as well
        val currentUser = getUserSession()
        currentUser?.let {
            val updatedUser = it.copy(totalEarnings = newEarnings.toDouble())
            editor.putString(KEY_USER_JSON, gson.toJson(updatedUser))
            editor.apply()
        }
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
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
    }
}