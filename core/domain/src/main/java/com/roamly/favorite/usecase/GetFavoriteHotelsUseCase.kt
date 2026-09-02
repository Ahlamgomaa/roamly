package com.roamly.favorite.usecase

import com.roamly.favorite.repository.FavoriteRepository
import com.roamly.hotel.model.Hotel
import com.roamly.hotel.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetFavoriteHotelsUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val favoriteRepository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<Hotel>> {
        return combine(
            hotelRepository.getHotels(),
            favoriteRepository.getFavoriteHotelIds()
        ) { hotels, favoriteIds ->
            hotels.filter { it.id in favoriteIds }
        }
    }
}
