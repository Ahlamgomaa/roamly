package com.roamly.booking

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.roamly.hotel.model.Hotel
import com.roamly.hotel.usecase.GetHotelByIdUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

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
    fun `initial state should load hotel`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        val viewModel = BookingViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            var state = awaitItem()
            assertTrue(state.isLoading)

            state = awaitItem()
            assertEquals(sampleHotel, state.hotel)
            assertEquals(1, state.rooms)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `selecting dates should update state and calculate nights`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        val viewModel = BookingViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() // Loading
            awaitItem() // Initial state

            val today = System.currentTimeMillis() / 86400000L
            viewModel.onCheckInSelected(today + 1)
            var state = awaitItem()
            assertEquals(today + 1, state.checkIn)

            viewModel.onCheckOutSelected(today + 3)
            state = awaitItem()
            assertEquals(today + 3, state.checkOut)
            assertEquals(2, state.nights)
            assertNotNull(state.priceBreakdown)
            assertEquals(6000.0, state.priceBreakdown!!.basePrice, 0.0)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `changing rooms should update price`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        val viewModel = BookingViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() // Loading
            awaitItem() // Initial state

            val today = System.currentTimeMillis() / 86400000L
            viewModel.onCheckInSelected(today + 1)
            awaitItem()
            viewModel.onCheckOutSelected(today + 2)
            awaitItem()
            
            viewModel.onRoomsChanged(2)
            val state = awaitItem()
            assertEquals(2, state.rooms)
            assertEquals(6000.0, state.priceBreakdown!!.basePrice, 0.0) // 3000 * 1 night * 2 rooms
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `invalid dates should show error in state`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        val viewModel = BookingViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() // Loading
            awaitItem() // Initial state

            val today = System.currentTimeMillis() / 86400000L
            // Check-out before check-in
            viewModel.onCheckInSelected(today + 5)
            awaitItem()
            viewModel.onCheckOutSelected(today + 3)
            val state = awaitItem()
            assertEquals(BookingDateValidation.CheckOutBeforeCheckIn, state.dateValidation)
            assertFalse(state.isConfirmEnabled)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirming booking should generate reference`() = runTest {
        every { getHotelByIdUseCase(hotelId) } returns flowOf(sampleHotel)
        val savedStateHandle = SavedStateHandle(mapOf("hotelId" to hotelId))
        val viewModel = BookingViewModel(getHotelByIdUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem() // Loading
            awaitItem() // Initial state

            viewModel.confirmBooking()
            val state = awaitItem()
            assertNotNull(state.bookingReference)
            assertTrue(state.bookingReference!!.startsWith("ROAM-"))
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
