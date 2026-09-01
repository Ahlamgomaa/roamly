package com.roamly.hotel.usecase

import com.roamly.hotel.repository.HotelRepository
import javax.inject.Inject

class RefreshHotelsUseCase @Inject constructor(
    private val repository: HotelRepository
) {
    suspend operator fun invoke(): Result<Unit> = repository.refreshHotels()
}
