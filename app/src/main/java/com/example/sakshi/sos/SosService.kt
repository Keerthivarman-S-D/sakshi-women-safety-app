package com.example.sakshi.sos

import android.app.*
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.core.app.NotificationCompat
import com.example.sakshi.R
import com.example.sakshi.location.LocationProvider
import com.example.sakshi.util.PermissionUtils
import kotlinx.coroutines.*

class SosService : Service() {

    private val sosRepo = SosRepository()
    private var uid: String = "demo_uid" // later FirebaseAuth UID; updated from intent

    // Make nullable and defensive to avoid crashes if initialization fails
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    // coroutine scope for background work in service
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var locationJob: Job? = null

    override fun onCreate() {
        super.onCreate()

        // 🔊 Alarm sound - guard against exceptions
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.sos_alarm)?.apply {
                isLooping = true
                setVolume(1f, 1f)
                start()
            }
        } catch (e: Exception) {
            // swallow - in production log
            mediaPlayer = null
        }

        // 📳 Vibration - guard against missing service
        try {
            val v = getSystemService(VIBRATOR_SERVICE) as? Vibrator
            vibrator = v
            val pattern = longArrayOf(0, 1500, 500)

            vibrator?.let { vb ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        vb.vibrate(
                            VibrationEffect.createWaveform(pattern, 0)
                        )
                    } catch (e: Exception) {
                        // ignore
                    }
                } else {
                    @Suppress("DEPRECATION")
                    try {
                        vb.vibrate(pattern, 0)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
        } catch (e: Exception) {
            vibrator = null
        }

        // Create channel and start foreground - guard to avoid crash
        try {
            createNotificationChannel()
            startForeground(1, createNotification())
        } catch (e: Exception) {
            // If starting foreground fails, stop the service to avoid inconsistent state
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SOS") {
            stopSelf()
            return START_NOT_STICKY
        }

        // read uid from intent extras if provided
        intent?.getStringExtra("EXTRA_UID")?.let {
            uid = it
        }

        // Start collecting location updates (if not already started)
        startLocationUpdatesIfNeeded()

        return START_STICKY
    }

    private fun startLocationUpdatesIfNeeded() {
        if (locationJob != null) return

        // Guard permission check - HomeViewModel should ensure this, but double-check
        if (!PermissionUtils.hasLocation(applicationContext)) return

        val provider = LocationProvider(applicationContext)

        locationJob = serviceScope.launch {
            try {
                provider.locationUpdates().collect { loc ->
                    try {
                        val lat = loc.latitude
                        val lon = loc.longitude

                        // update live SOS location to Firebase
                        try {
                            sosRepo.updateLocation(uid, lat, lon)
                        } catch (e: Exception) {
                            // ignore/write logs in real app
                        }

                        // update notification with latest coords
                        try {
                            val notif = createNotification("Lat: %.5f, Lng: %.5f".format(lat, lon))
                            val nm = getSystemService(NotificationManager::class.java)
                            nm?.notify(1, notif)
                        } catch (e: Exception) {
                            // ignore
                        }
                    } catch (se: SecurityException) {
                        // Lost permission unexpectedly -> stop updating and stop service
                        stopLocationUpdates()
                        stopSelf()
                    }
                }
            } catch (se: SecurityException) {
                // permission issue - stop the service
                stopLocationUpdates()
                stopSelf()
            } catch (e: CancellationException) {
                // job cancelled - ignore
            } catch (e: Exception) {
                // other exceptions - ignore/log
            }
        }
    }

    private fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer?.run {
                try {
                    if (isPlaying) stop()
                } catch (_: Exception) {}
                try {
                    release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}

        try {
            vibrator?.cancel()
        } catch (_: Exception) {}

        // cancel coroutine scope and location updates
        try {
            stopLocationUpdates()
            serviceScope.cancel()
        } catch (_: Exception) {}

        mediaPlayer = null
        vibrator = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(contentText: String = "Emergency alarm is running"): Notification {

        val stopIntent = Intent(this, SosService::class.java).apply {
            action = "STOP_SOS"
        }

        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "sos_channel")
            .setContentTitle("SOS ACTIVE")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "STOP SOS",
                stopPendingIntent
            )
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sos_channel",
                "SOS Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            try {
                manager?.createNotificationChannel(channel)
            } catch (e: Exception) {
                // ignore
            }
        }
    }
}
