package com.roamly.hoteldetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.roamly.hotel.model.Hotel
import com.roamly.hotel.usecase.GetHotelByIdUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotelDetailsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getHotelByIdUseCase: GetHotelByIdUseCase = mockk()
    private val hotelId = 1L

    private val sampleHotel = Hotel(
        id = 1,
        name = "Grand Nile",
        city = "Cairo",
        rating = 4.8,
        pricePerNight = 3000.0,
        description = "A luxurious hotel in Cairo.",
        address = "Cairo, Egypt",
        latitude = 30.0,
        longitude = 31.0,
        images = listOf("url1", "url2"),
        amenities = listOf("WiFi", "Pool")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load hotel details`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        
        val viewModel = HotelDetailsViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            var state = awaitItem()
            assertTrue(state.isLoading)

            state = awaitItem()
            assertEquals(sampleHotel, state.hotel)
            assertFalse(state.isLoading)
            assertFalse(state.isNotFound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be not found when hotel is null`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(null)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        
        val viewModel = HotelDetailsViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem()
            
            val state = awaitItem()
            assertTrue(state.isNotFound)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state should be error when use case throws exception`() = runTest {
        val errorMessage = "Network error"
        every { getHotelByIdUseCase(hotelId) } returns flow {
            throw Exception(errorMessage)
        }
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        
        val viewModel = HotelDetailsViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem()
            
            val state = awaitItem()
            assertEquals(errorMessage, state.errorMessage)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
