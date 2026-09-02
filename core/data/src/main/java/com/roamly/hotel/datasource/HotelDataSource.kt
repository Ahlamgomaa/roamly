package com.roamly.hotel.datasource

interface HotelDataSource {
    suspend fun getHotels(): List<HotelDto>
}
