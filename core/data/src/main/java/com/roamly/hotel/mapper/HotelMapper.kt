package com.roamly.hotel.mapper

import com.roamly.hotel.datasource.HotelDto
import com.roamly.hotel.local.HotelEntity
import com.roamly.hotel.model.Hotel

fun HotelDto.toEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        city = city,
        rating = rating,
        pricePerNight = pricePerNight,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        images = images,
        amenities = amenities
    )
}

fun HotelEntity.toDomain(): Hotel {
    return Hotel(
        id = id,
        name = name,
        city = city,
        rating = rating,
        pricePerNight = pricePerNight,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        images = images,
        amenities = amenities
    )
}
