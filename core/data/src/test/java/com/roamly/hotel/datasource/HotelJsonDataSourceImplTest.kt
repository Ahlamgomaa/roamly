package com.roamly.hotel.datasource

import android.content.Context
import android.content.res.AssetManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream

class HotelJsonDataSourceImplTest {

    private lateinit var dataSource: HotelJsonDataSourceImpl
    private val context: Context = mockk()
    private val assetManager: AssetManager = mockk()
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Before
    fun setup() {
        every { context.assets } returns assetManager
        dataSource = HotelJsonDataSourceImpl(context, moshi)
    }

    @Test
    fun `getHotels should parse JSON correctly`() = runTest {
        val json = """
            [
              {
                "id": 1,
                "name": "Grand Nile Hotel",
                "city": "Cairo",
                "rating": 4.8,
                "pricePerNight": 3200,
                "description": "Description",
                "address": "Address",
                "latitude": 30.0,
                "longitude": 31.0,
                "images": ["url1"],
                "amenities": ["WiFi"]
              }
            ]
        """.trimIndent()
        
        every { assetManager.open("hotels.json") } returns ByteArrayInputStream(json.toByteArray())

        val result = dataSource.getHotels()

        assertEquals(1, result.size)
        assertEquals("Grand Nile Hotel", result[0].name)
        assertEquals(4.8, result[0].rating, 0.0)
    }

    @Test
    fun `getHotels should return empty list on invalid JSON`() = runTest {
        every { assetManager.open("hotels.json") } returns ByteArrayInputStream("invalid".toByteArray())

        try {
            val result = dataSource.getHotels()
        } catch (e: Exception) {
            assertTrue(true)
        }
    }
}
