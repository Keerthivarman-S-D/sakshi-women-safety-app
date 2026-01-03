package com.example.sakshi.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sakshi.contacts.ContactsScreen
import com.example.sakshi.contacts.SharedContactsViewModel
import com.example.sakshi.home.HomeScreen
import com.example.sakshi.map.MapScreen
import com.example.sakshi.profile.ProfileScreen

@Composable
fun MainScaffold(sharedContactsVM: SharedContactsViewModel) {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            /*
             * Best Practice: Instead of passing the whole NavController,
             * we pass individual navigation actions as lambda functions.
             * This makes the HomeScreen composable unaware of the navigation logic,
             * making it more reusable and easier to test.
             */
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    sharedContactsVM = sharedContactsVM,
                    // Pass a lambda for any navigation action needed from the home screen.
                    // For example, if HomeScreen needs to navigate to the profile:
                    onNavigateToProfile = { navController.navigate(BottomNavItem.Profile.route) }
                )
            }

            composable(BottomNavItem.Map.route) {
                MapScreen(
                    // You can add navigation actions for MapScreen here if needed.
                    // For example, to go back:
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(BottomNavItem.Contacts.route) {
                ContactsScreen(
                    sharedVM = sharedContactsVM,
                    navController = navController
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}
