package com.example.sakshi

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                Log.e("MyApplication", "Uncaught exception prevented: ${throwable.message}", throwable)
                Handler(Looper.getMainLooper()).post {
                    try {
                        Toast.makeText(
                            applicationContext,
                            "An unexpected error occurred. Please restart the app.",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (_: Exception) {
                        // best-effort only
                    }
                }
            } catch (_: Exception) {
                // swallow to avoid secondary crashes
            }
            // NOTE: do not call previousHandler here to avoid immediate process termination.
            // If you want system default behavior, uncomment the next line:
            // previousHandler?.uncaughtException(thread, throwable)
        }
    }
}

