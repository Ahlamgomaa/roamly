package com.roamly.booking

import androidx.compose.runtime.Composable

@Composable
fun BookingScreen(
    hotelId: Long,
    onBackClick: () -> Unit,
    onBookingConfirmed: (String) -> Unit
) {
}

@Composable
fun BookingSuccessScreen(
    bookingReference: String,
    onDoneClick: () -> Unit
) {
}
