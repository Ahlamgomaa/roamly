package com.roamly.favorite.usecase

import com.roamly.favorite.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(hotelId: Long): Flow<Boolean> {
        return favoriteRepository.isFavorite(hotelId)
    }
}
