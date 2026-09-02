package com.roamly.booking

import com.roamly.hotel.model.Hotel

data class BookingUiState(
    val hotel: Hotel? = null,
    val checkIn: Long? = null,
    val checkOut: Long? = null,
    val rooms: Int = 1,
    val priceBreakdown: BookingPriceBreakdown? = null,
    val dateValidation: BookingDateValidation = BookingDateValidation.Valid,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookingReference: String? = null
) {
    val nights: Int = if (checkIn != null && checkOut != null) {
        BookingCalculator.calculateNights(checkIn, checkOut)
    } else 0

    val isConfirmEnabled: Boolean = hotel != null && 
            checkIn != null && 
            checkOut != null && 
            nights > 0 && 
            dateValidation == BookingDateValidation.Valid
}
