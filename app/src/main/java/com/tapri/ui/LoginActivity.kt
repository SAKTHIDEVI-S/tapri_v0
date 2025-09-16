package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.databinding.ActivityLoginBinding
import com.tapri.network.ApiClient
import com.tapri.network.AuthApi
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authApi: AuthApi
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        authApi = ApiClient.authRetrofit(sessionManager).create(AuthApi::class.java)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.sendOtpButton.setOnClickListener {
            val phoneNumber = binding.mobileEditText.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sendOtp(phoneNumber)
            } else {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Navigate to signup screen
        binding.signupText.setOnClickListener {
            val phoneNumber = binding.mobileEditText.text.toString().trim()
            val intent = Intent(this, SignUpActivity::class.java)
            if (phoneNumber.isNotEmpty()) intent.putExtra("phone", phoneNumber)
            startActivity(intent)
        }
    }
    
    private fun sendOtp(phoneNumber: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = authApi.sendOtp(
                    com.tapri.network.OtpRequest(
                        phone = phoneNumber,
                        purpose = "login"
                    )
                )
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "OTP sent successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, OtpActivity::class.java)
                        intent.putExtra("phone", phoneNumber)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("LoginActivity", "Error: $errorBody")
                        if (errorBody?.contains("NEW_USER") == true) {
                            Toast.makeText(this@LoginActivity, "No account found. Please sign up first.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                            intent.putExtra("phone", phoneNumber)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@LoginActivity, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("LoginActivity", "Exception: ${e.message}")
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
