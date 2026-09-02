package com.roamly.hotel.repository

import com.roamly.hotel.model.Hotel
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getHotels(): Flow<List<Hotel>>
    fun getHotelById(id: Long): Flow<Hotel?>
    suspend fun refreshHotels(): Result<Unit>
}
