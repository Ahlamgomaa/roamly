package com.roamly.favorite.repository

import com.roamly.favorite.local.FavoriteDao
import com.roamly.favorite.local.FavoriteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavoriteHotelIds(): Flow<List<Long>> {
        return favoriteDao.getFavoriteHotelIds()
    }

    override suspend fun addFavorite(hotelId: Long) {
        favoriteDao.addFavorite(FavoriteEntity(hotelId))
    }

    override suspend fun removeFavorite(hotelId: Long) {
        favoriteDao.removeFavorite(FavoriteEntity(hotelId))
    }

    override fun isFavorite(hotelId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(hotelId)
    }

    override fun getFavoriteCount(): Flow<Int> {
        return favoriteDao.getFavoriteCount()
    }
}
