package com.roamly.favorite.repository

import com.roamly.favorite.local.FavoriteDao
import com.roamly.favorite.local.FavoriteEntity
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

class FavoriteRepositoryImplTest {

    private lateinit var repository: FavoriteRepositoryImpl
    private val dao: FavoriteDao = mockk()

    @Before
    fun setup() {
        repository = FavoriteRepositoryImpl(dao)
    }

    @Test
    fun `getFavoriteHotelIds should return ids from dao`() = runTest {
        val ids = listOf(1L, 2L)
        every { dao.getFavoriteHotelIds() } returns flowOf(ids)

        val result = repository.getFavoriteHotelIds().first()

        assertEquals(ids, result)
    }

    @Test
    fun `addFavorite should call dao`() = runTest {
        val hotelId = 1L
        coEvery { dao.addFavorite(any()) } returns Unit

        repository.addFavorite(hotelId)

        coVerify { dao.addFavorite(FavoriteEntity(hotelId)) }
    }

    @Test
    fun `removeFavorite should call dao`() = runTest {
        val hotelId = 1L
        coEvery { dao.removeFavorite(any()) } returns Unit

        repository.removeFavorite(hotelId)

        coVerify { dao.removeFavorite(FavoriteEntity(hotelId)) }
    }

    @Test
    fun `isFavorite should return value from dao`() = runTest {
        val hotelId = 1L
        every { dao.isFavorite(hotelId) } returns flowOf(true)

        val result = repository.isFavorite(hotelId).first()

        assertTrue(result)
    }

    @Test
    fun `getFavoriteCount should return count from dao`() = runTest {
        every { dao.getFavoriteCount() } returns flowOf(5)

        val result = repository.getFavoriteCount().first()

        assertEquals(5, result)
    }
}
