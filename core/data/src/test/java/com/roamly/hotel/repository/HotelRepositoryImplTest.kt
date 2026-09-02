package com.roamly.hotel.repository

import com.roamly.hotel.datasource.HotelDataSource
import com.roamly.hotel.datasource.HotelDto
import com.roamly.hotel.local.HotelDao
import com.roamly.hotel.local.HotelEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HotelRepositoryImplTest {

    private lateinit var repository: HotelRepositoryImpl
    private val dataSource: HotelDataSource = mockk()
    private val dao: HotelDao = mockk()

    @Before
    fun setup() {
        repository = HotelRepositoryImpl(dataSource, dao)
    }

    @Test
    fun `getHotels should return domain hotels from dao`() = runTest {
        val entities = listOf(
            HotelEntity(1, "Hotel 1", "Cairo", 4.5, 1000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
        )
        every { dao.getHotels() } returns flowOf(entities)

        val result = repository.getHotels().first()

        assertEquals(1, result.size)
        assertEquals("Hotel 1", result[0].name)
    }

    @Test
    fun `refreshHotels should fetch from dataSource and insert into dao`() = runTest {
        val dtos = listOf(
            HotelDto(1, "Hotel 1", "Cairo", 4.5, 1000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
        )
        coEvery { dataSource.getHotels() } returns dtos
        coEvery { dao.insertHotels(any()) } returns Unit

        val result = repository.refreshHotels()

        assertTrue(result.isSuccess)
        coVerify { dao.insertHotels(any()) }
    }

    @Test
    fun `refreshHotels should fail when dataSource fails and cache is empty`() = runTest {
        coEvery { dataSource.getHotels() } throws Exception("Network error")
        coEvery { dao.getCount() } returns 0

        val result = repository.refreshHotels()

        assertTrue(result.isFailure)
        assertEquals("Failed to load hotels and no local cache available", result.exceptionOrNull()?.message)
    }

    @Test
    fun `refreshHotels should return failure but allow flow when dataSource fails and cache exists`() = runTest {
        coEvery { dataSource.getHotels() } throws Exception("Network error")
        coEvery { dao.getCount() } returns 5

        val result = repository.refreshHotels()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `Cache Fallback - getHotels should continue to emit cached data even if refresh fails`() = runTest {
        val cachedEntities = listOf(
            HotelEntity(1, "Cached Hotel", "Cairo", 4.5, 1000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
        )
        every { dao.getHotels() } returns flowOf(cachedEntities)
        coEvery { dataSource.getHotels() } throws Exception("Network failure")
        coEvery { dao.getCount() } returns 1

        val hotelsFlow = repository.getHotels()
        val refreshResult = repository.refreshHotels()
        val emittedHotels = hotelsFlow.first()

        assertTrue(refreshResult.isFailure)
        assertEquals(1, emittedHotels.size)
        assertEquals("Cached Hotel", emittedHotels[0].name)
    }
}
