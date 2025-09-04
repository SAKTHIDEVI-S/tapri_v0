package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.AuthApi
import com.tapri.network.User
import com.tapri.service.FirebaseAuthService
import com.tapri.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpActivity : AppCompatActivity() {
    private lateinit var firebaseAuthService: FirebaseAuthService
    private lateinit var api: AuthApi
    private lateinit var sessionManager: SessionManager
    private var mobile: String = ""
    private var verificationId: String = "" // Store verification ID
    
    // OTP fields
    private lateinit var otpField1: EditText
    private lateinit var otpField2: EditText
    private lateinit var otpField3: EditText
    private lateinit var otpField4: EditText
    private lateinit var otpField5: EditText
    private lateinit var otpField6: EditText
    private lateinit var verifyOtpButton: Button
    private lateinit var otpSentMessage: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Initialize views
        otpField1 = findViewById(R.id.otpField1)
        otpField2 = findViewById(R.id.otpField2)
        otpField3 = findViewById(R.id.otpField3)
        otpField4 = findViewById(R.id.otpField4)
        otpField5 = findViewById(R.id.otpField5)
        otpField6 = findViewById(R.id.otpField6)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)
        otpSentMessage = findViewById(R.id.otpSentMessage)
        
        mobile = intent.getStringExtra("mobile") ?: ""
        verificationId = intent.getStringExtra("verificationId") ?: "" // Get verification ID

        firebaseAuthService = FirebaseAuthService(this)
        api = ApiClient.retrofit.create(AuthApi::class.java)
        sessionManager = SessionManager(this)

        Log.d("OtpActivity", "Received mobile: $mobile")
        Log.d("OtpActivity", "Received verification ID: $verificationId")

        // Set up OTP sent message
        otpSentMessage.text = "Sent OTP to +91 ${maskMobileNumber(mobile)}"

        // Set up OTP field listeners for auto-focus
        setupOtpFieldListeners()

        verifyOtpButton.setOnClickListener {
            val otp = getOtpFromFields()
            if (otp.length != 6) {
                Toast.makeText(this, "Enter valid 6-digit OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            Log.d("OtpActivity", "Verifying OTP: $otp for mobile: $mobile")
            Log.d("OtpActivity", "Using verification ID: $verificationId")
            
            // Use the verification ID directly instead of relying on FirebaseAuthService
            if (verificationId.isNotEmpty()) {
                verifyCodeDirectly(otp, verificationId)
            } else {
                // Fallback to FirebaseAuthService method
                firebaseAuthService.verifyCode(otp) { success, error ->
                    handleVerificationResult(success, error)
                }
            }
        }
    }
    
    private fun setupOtpFieldListeners() {
        val otpFields = listOf(otpField1, otpField2, otpField3, otpField4, otpField5, otpField6)
        
        for (i in otpFields.indices) {
            val currentField = otpFields[i]
            val nextField = if (i < otpFields.size - 1) otpFields[i + 1] else null
            
            currentField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && nextField != null) {
                        nextField.requestFocus()
                    }
                }
            })
        }
    }
    
    private fun getOtpFromFields(): String {
        return otpField1.text.toString() +
               otpField2.text.toString() +
               otpField3.text.toString() +
               otpField4.text.toString() +
               otpField5.text.toString() +
               otpField6.text.toString()
    }
    
    private fun maskMobileNumber(mobile: String): String {
        return if (mobile.length >= 10) {
            mobile.substring(0, 2) + "xxxxxxxx" + mobile.substring(mobile.length - 2)
        } else {
            mobile
        }
    }
    
    private fun verifyCodeDirectly(code: String, verificationId: String) {
        Log.d("OtpActivity", "Verifying code directly with verification ID: $verificationId")
        
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("OtpActivity", "Firebase OTP verification successful")
                    val firebaseUser = task.result?.user
                    val firebaseUid = firebaseUser?.uid
                    
                    if (firebaseUid != null) {
                        // Get user profile from backend
                        api.getUserProfileByMobile(mobile).enqueue(object : Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                if (response.isSuccessful) {
                                    val user = response.body()
                                    if (user != null) {
                                        // Save user session
                                        sessionManager.saveUserSession(user)
                                        
                                        // Update last login
                                        api.updateLastLogin(user.id ?: 0).enqueue(object : Callback<Map<String, String>> {
                                            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                                // Last login updated successfully
                                            }
                                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                                // Handle error silently
                                            }
                                        })
                                        
                                        val intent = Intent(this@OtpActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@OtpActivity, "User not found. Please sign up.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this@OtpActivity, "User not found. Please sign up.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            
                            override fun onFailure(call: Call<User>, t: Throwable) {
                                Log.e("OtpActivity", "Backend verification failed", t)
                                Toast.makeText(this@OtpActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                            }
                        })
                    } else {
                        Toast.makeText(this@OtpActivity, "Firebase user not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("OtpActivity", "Firebase OTP verification failed", task.exception)
                    Toast.makeText(this@OtpActivity, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    private fun handleVerificationResult(success: Boolean, error: String?) {
        if (success) {
            Log.d("OtpActivity", "Firebase OTP verification successful via service")
            // Get user profile from backend
            api.getUserProfileByMobile(mobile).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // Save user session
                            sessionManager.saveUserSession(user)
                            
                            // Update last login
                            api.updateLastLogin(user.id ?: 0).enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                                    // Last login updated successfully
                                }
                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    // Handle error silently
                                }
                            })
                            
                            val intent = Intent(this@OtpActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@OtpActivity, "User not found. Please sign up.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@OtpActivity, "User not found. Please sign up.", Toast.LENGTH_SHORT).show()
                    }
                }
                
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("OtpActivity", "Backend verification failed", t)
                    Toast.makeText(this@OtpActivity, "Network error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        } else {
            Log.e("OtpActivity", "Firebase OTP verification failed: $error")
            Toast.makeText(this@OtpActivity, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }
} 