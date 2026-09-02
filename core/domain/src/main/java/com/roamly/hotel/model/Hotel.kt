package com.roamly.hotel.model

data class Hotel(
    val id: Long,
    val name: String,
    val city: String,
    val rating: Double,
    val pricePerNight: Double,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String>,
    val amenities: List<String>
)
