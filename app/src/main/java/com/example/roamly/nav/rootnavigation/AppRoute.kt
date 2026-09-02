package com.example.roamly.nav.rootnavigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {

    @Serializable
    data object NestedNav : AppRoute {

        @Serializable
        data object Hotels : AppRoute

        @Serializable
        data object Favorites : AppRoute
    }

    @Serializable
    data class HotelDetails(
        val hotelId: Long,
    ) : AppRoute

    @Serializable
    data class Booking(
        val hotelId: Long,
    ) : AppRoute

    @Serializable
    data class BookingSuccess(
        val bookingReference: String,
        val hotelName: String,
        val checkIn: Long,
        val checkOut: Long,
        val rooms: Int,
        val totalPrice: Double
    ) : AppRoute
}