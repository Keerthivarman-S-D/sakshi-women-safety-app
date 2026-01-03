package com.example.sakshi.home

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sakshi.location.LocationProvider
import com.example.sakshi.sos.SosRepository
import com.example.sakshi.sos.SosService
import com.example.sakshi.sos.sendSosSms
import com.example.sakshi.util.PermissionUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class HomeViewModel : ViewModel() {

    private val sosRepo = SosRepository()

    private val _countdown = MutableStateFlow<Int?>(null)
    val countdown: StateFlow<Int?> = _countdown

    private val _sosTriggered = MutableStateFlow(false)
    val sosTriggered: StateFlow<Boolean> = _sosTriggered

    // ▶️ Start 3-second countdown
    fun startCountdown() {
        if (_countdown.value != null) return

        viewModelScope.launch {
            for (i in 3 downTo 1) {
                _countdown.value = i
                delay(1000)
            }
            _countdown.value = null
            _sosTriggered.value = true
        }
    }

    fun cancelCountdown() {
        _countdown.value = null
    }

    fun resetSOS() {
        _sosTriggered.value = false
    }

    fun triggerSOS(
        context: Context,
        contacts: List<String>,
        uid: String
    ) {
        viewModelScope.launch {
            // Ensure required permissions are still granted before proceeding
            if (!PermissionUtils.hasLocation(context) || !PermissionUtils.hasSms(context)) {
                // Drop the SOS trigger if permissions are not available
                _sosTriggered.value = false
                return@launch
            }

            val provider = LocationProvider(context)

            // Try to get a fresh location from locationUpdates() with a short timeout
            val fresh = withTimeoutOrNull(3000L) {
                provider.locationUpdates().firstOrNull()
            }

            val location = if (fresh != null) {
                // use the fresh update
                fresh
            } else {
                // fallback to last location
                provider.getLastLocation() ?: run {
                    // cannot obtain location; abort SOS
                    _sosTriggered.value = false
                    return@launch
                }
            }

            // Save SOS event
            sosRepo.createEvent(
                uid,
                location.latitude,
                location.longitude
            )

            // Send SMS (guarded)
            try {
                sendSosSms(
                    contacts,
                    location.latitude,
                    location.longitude
                )
            } catch (_: SecurityException) {
                // If SMS permission was lost unexpectedly, abort gracefully
                _sosTriggered.value = false
                return@launch
            } catch (_: Exception) {
                // swallow/send logs in real app
            }

            // Start foreground service safely (use startForegroundService on O+)
            val intent = Intent(context.applicationContext, SosService::class.java).apply {
                // pass uid to the service so it can update the same SOS node
                putExtra("EXTRA_UID", uid)
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context.applicationContext, intent)
                } else {
                    context.applicationContext.startService(intent)
                }
            } catch (_: Exception) {
                // if starting the service fails, reset the SOS state so UI recovers
                _sosTriggered.value = false
            }
        }
    }
}
