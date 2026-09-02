package com.roamly.favorites

import com.roamly.hotel.model.Hotel

data class FavoritesUiState(
    val favoriteHotels: List<Hotel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
