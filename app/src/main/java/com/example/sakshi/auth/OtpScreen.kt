package com.example.sakshi.auth

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OtpScreen(
    onVerified: () -> Unit,
    viewModel: OtpViewModel = viewModel()
) {
    val context = LocalContext.current
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var otpSent by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        if (!otpSent) {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone (+91XXXXXXXXXX)") }
            )

            Button(
                onClick = {
                    viewModel.sendOtp(
                        context as Activity,
                        phone,
                        onError = { error = it }
                    )
                    otpSent = true
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Send OTP")
            }
        } else {
            OutlinedTextField(
                value = otp,
                onValueChange = { otp = it },
                label = { Text("Enter OTP") }
            )

            Button(
                onClick = {
                    viewModel.verifyOtp(
                        otp,
                        onSuccess = onVerified,
                        onError = { error = it }
                    )
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Verify OTP")
            }
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
