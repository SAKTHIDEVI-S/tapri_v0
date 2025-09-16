package com.tapri.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.tapri.databinding.ActivityOtpBinding
import com.tapri.network.ApiClient
import com.tapri.network.AuthApi
import com.tapri.utils.SessionManager
import com.tapri.ui.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var authApi: AuthApi
    private lateinit var sessionManager: SessionManager
    private var phoneNumber: String = ""
    private val STORAGE_PERMISSION_REQUEST = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        authApi = ApiClient.authRetrofit(sessionManager).create(AuthApi::class.java)
        
        phoneNumber = intent.getStringExtra("phone") ?: ""
        
        setupOtpInputs()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.verifyOtpButton.setOnClickListener {
            val otp = buildOtpFromFields()
            if (otp.isNotEmpty()) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Removed test button - no more demo mode
    }
    
    // Removed testDirectNavigation - no more demo mode

    private fun setupOtpInputs() {
        val fields = arrayOf(
            binding.otpField1,
            binding.otpField2,
            binding.otpField3,
            binding.otpField4,
            binding.otpField5,
            binding.otpField6
        )

        // Focus first field
        fields[0].requestFocus()

        for (i in fields.indices) {
            val current = fields[i]
            val next = if (i < fields.lastIndex) fields[i + 1] else null
            val prev = if (i > 0) fields[i - 1] else null

            // Move forward on input
            current.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    if ((s?.length ?: 0) == 1) {
                        if (next != null) {
                            next.requestFocus()
                        } else {
                            // Last field; hide keyboard
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(current.windowToken, 0)
                        }
                    }
                }
            })

            // Move back on delete when empty
            current.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (current.text.isNullOrEmpty() && prev != null) {
                        prev.requestFocus()
                        prev.setSelection(prev.text?.length ?: 0)
                        return@setOnKeyListener true
                    }
                }
                false
            }

            // Select all on focus for quick correction
            current.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus && v is EditText) v.selectAll()
            }
        }
    }

    private fun buildOtpFromFields(): String {
        val d1 = binding.otpField1.text?.toString()?.trim() ?: ""
        val d2 = binding.otpField2.text?.toString()?.trim() ?: ""
        val d3 = binding.otpField3.text?.toString()?.trim() ?: ""
        val d4 = binding.otpField4.text?.toString()?.trim() ?: ""
        val d5 = binding.otpField5.text?.toString()?.trim() ?: ""
        val d6 = binding.otpField6.text?.toString()?.trim() ?: ""
        return (d1 + d2 + d3 + d4 + d5 + d6).trim()
    }
    
    private fun verifyOtp(otp: String) {
        Log.d("OtpActivity", "Verifying OTP: $otp for phone: $phoneNumber")
        
        // Show loading state
        binding.verifyOtpButton.isEnabled = false
        binding.verifyOtpButton.text = "Verifying..."
        
        // Make real API call to verify OTP
        lifecycleScope.launch {
            try {
                val authApi = ApiClient.authRetrofit(sessionManager).create(AuthApi::class.java)
                val request = mapOf(
                    "phone" to phoneNumber,
                    "code" to otp
                )
                
                Log.d("OtpActivity", "Making API call to verify OTP")
                val response = authApi.verifyOtpRaw(request)
                
                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("OtpActivity", "OTP verification response: $result")
                    
                    if (result != null) {
                        // Check if user needs to complete signup
                        val needsSignup = result["needsSignup"] as? Boolean
                        if (needsSignup == true) {
                            Log.d("OtpActivity", "User needs to complete signup")
                            // For demo, auto-complete signup with default data
                            val tempToken = result["tempToken"] as? String ?: ""
                            launch {
                                completeSignup(tempToken)
                            }
                        } else {
                            // User exists, save JWT and user data
                            Log.d("OtpActivity", "Existing user, saving JWT")
                            val jwt = result["jwt"] as? String
                            val refreshToken = result["refreshToken"] as? String
                            val expiresIn = result["expiresIn"] as? Number
                            
                            if (jwt != null) {
                                // Calculate expiry time (default to 1 hour if not provided)
                                val expiryTime = if (expiresIn != null) {
                                    System.currentTimeMillis() + (expiresIn.toLong() * 1000)
                                } else {
                                    System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour default
                                }
                                
                                sessionManager.saveTokens(jwt, refreshToken, expiryTime)
                                Log.d("OtpActivity", "JWT saved: $jwt, refresh token: $refreshToken, expires: $expiryTime")
                            }
                            
                            val userMap = result["user"] as? Map<String, Any>
                            if (userMap != null) {
                                val name = userMap["name"] as? String ?: "User"
                                val phone = userMap["phone"] as? String ?: phoneNumber
                                val city = userMap["city"] as? String
                                
                                sessionManager.saveUser(name, phone, city)
                                Log.d("OtpActivity", "User data saved: $name")
                            }
                            
                            // User is now logged in after successful OTP verification
                            navigateToHome()
                            Toast.makeText(this@OtpActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        throw Exception("Empty response from server")
                    }
                   } else {
                       Log.e("OtpActivity", "OTP verification failed: ${response.code()}")
                       val errorBody = response.errorBody()?.string()
                       Log.e("OtpActivity", "Error body: $errorBody")
                       
                       Toast.makeText(this@OtpActivity, "OTP verification failed. Please try again.", Toast.LENGTH_SHORT).show()
                   }
                
               } catch (e: Exception) {
                   Log.e("OtpActivity", "Error verifying OTP: ${e.message}", e)
                   Toast.makeText(this@OtpActivity, "Network error. Please check your connection and try again.", Toast.LENGTH_SHORT).show()
               } finally {
                // Reset button state
                binding.verifyOtpButton.isEnabled = true
                binding.verifyOtpButton.text = "Verify OTP"
            }
        }
    }
    
    private suspend fun completeSignup(tempToken: String) {
        try {
            val authApi = ApiClient.authRetrofit(sessionManager).create(AuthApi::class.java)
            val request = mapOf(
                "name" to "New User",
                "city" to "Unknown"
            )
            
            Log.d("OtpActivity", "Completing signup with temp token")
            // Use raw response for signup completion as well
            val response = authApi.completeSignupRaw(tempToken, request)
            
            if (response.isSuccessful) {
                val result = response.body()
                Log.d("OtpActivity", "Signup completion response: $result")
                
                if (result != null) {
                    val jwt = result["jwt"] as? String
                    val refreshToken = result["refreshToken"] as? String
                    val expiresIn = result["expiresIn"] as? Number
                    
                    if (jwt != null) {
                        // Calculate expiry time (default to 1 hour if not provided)
                        val expiryTime = if (expiresIn != null) {
                            System.currentTimeMillis() + (expiresIn.toLong() * 1000)
                        } else {
                            System.currentTimeMillis() + (60 * 60 * 1000) // 1 hour default
                        }
                        
                        sessionManager.saveTokens(jwt, refreshToken, expiryTime)
                        Log.d("OtpActivity", "JWT saved after signup: $jwt, refresh token: $refreshToken, expires: $expiryTime")
                    }
                    
                    val userMap = result["user"] as? Map<String, Any>
                    if (userMap != null) {
                        val name = userMap["name"] as? String ?: "Demo User"
                        val phone = userMap["phone"] as? String ?: phoneNumber
                        val city = userMap["city"] as? String
                        
                        sessionManager.saveUser(name, phone, city)
                        Log.d("OtpActivity", "User data saved after signup: $name")
                    }
                    
                    // User is now logged in after successful OTP verification
                    navigateToHome()
                    Toast.makeText(this, "Signup completed! Login successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("OtpActivity", "Empty signup response")
                    Toast.makeText(this@OtpActivity, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("OtpActivity", "Signup completion failed: ${response.code()}")
                Toast.makeText(this@OtpActivity, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("OtpActivity", "Error completing signup: ${e.message}", e)
            Toast.makeText(this@OtpActivity, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Removed saveDemoData - no more demo mode
    
    private fun requestStoragePermissionAfterLogin() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) 
            != PackageManager.PERMISSION_GRANTED) {
            showStoragePermissionDialog()
        } else {
            // Permission already granted, proceed to home
            navigateToHome()
        }
    }
    
    private fun showStoragePermissionDialog() {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Storage Permission Required")
            .setMessage("Tapri needs access to your device storage to let you upload photos and videos in your posts. This helps you share your experiences with the community.\n\nTap 'Allow' to grant permission directly.")
            .setPositiveButton("Allow") { _, _ ->
                // Request permission directly
                ActivityCompat.requestPermissions(this, 
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 
                    STORAGE_PERMISSION_REQUEST)
            }
            .setNegativeButton("Skip") { _, _ ->
                Toast.makeText(this, "You can grant this permission later in settings", Toast.LENGTH_LONG).show()
                navigateToHome()
            }
            .setCancelable(false)
            .create()
        
        dialog.show()
    }
    
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            STORAGE_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Storage permission denied. You can grant it later in settings.", Toast.LENGTH_LONG).show()
                }
                // Navigate to home regardless of permission result
                navigateToHome()
            }
        }
    }
    
    private fun convertMapToUser(userMap: Map<String, Any>): com.tapri.network.User {
        return com.tapri.network.User(
            id = (userMap["id"] as? Number)?.toLong() ?: 0L,
            phone = userMap["phone"] as? String ?: "",
            name = userMap["name"] as? String ?: "",
            city = userMap["city"] as? String,
            state = userMap["state"] as? String,
            bio = userMap["bio"] as? String,
            profilePhotoUrl = userMap["profilePhotoUrl"] as? String,
            profilePicture = userMap["profilePicture"] as? String,
            lastSeen = userMap["lastSeen"] as? String,
            lastLogin = userMap["lastLogin"] as? String,
            lastSeenVisible = userMap["lastSeenVisible"] as? Boolean,
            rating = (userMap["rating"] as? Number)?.toDouble(),
            earnings = (userMap["earnings"] as? Number)?.toDouble(),
            isActive = userMap["isActive"] as? Boolean,
            createdAt = userMap["createdAt"] as? String,
            updatedAt = userMap["updatedAt"] as? String
        )
    }
    
    private fun navigateToHome() {
        try {
            Log.d("OtpActivity", "Navigating to HomeActivity")
            
            // Navigate to home activity
            val intent = Intent(this@OtpActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Log.d("OtpActivity", "Successfully started HomeActivity")
            finish()
        } catch (e: Exception) {
            Log.e("OtpActivity", "Error navigating to HomeActivity: ${e.message}", e)
            Toast.makeText(this@OtpActivity, "Login successful! Please restart the app.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
