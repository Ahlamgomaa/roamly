package com.roamly.booking

import org.junit.Assert.assertEquals
import org.junit.Test

class BookingCalculatorTest {

    @Test
    fun `calculateNights should return correct number of days`() {
        val checkIn = 100L
        val checkOut = 103L
        assertEquals(3, BookingCalculator.calculateNights(checkIn, checkOut))
    }

    @Test
    fun `calculateNights should return 0 when checkout is same as checkin`() {
        val checkIn = 100L
        val checkOut = 100L
        assertEquals(0, BookingCalculator.calculateNights(checkIn, checkOut))
    }

    @Test
    fun `calculateNights should return 0 when checkout is before checkin`() {
        val checkIn = 100L
        val checkOut = 99L
        assertEquals(0, BookingCalculator.calculateNights(checkIn, checkOut))
    }

    @Test
    fun `calculatePrice should return correct values`() {
        val pricePerNight = 1000.0
        val nights = 3
        val rooms = 2

        val breakdown = BookingCalculator.calculatePrice(pricePerNight, nights, rooms)

        assertEquals(6000.0, breakdown.basePrice, 0.0)
        assertEquals(900.0, breakdown.vatAmount, 0.0)
        assertEquals(6900.0, breakdown.totalPrice, 0.0)
    }

    @Test
    fun `validateDates should return Valid for future dates`() {
        val today = 100L
        val checkIn = 101L
        val checkOut = 102L
        assertEquals(BookingDateValidation.Valid, BookingCalculator.validateDates(checkIn, checkOut, today))
    }

    @Test
    fun `validateDates should return CheckInInPast for past checkin`() {
        val today = 100L
        val checkIn = 99L
        val checkOut = 101L
        assertEquals(BookingDateValidation.CheckInInPast, BookingCalculator.validateDates(checkIn, checkOut, today))
    }

    @Test
    fun `validateDates should return CheckOutBeforeCheckIn when checkout is before checkin`() {
        val today = 100L
        val checkIn = 101L
        val checkOut = 99L
        assertEquals(BookingDateValidation.CheckOutBeforeCheckIn, BookingCalculator.validateDates(checkIn, checkOut, today))
    }

    @Test
    fun `validateDates should return CheckOutIsCheckIn when checkout equals checkin`() {
        val today = 100L
        val checkIn = 101L
        val checkOut = 101L
        assertEquals(BookingDateValidation.CheckOutIsCheckIn, BookingCalculator.validateDates(checkIn, checkOut, today))
    }
}
