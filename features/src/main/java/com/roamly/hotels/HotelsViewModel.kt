package com.roamly.hotels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.hotel.model.Hotel
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

    val uiState: StateFlow<HotelsUiState> = combine(
        getHotelsUseCase(),
        _searchQuery.debounce(300),
        _selectedCity,
        _minRating,
        _minPrice,
        _maxPrice,
        _currentPage,
        _isLoadingMore,
        _isRefreshing,
        _isCached,
        _errorMessage
    ) { params ->
        val hotels = params[0] as List<Hotel>
        val query = params[1] as String
        val city = params[2] as? String
        val rating = params[3] as? Double
        val minP = params[4] as? Double
        val maxP = params[5] as? Double
        val page = params[6] as Int
        val loadingMore = params[7] as Boolean
        val refreshing = params[8] as Boolean
        val cached = params[9] as Boolean
        val error = params[10] as? String

        val filteredHotels = hotels.filter { hotel ->
            val matchesQuery = query.isBlank() || hotel.name.contains(query, ignoreCase = true)
            val matchesCity = city == null || hotel.city.equals(city, ignoreCase = true)
            val matchesRating = rating == null || hotel.rating >= rating
            val matchesMinPrice = minP == null || hotel.pricePerNight >= minP
            val matchesMaxPrice = maxP == null || hotel.pricePerNight <= maxP

            matchesQuery && matchesCity && matchesRating && matchesMinPrice && matchesMaxPrice
        }

        val pageSize = 10
        val totalFilteredCount = filteredHotels.size
        val hasMore = page * pageSize < totalFilteredCount
        val displayed = filteredHotels.take(page * pageSize)

        HotelsUiState(
            hotels = hotels,
            searchQuery = query,
            selectedCity = city,
            minRating = rating,
            minPrice = minP,
            maxPrice = maxP,
            isLoading = hotels.isEmpty() && error == null && !refreshing,
            isRefreshing = refreshing,
            isLoadingMore = loadingMore,
            isCached = cached,
            hasMore = hasMore,
            errorMessage = error,
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
        _currentPage.value = 1
    }

    fun onCityFilterChange(city: String?) {
        savedStateHandle[KEY_CITY] = city
        _currentPage.value = 1
    }

    fun onRatingFilterChange(rating: Double?) {
        savedStateHandle[KEY_RATING] = rating
        _currentPage.value = 1
    }

    fun onPriceRangeChange(min: Double?, max: Double?) {
        savedStateHandle[KEY_MIN_PRICE] = min
        savedStateHandle[KEY_MAX_PRICE] = max
        _currentPage.value = 1
    }

    fun onResetFilters() {
        savedStateHandle[KEY_SEARCH_QUERY] = ""
        savedStateHandle[KEY_CITY] = null
        savedStateHandle[KEY_RATING] = null
        savedStateHandle[KEY_MIN_PRICE] = null
        savedStateHandle[KEY_MAX_PRICE] = null
        _currentPage.value = 1
    }

    fun onLoadMore() {
        val state = uiState.value
        if (!state.isLoadingMore && state.hasMore) {
            viewModelScope.launch {
                _isLoadingMore.value = true
                _currentPage.value += 1
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

    companion object {
        private const val KEY_SEARCH_QUERY = "search_query"
        private const val KEY_CITY = "city"
        private const val KEY_RATING = "rating"
        private const val KEY_MIN_PRICE = "min_price"
        private const val KEY_MAX_PRICE = "max_price"
    }
}
