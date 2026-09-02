package com.roamly.hotel.datasource

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HotelJsonDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi
) : HotelDataSource {

    override suspend fun getHotels(): List<HotelDto> = withContext(Dispatchers.IO) {
        val jsonString = context.assets.open("hotels.json").bufferedReader().use { it.readText() }
        val type = Types.newParameterizedType(List::class.java, HotelDto::class.java)
        val adapter = moshi.adapter<List<HotelDto>>(type)
        adapter.fromJson(jsonString) ?: emptyList()
    }
}
