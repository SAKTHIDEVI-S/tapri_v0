package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mobileEditText = findViewById<EditText>(R.id.mobileEditText)
        val sendOtpButton = findViewById<Button>(R.id.sendOtpButton)
        val signupText = findViewById<TextView>(R.id.signupText)

        // Set colored and clickable 'Sign up' text
        signupText.text = Html.fromHtml("Don't have an account? <font color='#D32F2F'><u>Sign up</u></font>")
        signupText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        sendOtpButton.setOnClickListener {
            // Mock: Always go to OTP screen
            startActivity(Intent(this, OtpActivity::class.java))
        }
    }
} 