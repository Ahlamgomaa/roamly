package com.example.roamly.nav.rootnavigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import com.example.roamly.nav.nestednavigation.NestedNavDisplay
import com.roamly.booking.BookingRoot
import com.roamly.booking.BookingSuccessScreen
import com.roamly.hoteldetails.HotelDetailsRoot

@Composable
fun RootNavDisplay() {

    val rootBackStack = rememberNavBackStack(
        AppRoute.NestedNav
    )

    NavDisplay(
        backStack = rootBackStack,

        onBack = {
            rootBackStack.pop()
        },

        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),

        entryProvider = entryProvider {

            entry<AppRoute.NestedNav> {

                NestedNavDisplay(

                    navigateBack = {
                        rootBackStack.pop()
                    },

                    openHotelDetails = { hotelId ->

                        rootBackStack.push(
                            AppRoute.HotelDetails(
                                hotelId = hotelId
                            )
                        )
                    }
                )
            }

            entry<AppRoute.HotelDetails> { route ->

                HotelDetailsRoot(
                    hotelId = route.hotelId,

                    onBackClick = {
                        rootBackStack.popIfCurrent(route)
                    },

                    onBookClick = {

                        rootBackStack.push(
                            AppRoute.Booking(
                                hotelId = route.hotelId
                            )
                        )
                    }
                )
            }



            entry<AppRoute.Booking> { route ->

                BookingRoot(
                    hotelId = route.hotelId,

                    onBackClick = {
                        rootBackStack.popIfCurrent(route)
                    },

                    onBookingConfirmed = { reference, hotelName, checkIn, checkOut, rooms, total ->

                        rootBackStack.push(
                            AppRoute.BookingSuccess(
                                bookingReference = reference,
                                hotelName = hotelName,
                                checkIn = checkIn,
                                checkOut = checkOut,
                                rooms = rooms,
                                totalPrice = total
                            )
                        )
                    }
                )
            }



            entry<AppRoute.BookingSuccess> { route ->

                BookingSuccessScreen(
                    bookingReference = route.bookingReference,
                    hotelName = route.hotelName,
                    checkIn = route.checkIn,
                    checkOut = route.checkOut,
                    rooms = route.rooms,
                    totalPrice = route.totalPrice,

                    onDoneClick = {

                        rootBackStack.setRoot(
                            AppRoute.NestedNav
                        )
                    }
                )
            }
        }
    )
}
