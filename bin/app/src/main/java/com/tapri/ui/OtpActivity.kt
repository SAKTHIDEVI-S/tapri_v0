package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class OtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val otpEditText = findViewById<EditText>(R.id.otpEditText)
        val verifyOtpButton = findViewById<Button>(R.id.verifyOtpButton)
        verifyOtpButton.setOnClickListener {
            // Mock: Always go to SignUp screen
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
} 