package com.roamly.hotels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.roamly.hotel.model.Hotel
import org.junit.Rule
import org.junit.Test

class HotelsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleHotels = listOf(
        Hotel(1, "Grand Nile", "Cairo", 4.8, 3000.0, "Desc", "Address", 30.0, 31.0, emptyList(), listOf("WiFi"))
    )

    @Test
    fun hotelsScreen_shouldDisplayHotels() {
        val state = HotelsUiState(
            hotels = sampleHotels,
            displayedHotels = sampleHotels,
            isLoading = false
        )

        composeTestRule.setContent {
            HotelsScreen(
                state = state,
                onSearchQueryChange = {},
                onCityFilterChange = {},
                onRatingFilterChange = {},
                onPriceRangeChange = { _, _ -> },
                onLoadMore = {},
                onRefresh = {},
                onResetFilters = {},
                onHotelClick = {}
            )
        }

        composeTestRule.onNodeWithText("Grand Nile").assertExists()
        composeTestRule.onNodeWithText("Cairo").assertExists()
    }
}
