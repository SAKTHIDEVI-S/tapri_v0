package com.tapri.service

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class FirebaseAuthService(private val context: Context) {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    
    interface AuthCallback {
        fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken)
        fun onVerificationCompleted(credential: PhoneAuthCredential)
        fun onVerificationFailed(exception: FirebaseException)
        fun onCodeSentError(message: String)
    }
    
    fun sendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callback: AuthCallback
    ) {
        Log.d("FirebaseAuthService", "Sending verification code for: $phoneNumber")
        Log.d("FirebaseAuthService", "Phone number length: ${phoneNumber.length}")
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("FirebaseAuthService", "Verification completed automatically")
                callback.onVerificationCompleted(credential)
            }
            
            override fun onVerificationFailed(exception: FirebaseException) {
                // Log the specific error for debugging
                Log.e("FirebaseAuthService", "Verification failed: ${exception.message}")
                Log.e("FirebaseAuthService", "Exception class: ${exception.javaClass.simpleName}")
                
                // Get error code from exception message
                val errorCode = when {
                    exception.message?.contains("INVALID_PHONE_NUMBER") == true -> "ERROR_INVALID_PHONE_NUMBER"
                    exception.message?.contains("TOO_MANY_REQUESTS") == true -> "ERROR_TOO_MANY_REQUESTS"
                    exception.message?.contains("QUOTA_EXCEEDED") == true -> "ERROR_QUOTA_EXCEEDED"
                    exception.message?.contains("APP_NOT_AUTHORIZED") == true -> "ERROR_APP_NOT_AUTHORIZED"
                    exception.message?.contains("INVALID_VERIFICATION_CODE") == true -> "ERROR_INVALID_VERIFICATION_CODE"
                    exception.message?.contains("INVALID_VERIFICATION_ID") == true -> "ERROR_INVALID_VERIFICATION_ID"
                    exception.message?.contains("SESSION_EXPIRED") == true -> "ERROR_SESSION_EXPIRED"
                    else -> "UNKNOWN_ERROR"
                }
                
                Log.e("FirebaseAuthService", "Error code: $errorCode")
                
                // Provide more specific error messages
                val errorMessage = when (errorCode) {
                    "ERROR_INVALID_PHONE_NUMBER" -> "Invalid phone number format"
                    "ERROR_TOO_MANY_REQUESTS" -> "Too many requests. Please try again later"
                    "ERROR_QUOTA_EXCEEDED" -> "SMS quota exceeded. Please try again later"
                    "ERROR_APP_NOT_AUTHORIZED" -> "App not authorized. Check Firebase configuration"
                    "ERROR_INVALID_VERIFICATION_CODE" -> "Invalid verification code"
                    "ERROR_INVALID_VERIFICATION_ID" -> "Invalid verification ID"
                    "ERROR_SESSION_EXPIRED" -> "Session expired. Please try again"
                    else -> "Verification failed: ${exception.message}"
                }
                
                Log.e("FirebaseAuthService", "Error message: $errorMessage")
                callback.onVerificationFailed(exception)
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("FirebaseAuthService", "Code sent successfully")
                Log.d("FirebaseAuthService", "Verification ID: $verificationId")
                this@FirebaseAuthService.verificationId = verificationId
                this@FirebaseAuthService.resendToken = token
                callback.onCodeSent(verificationId, token)
            }
        }
        
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        
        Log.d("FirebaseAuthService", "Starting phone verification...")
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    fun resendVerificationCode(
        phoneNumber: String,
        activity: Activity,
        callback: AuthCallback
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                callback.onVerificationCompleted(credential)
            }
            
            override fun onVerificationFailed(exception: FirebaseException) {
                callback.onVerificationFailed(exception)
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@FirebaseAuthService.verificationId = verificationId
                this@FirebaseAuthService.resendToken = token
                callback.onCodeSent(verificationId, token)
            }
        }
        
        resendToken?.let { token ->
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(token)
                .build()
            
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }
    
    fun verifyCode(
        code: String,
        callback: (Boolean, String?) -> Unit
    ) {
        Log.d("FirebaseAuthService", "Verifying code: $code")
        Log.d("FirebaseAuthService", "Verification ID: $verificationId")
        
        verificationId?.let { id ->
            Log.d("FirebaseAuthService", "Creating credential with verification ID: $id")
            val credential = PhoneAuthProvider.getCredential(id, code)
            
            Log.d("FirebaseAuthService", "Signing in with credential...")
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuthService", "Sign in successful")
                        val user = task.result?.user
                        val uid = user?.uid
                        Log.d("FirebaseAuthService", "User UID: $uid")
                        callback(true, uid)
                    } else {
                        Log.e("FirebaseAuthService", "Sign in failed", task.exception)
                        Log.e("FirebaseAuthService", "Error message: ${task.exception?.message}")
                        callback(false, task.exception?.message)
                    }
                }
        } ?: run {
            Log.e("FirebaseAuthService", "Verification ID not found")
            callback(false, "Verification ID not found")
        }
    }
    
    fun signOut() {
        auth.signOut()
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
} 