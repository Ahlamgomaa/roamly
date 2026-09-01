package com.roamly.hotels

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText

import com.roamly.hotel.model.Hotel
import com.roamly.hotel.usecase.GetHotelsUseCase
import com.roamly.hotel.usecase.RefreshHotelsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HotelsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val getHotelsUseCase: GetHotelsUseCase = mockk()
    private val refreshHotelsUseCase: RefreshHotelsUseCase = mockk()

    private val sampleHotels = listOf(
        Hotel(1, "Grand Nile", "Cairo", 4.8, 3000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
    )

    @Before
    fun setup() {
        every { getHotelsUseCase() } returns flowOf(sampleHotels)
        coEvery { refreshHotelsUseCase() } returns Result.success(Unit)
    }

    @Test
    fun hotelsScreen_shouldDisplayHotels() {
        composeTestRule.setContent {
            HotelsScreen(
                onHotelClick = {},
                viewModel = HotelsViewModel(getHotelsUseCase, refreshHotelsUseCase, androidx.lifecycle.SavedStateHandle())
            )
        }

        composeTestRule.onNodeWithText("Grand Nile").assertExists()
        composeTestRule.onNodeWithText("Cairo").assertExists()
    }
}
