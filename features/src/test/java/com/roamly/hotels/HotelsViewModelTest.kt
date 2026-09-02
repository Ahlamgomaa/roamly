package com.roamly.hotels

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.roamly.hotel.model.Hotel
import com.roamly.hotel.usecase.GetHotelsUseCase
import com.roamly.hotel.usecase.RefreshHotelsUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotelsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HotelsViewModel
    private val getHotelsUseCase: GetHotelsUseCase = mockk()
    private val refreshHotelsUseCase: RefreshHotelsUseCase = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val sampleHotels = listOf(
        Hotel(1, "Grand Nile", "Cairo", 4.8, 3000.0, "", "", 0.0, 0.0, emptyList(), emptyList()),
        Hotel(2, "Alex Inn", "Alexandria", 4.2, 1500.0, "", "", 0.0, 0.0, emptyList(), emptyList())
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getHotelsUseCase() } returns flowOf(sampleHotels)
        coEvery { refreshHotelsUseCase() } returns Result.success(Unit)

        viewModel = HotelsViewModel(getHotelsUseCase, refreshHotelsUseCase, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load hotels`() = runTest {
        viewModel.uiState.test {
            var state = awaitItem()
            assertTrue(state.isLoading)

            state = awaitItem()
            assertEquals(2, state.hotels.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query should filter hotels`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onSearchQueryChange("Grand")

            advanceTimeBy(400)
            runCurrent()

            val state = awaitItem()
            assertEquals(1, state.displayedHotels.size)
            assertEquals("Grand Nile", state.displayedHotels[0].name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `city filter should filter hotels`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onCityFilterChange("Alexandria")

            val state = awaitItem()
            assertEquals(1, state.displayedHotels.size)
            assertEquals("Alexandria", state.displayedHotels[0].city)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reset filters should clear all filters`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onSearchQueryChange("Grand")
            advanceTimeBy(400)
            runCurrent()
            awaitItem()

            viewModel.onCityFilterChange("Cairo")
            awaitItem()

            viewModel.onResetFilters()

            advanceTimeBy(400)
            runCurrent()

            val state = expectMostRecentItem()
            assertEquals("", state.searchQuery)
            assertEquals(null, state.selectedCity)
            assertEquals(2, state.displayedHotels.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pagination should load more hotels`() = runTest {
        val manyHotels = (1..15).map {
            Hotel(
                it.toLong(),
                "Hotel ${it}",
                "City",
                4.0,
                1000.0,
                "",
                "",
                0.0,
                0.0,
                emptyList(),
                emptyList()
            )
        }
        every { getHotelsUseCase() } returns flowOf(manyHotels)

        viewModel = HotelsViewModel(getHotelsUseCase, refreshHotelsUseCase, savedStateHandle)

        viewModel.uiState.test {
            awaitItem()
            var state = awaitItem()
            assertEquals(10, state.displayedHotels.size)
            assertTrue(state.hasMore)

            viewModel.onLoadMore()
            state = awaitItem()
            assertEquals(15, state.displayedHotels.size)
            assertFalse(state.hasMore)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
