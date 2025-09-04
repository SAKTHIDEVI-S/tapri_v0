package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import com.tapri.network.User
import com.tapri.utils.SessionManager

class SignUpActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    
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

        createAccountButton.setOnClickListener {
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
            } else {
                val mobile = mobileEditText.text.toString()
                val name = nameEditText.text.toString()
                val city = cityEditText.text.toString()
                val state = stateEditText.text.toString()
                
                if (mobile.isEmpty() || name.isEmpty()) {
                    Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                Log.d("SignUpActivity", "Creating account for mobile: $mobile")
                
                // Create user object for frontend-only version
                val user = User(
                    id = 1L,
                    mobile = mobile,
                    name = name,
                    city = city,
                    state = state,
                    rating = 4.5,
                    totalEarnings = 0.0,
                    profilePictureUrl = null,
                    lastLogin = null
                )
                
                // Save user session
                sessionManager.saveUserSession(user)
                
                Toast.makeText(this@SignUpActivity, "Account created successfully!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                finish()
            }
        }
    }
} 