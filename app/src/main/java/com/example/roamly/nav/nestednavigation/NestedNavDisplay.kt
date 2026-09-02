package com.example.roamly.nav.nestednavigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.roamly.nav.rootnavigation.AppRoute
import com.example.roamly.nav.rootnavigation.pop
import com.example.roamly.nav.rootnavigation.setRoot
import com.roamly.hotels.HotelsRoot
import com.roamly.favorites.FavoritesScreen

@Composable
fun NestedNavDisplay(
    navigateBack: () -> Unit,
    openHotelDetails: (Long) -> Unit,
) {
    val nestedBackStack = rememberNavBackStack(
        AppRoute.NestedNav.Hotels
    )

    Scaffold(
        bottomBar = {
            NavigationBar {

                BottomBarDestination.entries.forEach { destination ->

                    BottomNavigationButton(
                        onClick = {
                            nestedBackStack.setRoot(
                                destination.route
                            )
                        },

                        icon = if (
                            nestedBackStack.lastOrNull() == destination.route
                        ) {
                            destination.selectedIcon
                        } else {
                            destination.icon
                        },

                        selected = nestedBackStack.lastOrNull() ==
                                destination.route,

                        label = destination.title,
                    )
                }
            }
        }
    ) { innerPadding ->

        NavDisplay(
            backStack = nestedBackStack,

            modifier = Modifier.padding(innerPadding),

            onBack = {
                if (!nestedBackStack.pop()) {
                    navigateBack()
                }
            },

            entryProvider = entryProvider {

                entry<AppRoute.NestedNav.Hotels> {

                    HotelsRoot(
                        onHotelClick = { hotelId ->
                            openHotelDetails(hotelId)
                        }
                    )
                }

                entry<AppRoute.NestedNav.Favorites> {

                    FavoritesScreen(
                        onHotelClick = { hotelId ->
                            openHotelDetails(hotelId)
                        }
                    )
                }
            }
        )
    }
}