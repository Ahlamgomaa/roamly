package com.example.roamly.nav.nestednavigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.roamly.R
import com.example.roamly.nav.rootnavigation.AppRoute

enum class BottomBarDestination(
    val title: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
    val route: AppRoute,
) {

    Hotels(
        title = R.string.hotels,
        icon = Icons.Outlined.Hotel,
        selectedIcon = Icons.Filled.Hotel,
        route = AppRoute.NestedNav.Hotels,
    ),

    Favorites(
        title = R.string.favorites,
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite,
        route = AppRoute.NestedNav.Favorites,
    ),
}
