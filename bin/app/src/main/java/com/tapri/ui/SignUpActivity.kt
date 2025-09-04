package com.tapri.ui

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val mobileEditText = findViewById<EditText>(R.id.signupMobileEditText)
        val nameEditText = findViewById<EditText>(R.id.signupNameEditText)
        val cityEditText = findViewById<EditText>(R.id.signupCityEditText)
        val stateEditText = findViewById<EditText>(R.id.signupStateEditText)
        val termsCheckBox = findViewById<CheckBox>(R.id.termsCheckBox)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            if (!termsCheckBox.isChecked) {
                Toast.makeText(this, "Please agree to the Terms and Conditions", Toast.LENGTH_SHORT).show()
            } else {
                // Mock: Show success
                Toast.makeText(this, "Account created! (Home not implemented)", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 