package com.roamly.favorite.usecase

import com.roamly.favorite.repository.FavoriteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(hotelId: Long) {
        val isFavorite = favoriteRepository.isFavorite(hotelId).first()
        if (isFavorite) {
            favoriteRepository.removeFavorite(hotelId)
        } else {
            favoriteRepository.addFavorite(hotelId)
        }
    }
}
