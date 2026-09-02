package com.roamly.favorite.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getFavoriteHotelIds(): Flow<List<Long>>
    suspend fun addFavorite(hotelId: Long)
    suspend fun removeFavorite(hotelId: Long)
    fun isFavorite(hotelId: Long): Flow<Boolean>
    fun getFavoriteCount(): Flow<Int>
}
