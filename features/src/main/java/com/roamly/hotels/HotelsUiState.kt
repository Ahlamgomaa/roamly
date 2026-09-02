package com.roamly.hotels

import com.roamly.hotel.model.Hotel

data class HotelItem(
    val hotel: Hotel,
    val isFavorite: Boolean
)

data class HotelsUiState(
    val hotels: List<HotelItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCity: String? = null,
    val minRating: Double? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val isCached: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null,
    val availableCities: List<String> = emptyList(),
    val displayedHotels: List<HotelItem> = emptyList()
)
