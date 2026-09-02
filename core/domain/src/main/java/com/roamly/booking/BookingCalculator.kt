package com.roamly.booking

object BookingCalculator {

    const val VAT_RATE = 0.15

    fun calculateNights(checkInEpochDays: Long, checkOutEpochDays: Long): Int {
        if (checkOutEpochDays <= checkInEpochDays) return 0
        return (checkOutEpochDays - checkInEpochDays).toInt()
    }

    fun calculatePrice(
        pricePerNight: Double,
        nights: Int,
        numberOfRooms: Int
    ): BookingPriceBreakdown {
        val basePrice = pricePerNight * nights * numberOfRooms
        val vatAmount = basePrice * VAT_RATE
        val totalPrice = basePrice + vatAmount
        
        return BookingPriceBreakdown(
            basePrice = basePrice,
            vatAmount = vatAmount,
            totalPrice = totalPrice
        )
    }

    fun validateDates(checkInEpochDays: Long, checkOutEpochDays: Long, todayEpochDays: Long): BookingDateValidation {
        return when {
            checkInEpochDays < todayEpochDays -> BookingDateValidation.CheckInInPast
            checkOutEpochDays < checkInEpochDays -> BookingDateValidation.CheckOutBeforeCheckIn
            checkOutEpochDays == checkInEpochDays -> BookingDateValidation.CheckOutIsCheckIn
            else -> BookingDateValidation.Valid
        }
    }
}

data class BookingPriceBreakdown(
    val basePrice: Double,
    val vatAmount: Double,
    val totalPrice: Double
)

sealed interface BookingDateValidation {
    data object Valid : BookingDateValidation
    data object CheckInInPast : BookingDateValidation
    data object CheckOutBeforeCheckIn : BookingDateValidation
    data object CheckOutIsCheckIn : BookingDateValidation
}
