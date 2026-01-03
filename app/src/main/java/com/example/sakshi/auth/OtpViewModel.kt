package com.example.sakshi.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OtpViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _verificationId = MutableStateFlow("")
    val verificationId: StateFlow<String> = _verificationId

    fun sendOtp(
        activity: Activity,
        phone: String,
        onError: (String) -> Unit
    ) {
        repo.sendOtp(
            activity,
            phone,
            onCodeSent = { id ->
                _verificationId.value = id
            },
            onError = onError
        )
    }

    fun verifyOtp(
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        repo.verifyOtp(
            _verificationId.value,
            otp,
            onSuccess,
            onError
        )
    }
}
