package com.tapri.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.AuthApi
import com.tapri.network.DirectSignupRequest
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var authApi: AuthApi
    private val STORAGE_PERMISSION_REQUEST = 1001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val mobileEditText = findViewById<EditText>(R.id.signupMobileEditText)
        val nameEditText = findViewById<EditText>(R.id.signupNameEditText)
        val cityEditText = findViewById<EditText>(R.id.signupCityEditText)
        val stateEditText = findViewById<EditText>(R.id.signupStateEditText)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)

        sessionManager = SessionManager(this)
        authApi = ApiClient.authRetrofit(sessionManager).create(AuthApi::class.java)

        val passedPhone = intent.getStringExtra("phone") ?: ""
        mobileEditText.setText(passedPhone)

        createAccountButton.setOnClickListener {
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
            } else {
                val phone = mobileEditText.text.toString().trim()
                val name = nameEditText.text.toString().trim()
                val city = cityEditText.text.toString().trim().ifEmpty { null }
                val state = stateEditText.text.toString().trim().ifEmpty { null }
                
                if (phone.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val resp = authApi.directSignup(
                            DirectSignupRequest(name = name, phone = phone, city = city, state = state)
                        )
                        withContext(Dispatchers.Main) {
                            if (resp.isSuccessful) {
                                val body = resp.body()
                                if (body != null) {
                                    sessionManager.saveAuthToken(body.jwt)
                                    sessionManager.saveUserSession(body.user)
                                    Toast.makeText(this@SignUpActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                    // Request storage permission after successful signup
                                    requestStoragePermissionAfterSignup()
                                }
                            } else {
                                val err = resp.errorBody()?.string() ?: ""
                                Log.e("SignUpActivity", "Error: $err")
                                if (err.contains("ALREADY_EXISTS")) {
                                    Toast.makeText(this@SignUpActivity, "Account already exists. Please login.", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                                    intent.putExtra("phone", phone)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@SignUpActivity, "Signup failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("SignUpActivity", "Exception: ${e.message}")
                            Toast.makeText(this@SignUpActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun requestStoragePermissionAfterSignup() {
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
    
    private fun navigateToHome() {
        val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
} 