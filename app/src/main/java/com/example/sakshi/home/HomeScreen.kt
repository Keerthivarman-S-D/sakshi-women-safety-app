package com.example.sakshi.home

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakshi.contacts.SharedContactsViewModel
import com.example.sakshi.sos.SosService
import com.example.sakshi.util.PermissionUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    sharedContactsVM: SharedContactsViewModel,
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToProfile: () -> Unit
) {
    val countdown by homeViewModel.countdown.collectAsState()
    val sosTriggered by homeViewModel.sosTriggered.collectAsState()
    val contacts by sharedContactsVM.contacts.collectAsState()
    val context = LocalContext.current

    // Install a lightweight uncaught exception handler to avoid app crash when third-party map code throws.
    // It shows a friendly toast and keeps the app running while restoring the previous handler on dispose.
    DisposableEffect(Unit) {
        val prevHandler = Thread.getDefaultUncaughtExceptionHandler()
        val handler = Thread.UncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("HomeScreen", "Uncaught exception prevented: ${throwable.message}", throwable)
                Toast.makeText(context, "An unexpected error occurred. Please try again.", Toast.LENGTH_LONG).show()
            } catch (_: Exception) {
                // best-effort only
            }
            // NOTE: do not delegate to prevHandler here to avoid process termination.
        }
        Thread.setDefaultUncaughtExceptionHandler(handler)

        onDispose {
            Thread.setDefaultUncaughtExceptionHandler(prevHandler)
        }
    }

    // Activity result launchers for permissions
    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            try {
                // If location just granted and SMS already present, start
                if (granted && PermissionUtils.hasSms(context)) {
                    homeViewModel.startCountdown()
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error in location permission callback: ${e.message}", e)
                Toast.makeText(context, "Unable to start SOS: permission check failed.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val smsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            try {
                // If SMS just granted and location already present, start
                if (granted && PermissionUtils.hasLocation(context)) {
                    homeViewModel.startCountdown()
                }
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error in SMS permission callback: ${e.message}", e)
                Toast.makeText(context, "Unable to start SOS: permission check failed.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun ensurePermissionsAndStart() {
        try {
            // check contacts
            if (contacts.isEmpty()) return

            // Check permissions first
            val hasLoc = try { PermissionUtils.hasLocation(context) } catch (e: Exception) {
                Log.e("HomeScreen", "hasLocation failed: ${e.message}", e)
                false
            }
            val hasSms = try { PermissionUtils.hasSms(context) } catch (e: Exception) {
                Log.e("HomeScreen", "hasSms failed: ${e.message}", e)
                false
            }

            if (hasLoc && hasSms) {
                homeViewModel.startCountdown()
                return
            }

            // Request missing permissions (order: location then SMS)
            if (!hasLoc) {
                try {
                    locationLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Failed to request location permission: ${e.message}", e)
                    Toast.makeText(context, "Unable to request location permission.", Toast.LENGTH_SHORT).show()
                }
            }

            if (!hasSms) {
                try {
                    smsLauncher.launch(android.Manifest.permission.SEND_SMS)
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Failed to request SMS permission: ${e.message}", e)
                    Toast.makeText(context, "Unable to request SMS permission.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "ensurePermissionsAndStart failed: ${e.message}", e)
            Toast.makeText(context, "An error occurred. Try again.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // 🚨 SOS BUTTON (tap or long-press)
        Button(
            onClick = {
                try {
                    ensurePermissionsAndStart()
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Button onClick failed: ${e.message}", e)
                    Toast.makeText(context, "An error occurred. Try again.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .size(220.dp)
                .combinedClickable(
                    onClick = { /* handled by Button onClick */ },
                    onLongClick = {
                        try {
                            ensurePermissionsAndStart()
                        } catch (e: Exception) {
                            Log.e("HomeScreen", "Button onLongClick failed: ${e.message}", e)
                            Toast.makeText(context, "An error occurred. Try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                ),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "SOS",
                fontSize = 40.sp,
                color = MaterialTheme.colorScheme.onError
            )
        }


        // ⏳ COUNTDOWN
        countdown?.let {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sending SOS in $it",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { homeViewModel.cancelCountdown() }
                ) {
                    Text("Cancel")
                }
            }
        }

        // ⚠️ NO CONTACT WARNING
        if (contacts.isEmpty()) {
            Text(
                text = "Add at least one emergency contact to use SOS",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            )
        }

        // 🚨 SOS TRIGGERED
        if (sosTriggered) {

            LaunchedEffect(Unit) {
                // Guard the trigger with try/catch to prevent crashes from bubbling up
                try {
                    homeViewModel.triggerSOS(
                        context = context,
                        contacts = contacts.map { it.phone },
                        uid = "demo_uid"
                    )
                } catch (e: Exception) {
                    // if something went wrong, reset the state so UI recovers
                    homeViewModel.resetSOS()
                }
            }

            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = {
                            val stopIntent =
                                Intent(context.applicationContext, SosService::class.java).apply {
                                    action = "STOP_SOS"
                                }
                            // Use stopService to avoid starting a new service just to send an action
                            try {
                                context.stopService(stopIntent)
                            } catch (_: Exception) {
                                // fallback: try to start the service with STOP action (older devices)
                                try {
                                    context.startService(stopIntent)
                                } catch (_: Exception) {
                                    // ignore
                                }
                            }

                            homeViewModel.resetSOS()
                        }
                    ) {
                        Text("OK")
                    }
                },
                title = { Text("SOS Activated") },
                text = {
                    Text("Emergency actions have been triggered.")
                }
            )
        }
    }
}
