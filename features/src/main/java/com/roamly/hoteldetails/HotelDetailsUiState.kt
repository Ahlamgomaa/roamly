package com.roamly.hoteldetails

import com.roamly.hotel.model.Hotel

data class HotelDetailsUiState(
    val hotel: Hotel? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isNotFound: Boolean = false
)
