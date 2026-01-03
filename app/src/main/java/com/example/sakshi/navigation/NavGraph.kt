package com.example.sakshi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sakshi.auth.AppStartViewModel
import com.example.sakshi.auth.OtpScreen
import com.example.sakshi.auth.OtpViewModel
import com.example.sakshi.auth.StartDestination
import com.example.sakshi.contacts.ContactsScreen
import com.example.sakshi.contacts.SharedContactsViewModel
import com.example.sakshi.map.LiveSosMapScreen
import com.example.sakshi.map.MapScreen

@Composable
fun NavGraph(
    sharedContactsVM: SharedContactsViewModel,
    startVM: AppStartViewModel
) {
    val nav = rememberNavController()
    val start by startVM.start.collectAsState()

    LaunchedEffect(start) {
        when (start) {
            StartDestination.OTP -> nav.navigate("otp")
            StartDestination.CONTACTS -> nav.navigate("contacts")
            StartDestination.HOME -> nav.navigate("home")
            else -> {}
        }
    }

    NavHost(navController = nav, startDestination = "otp") {

        composable("otp") {
            val otpViewModel: OtpViewModel = viewModel()
            OtpScreen(
                onVerified = { startVM.decideStart() },
                viewModel = otpViewModel
            )
        }

        composable("contacts") {
            ContactsScreen(
                sharedVM = sharedContactsVM,
                navController = nav
            )
        }

        composable("home") {
            MainScaffold(sharedContactsVM)
        }

        composable("live_sos") {
            LiveSosMapScreen()
        }


        composable("map") {
            MapScreen(
                onNavigateBack = { nav.popBackStack() }
            )
        }
    }
}
