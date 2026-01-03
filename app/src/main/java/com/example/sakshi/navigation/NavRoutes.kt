package com.example.sakshi.navigation

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Map : NavRoutes("map")
    object Contacts : NavRoutes("contacts")
    object Profile : NavRoutes("profile")
}
