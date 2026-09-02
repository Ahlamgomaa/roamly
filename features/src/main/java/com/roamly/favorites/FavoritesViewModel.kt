package com.roamly.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.favorite.usecase.GetFavoriteHotelsUseCase
import com.roamly.favorite.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoriteHotelsUseCase: GetFavoriteHotelsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = getFavoriteHotelsUseCase()
        .map { hotels ->
            FavoritesUiState(favoriteHotels = hotels)
        }
        .onStart { emit(FavoritesUiState(isLoading = true)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FavoritesUiState(isLoading = true)
        )

    fun onToggleFavorite(hotelId: Long) {
        viewModelScope.launch {
            toggleFavoriteUseCase(hotelId)
        }
    }
}
