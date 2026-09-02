package com.roamly.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.hotel.usecase.GetHotelByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _hotelId = savedStateHandle.getStateFlow<Long?>(KEY_HOTEL_ID, null)
    private val _checkIn = savedStateHandle.getStateFlow<Long?>(KEY_CHECK_IN, null)
    private val _checkOut = savedStateHandle.getStateFlow<Long?>(KEY_CHECK_OUT, null)
    private val _rooms = savedStateHandle.getStateFlow(KEY_ROOMS, 1)
    private val _bookingReference = savedStateHandle.getStateFlow<String?>(KEY_REFERENCE, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BookingUiState> = combine(
        _hotelId.filterNotNull().flatMapLatest { id -> getHotelByIdUseCase(id) },
        _checkIn,
        _checkOut,
        _rooms,
        _bookingReference
    ) { hotel, checkIn, checkOut, rooms, reference ->
        
        val todayEpochDays = System.currentTimeMillis() / 86400000L
        
        val validation = when {
            checkIn != null && checkOut != null -> BookingCalculator.validateDates(checkIn, checkOut, todayEpochDays)
            checkIn != null && checkIn < todayEpochDays -> BookingDateValidation.CheckInInPast
            else -> BookingDateValidation.Valid
        }

        val nights = if (checkIn != null && checkOut != null) {
            BookingCalculator.calculateNights(checkIn, checkOut)
        } else 0

        val priceBreakdown = if (hotel != null && nights > 0) {
            BookingCalculator.calculatePrice(hotel.pricePerNight, nights, rooms)
        } else null

        BookingUiState(
            hotel = hotel,
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            priceBreakdown = priceBreakdown,
            dateValidation = validation,
            bookingReference = reference
        )
    }
    .onStart { BookingUiState(isLoading = true) }
    .catch { e -> emit(BookingUiState(errorMessage = e.message ?: "Unknown error")) }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BookingUiState(isLoading = true)
    )

    fun setHotelId(id: Long) {
        if (savedStateHandle.get<Long>(KEY_HOTEL_ID) == null) {
            savedStateHandle[KEY_HOTEL_ID] = id
        }
    }

    fun onCheckInSelected(epochDays: Long) {
        savedStateHandle[KEY_CHECK_IN] = epochDays
    }

    fun onCheckOutSelected(epochDays: Long) {
        savedStateHandle[KEY_CHECK_OUT] = epochDays
    }

    fun onRoomsChanged(rooms: Int) {
        if (rooms >= 1) {
            savedStateHandle[KEY_ROOMS] = rooms
        }
    }

    fun confirmBooking() {
        val reference = generateBookingReference()
        savedStateHandle[KEY_REFERENCE] = reference
    }

    private fun generateBookingReference(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val randomPart = (1..6).map { chars.random() }.joinToString("")
        return "ROAM-$randomPart"
    }

    companion object {
        private const val KEY_HOTEL_ID = "hotelId"
        private const val KEY_CHECK_IN = "check_in"
        private const val KEY_CHECK_OUT = "check_out"
        private const val KEY_ROOMS = "rooms"
        private const val KEY_REFERENCE = "reference"
    }
}
