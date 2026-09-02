package com.roamly.hotel.usecase

import com.roamly.hotel.model.Hotel
import com.roamly.hotel.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    operator fun invoke(): Flow<List<Hotel>> = repository.getHotels()
}
