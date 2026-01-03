package com.example.sakshi

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sakshi.auth.AppStartViewModel
import com.example.sakshi.contacts.SharedContactsViewModel
import com.example.sakshi.contacts.SharedContactsViewModelFactory
import com.example.sakshi.navigation.NavGraph
import com.example.sakshi.ui.theme.SakshiTheme
import android.preference.PreferenceManager
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {

    private val uid = "demo_uid" // later replace with FirebaseAuth UID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // ⚙️ Initialize osmdroid configuration (must be done before creating MapView)
        // This sets up tile caching and user agent so tile servers accept requests.
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        Configuration.getInstance().userAgentValue = packageName

        // 🔔 Required for SOS foreground service
        createNotificationChannel()

        setContent {
            SakshiTheme {

                val sharedContactsVM: SharedContactsViewModel =
                    viewModel(factory = SharedContactsViewModelFactory(uid))

                val startVM: AppStartViewModel = viewModel()

                LaunchedEffect(Unit) {
                    startVM.decideStart()
                }

                // Load saved contacts once when UI composes
                LaunchedEffect(Unit) {
                    sharedContactsVM.loadContacts()
                }

                NavGraph(
                    sharedContactsVM = sharedContactsVM,
                    startVM = startVM
                )
            }
        }
    }


    // 🔔 Notification channel for SOS
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "sos_channel",
                "SOS Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager =
                getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
