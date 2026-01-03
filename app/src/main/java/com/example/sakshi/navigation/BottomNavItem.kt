package com.example.sakshi.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Warning)
    object Map : BottomNavItem("map", "Map", Icons.Filled.Map)
    object Contacts : BottomNavItem("contacts", "Contacts", Icons.Filled.People)
    object Profile : BottomNavItem("profile", "Profile", Icons.Filled.Person)
}
