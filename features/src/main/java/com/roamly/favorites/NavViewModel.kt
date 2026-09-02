package com.roamly.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roamly.favorite.usecase.GetFavoriteCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NavViewModel @Inject constructor(
    private val getFavoriteCountUseCase: GetFavoriteCountUseCase
) : ViewModel() {

    val favoriteCount: StateFlow<Int> = getFavoriteCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}
