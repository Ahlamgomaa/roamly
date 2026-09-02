package com.roamly.hotel.repository

import com.roamly.hotel.datasource.HotelDataSource
import com.roamly.hotel.local.HotelDao
import com.roamly.hotel.mapper.toDomain
import com.roamly.hotel.mapper.toEntity
import com.roamly.hotel.model.Hotel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val dataSource: HotelDataSource,
    private val dao: HotelDao
) : HotelRepository {

    override fun getHotels(): Flow<List<Hotel>> {
        return dao.getHotels().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHotelById(id: Long): Flow<Hotel?> {
        return dao.getHotelById(id).map { it?.toDomain() }
    }

    override suspend fun refreshHotels(): Result<Unit> {
        return try {
            val dtos = dataSource.getHotels()
            if (dtos.isNotEmpty()) {
                dao.insertHotels(dtos.map { it.toEntity() })
            }
            Result.success(Unit)
        } catch (e: Exception) {
            if (dao.getCount() > 0) {
                Result.failure(e)
            } else {
                Result.failure(Exception("Failed to load hotels and no local cache available", e))
            }
        }
    }
}
