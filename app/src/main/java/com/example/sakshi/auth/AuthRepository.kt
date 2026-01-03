package com.example.sakshi.auth

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    // ✅ CHECK LOGIN
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun uid(): String {
        return auth.currentUser?.uid ?: ""
    }

    // ✅ SEND OTP (with backdoor)
    fun sendOtp(
        activity: Activity,
        phone: String,
        onCodeSent: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // FOR TESTING: bypass OTP for any number
        onCodeSent("dummy_verification_id")
    }

    // ✅ VERIFY OTP (with backdoor)
    fun verifyOtp(
        verificationId: String,
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // FOR TESTING: accept 123456 for any number
        if (otp == "123456") {
            auth.signInAnonymously()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError("Anonymous sign-in failed") }
        } else {
            onError("Invalid OTP")
        }
    }
}
