package com.roamly.hoteldetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.favorite.usecase.IsFavoriteUseCase
import com.roamly.favorite.usecase.ToggleFavoriteUseCase
import com.roamly.hotel.usecase.GetHotelByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    private val getHotelByIdUseCase: GetHotelByIdUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _hotelId = savedStateHandle.getStateFlow<Long?>(KEY_HOTEL_ID, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HotelDetailsUiState> = _hotelId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                getHotelByIdUseCase(id),
                isFavoriteUseCase(id)
            ) { hotel, isFavorite ->
                if (hotel != null) {
                    HotelDetailsUiState(hotel = hotel, isFavorite = isFavorite)
                } else {
                    HotelDetailsUiState(isNotFound = true)
                }
            }
            .onStart { emit(HotelDetailsUiState(isLoading = true)) }
            .catch { e -> emit(HotelDetailsUiState(errorMessage = e.message ?: "Unknown error")) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HotelDetailsUiState(isLoading = true)
        )

    fun setHotelId(id: Long) {
        if (savedStateHandle.get<Long>(KEY_HOTEL_ID) == null) {
            savedStateHandle[KEY_HOTEL_ID] = id
        }
    }

    fun onToggleFavorite() {
        val id = _hotelId.value ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(id)
        }
    }

    companion object {
        private const val KEY_HOTEL_ID = "hotelId"
    }
}
