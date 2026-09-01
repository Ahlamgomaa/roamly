package com.example.roamly.nav.nestednavigation

import com.example.roamly.R
import com.example.roamly.nav.rootnavigation.AppRoute

enum class BottomBarDestination(
    val title: Int,
    val icon: Int,
    val selectedIcon: Int,
    val route: AppRoute,
) {

    Hotels(
        title = R.string.hotels,
        icon = R.drawable.ic_hotel,
        selectedIcon = R.drawable.ic_hotel,
        route = AppRoute.NestedNav.Hotels,
    ),

    Favorites(
        title = R.string.favorites,
        icon = R.drawable.ic_favorite_border,
        selectedIcon = R.drawable.ic_favorite,
        route = AppRoute.NestedNav.Favorites,
    ),
}