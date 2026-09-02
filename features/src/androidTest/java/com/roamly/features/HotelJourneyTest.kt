package com.roamly.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.roamly.booking.BookingDateValidation
import com.roamly.booking.BookingPriceBreakdown
import com.roamly.booking.BookingScreen
import com.roamly.booking.BookingUiState
import com.roamly.hotel.model.Hotel
import com.roamly.hoteldetails.HotelDetailsScreen
import com.roamly.hoteldetails.HotelDetailsUiState
import com.roamly.hotels.HotelsScreen
import com.roamly.hotels.HotelsUiState
import org.junit.Rule
import org.junit.Test

class HotelJourneyTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleHotel = Hotel(
        id = 1,
        name = "Grand Nile",
        city = "Cairo",
        rating = 4.8,
        pricePerNight = 3000.0,
        description = "A luxurious hotel.",
        address = "Cairo, Egypt",
        latitude = 30.0,
        longitude = 31.0,
        images = emptyList(),
        amenities = listOf("WiFi", "Pool")
    )

    @Test
    fun criticalJourney_HotelsToBooking() {
        composeTestRule.setContent {
            var currentScreen by remember { mutableStateOf("hotels") }
            
            when (currentScreen) {
                "hotels" -> {
                    HotelsScreen(
                        state = HotelsUiState(
                            hotels = listOf(com.roamly.hotels.HotelItem(sampleHotel, false)),
                            displayedHotels = listOf(com.roamly.hotels.HotelItem(sampleHotel, false)),
                            isLoading = false
                        ),
                        onSearchQueryChange = {},
                        onCityFilterChange = {},
                        onRatingFilterChange = {},
                        onPriceRangeChange = { _, _ -> },
                        onLoadMore = {},
                        onRefresh = {},
                        onResetFilters = {},
                        onHotelClick = { currentScreen = "details" },
                        onToggleFavorite = {}
                    )
                }
                "details" -> {
                    HotelDetailsScreen(
                        state = HotelDetailsUiState(
                            hotel = sampleHotel,
                            isLoading = false
                        ),
                        onBackClick = { currentScreen = "hotels" },
                        onBookClick = { currentScreen = "booking" },
                        onToggleFavorite = {}
                    )
                }
                "booking" -> {
                    BookingScreen(
                        state = BookingUiState(
                            hotel = sampleHotel,
                            checkIn = 100L,
                            checkOut = 102L,
                            rooms = 1,
                            priceBreakdown = BookingPriceBreakdown(6000.0, 900.0, 6900.0),
                            dateValidation = BookingDateValidation.Valid
                        ),
                        onBackClick = { currentScreen = "details" },
                        onCheckInSelected = {},
                        onCheckOutSelected = {},
                        onRoomsChanged = {},
                        onConfirmClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Grand Nile").assertExists()
        composeTestRule.onNodeWithText("Grand Nile").performClick()

        composeTestRule.onNodeWithText("About this hotel").assertExists()
        composeTestRule.onNodeWithText("3000 EGP").assertExists()
        composeTestRule.onNodeWithText("Book Now").performClick()

        composeTestRule.onNodeWithText("Booking").assertExists()
        composeTestRule.onNodeWithText("Price Summary").assertExists()
        composeTestRule.onNodeWithText("6900 EGP").assertExists()
    }
}
