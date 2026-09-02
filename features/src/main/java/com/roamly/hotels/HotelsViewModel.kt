package com.roamly.hotels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.favorite.usecase.GetFavoriteHotelIdsUseCase
import com.roamly.favorite.usecase.ToggleFavoriteUseCase
import com.roamly.hotel.usecase.GetHotelsUseCase
import com.roamly.hotel.usecase.RefreshHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val refreshHotelsUseCase: RefreshHotelsUseCase,
    private val getFavoriteHotelIdsUseCase: GetFavoriteHotelIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchQuery = savedStateHandle.getStateFlow(KEY_SEARCH_QUERY, "")
    private val _selectedCity = savedStateHandle.getStateFlow<String?>(KEY_CITY, null)
    private val _minRating = savedStateHandle.getStateFlow<Double?>(KEY_RATING, null)
    private val _minPrice = savedStateHandle.getStateFlow<Double?>(KEY_MIN_PRICE, null)
    private val _maxPrice = savedStateHandle.getStateFlow<Double?>(KEY_MAX_PRICE, null)

    private val _currentPage = MutableStateFlow(1)
    private val _isLoadingMore = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)
    private val _isCached = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private var lastRequestedPage = 1

    private val _debouncedQuery = _searchQuery.debounce(300)

    private val _filterState = combine(
        _debouncedQuery,
        _selectedCity,
        _minRating,
        _minPrice,
        _maxPrice
    ) { query, city, rating, minP, maxP ->
        FilterState(query, city, rating, minP, maxP)
    }

    private val _internalState = combine(
        _currentPage,
        _isLoadingMore,
        _isRefreshing,
        _isCached,
        _errorMessage
    ) { page, loadingMore, refreshing, cached, error ->
        InternalState(page, loadingMore, refreshing, cached, error)
    }

    val uiState: StateFlow<HotelsUiState> = combine(
        getHotelsUseCase(),
        getFavoriteHotelIdsUseCase(),
        _searchQuery,
        _filterState,
        _internalState
    ) { hotels, favoriteIds, rawQuery, filters, internal ->
        val hotelItems = hotels.map { hotel ->
            HotelItem(hotel, hotel.id in favoriteIds)
        }
        val filteredHotels = hotelItems.filter { item ->
            val hotel = item.hotel
            val matchesQuery = filters.query.isBlank() || hotel.name.contains(filters.query, ignoreCase = true)
            val matchesCity = filters.city == null || hotel.city.equals(filters.city, ignoreCase = true)
            val matchesRating = filters.rating == null || hotel.rating >= filters.rating
            val matchesMinPrice = filters.minPrice == null || hotel.pricePerNight >= filters.minPrice
            val matchesMaxPrice = filters.maxPrice == null || hotel.pricePerNight <= filters.maxPrice

            matchesQuery && matchesCity && matchesRating && matchesMinPrice && matchesMaxPrice
        }

        val pageSize = 10
        val totalFilteredCount = filteredHotels.size
        val hasMore = internal.page * pageSize < totalFilteredCount
        val displayed = filteredHotels.take(internal.page * pageSize)

        HotelsUiState(
            hotels = hotelItems,
            searchQuery = rawQuery,
            selectedCity = filters.city,
            minRating = filters.rating,
            minPrice = filters.minPrice,
            maxPrice = filters.maxPrice,
            isLoading = hotels.isEmpty() && internal.error == null && !internal.isRefreshing,
            isRefreshing = internal.isRefreshing,
            isLoadingMore = internal.isLoadingMore,
            isCached = internal.isCached,
            hasMore = hasMore,
            errorMessage = internal.error,
            availableCities = hotels.map { it.city }.distinct().sorted(),
            displayedHotels = displayed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HotelsUiState(isLoading = true)
    )

    init {
        refresh()
    }

    fun onSearchQueryChange(query: String) {
        savedStateHandle[KEY_SEARCH_QUERY] = query
        resetPagination()
    }

    fun onCityFilterChange(city: String?) {
        savedStateHandle[KEY_CITY] = city
        resetPagination()
    }

    fun onRatingFilterChange(rating: Double?) {
        savedStateHandle[KEY_RATING] = rating
        resetPagination()
    }

    fun onPriceRangeChange(min: Double?, max: Double?) {
        savedStateHandle[KEY_MIN_PRICE] = min
        savedStateHandle[KEY_MAX_PRICE] = max
        resetPagination()
    }

    fun onResetFilters() {
        savedStateHandle[KEY_SEARCH_QUERY] = ""
        savedStateHandle[KEY_RATING] = null
        savedStateHandle[KEY_MIN_PRICE] = null
        savedStateHandle[KEY_MAX_PRICE] = null
        resetPagination()
    }

    fun onToggleFavorite(hotelId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(hotelId)
        }
    }

    private fun resetPagination() {
        _currentPage.value = 1
        lastRequestedPage = 1
    }

    fun onLoadMore() {
        val currentState = uiState.value
        val nextPage = _currentPage.value + 1
        
        if (!currentState.isLoadingMore && currentState.hasMore && nextPage > lastRequestedPage) {
            viewModelScope.launch {
                _isLoadingMore.value = true
                lastRequestedPage = nextPage
                _currentPage.value = nextPage
                _isLoadingMore.value = false
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            val result = refreshHotelsUseCase()
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                _isCached.value = true
            } else {
                _isCached.value = false
            }
            _isRefreshing.value = false
        }
    }

    private data class FilterState(
        val query: String,
        val city: String?,
        val rating: Double?,
        val minPrice: Double?,
        val maxPrice: Double?
    )

    private data class InternalState(
        val page: Int,
        val isLoadingMore: Boolean,
        val isRefreshing: Boolean,
        val isCached: Boolean,
        val error: String?
    )

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
        private const val KEY_CITY = "city"
        private const val KEY_RATING = "rating"
        private const val KEY_MIN_PRICE = "min_price"
        private const val KEY_MAX_PRICE = "max_price"
    }
}
