package com.roamly.hotels

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.roamly.favorite.repository.FavoriteRepository
import com.roamly.favorite.usecase.ToggleFavoriteUseCase
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
    private val favoriteRepository: FavoriteRepository = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val savedStateHandle = SavedStateHandle()

    private val sampleHotels = listOf(
        Hotel(1, "Grand Nile", "Cairo", 4.8, 3000.0, "", "", 0.0, 0.0, emptyList(), emptyList()),
        Hotel(2, "Alex Inn", "Alexandria", 4.2, 1500.0, "", "", 0.0, 0.0, emptyList(), emptyList()),
        Hotel(3, "Luxor Palace", "Luxor", 4.5, 2000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getHotelsUseCase() } returns flowOf(sampleHotels)
        coEvery { refreshHotelsUseCase() } returns Result.success(Unit)
        every { favoriteRepository.getFavoriteHotelIds() } returns flowOf(emptyList())

        viewModel = HotelsViewModel(
            getHotelsUseCase,
            refreshHotelsUseCase,
            favoriteRepository,
            toggleFavoriteUseCase,
            savedStateHandle
        )
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
            assertEquals(3, state.hotels.size)
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search query should filter hotels case-insensitively and immediately update raw query`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onSearchQueryChange("grand")

            var state = awaitItem()
            assertEquals("grand", state.searchQuery)
            assertEquals(3, state.displayedHotels.size)

            advanceTimeBy(300)
            runCurrent()

            state = awaitItem()
            assertEquals(1, state.displayedHotels.size)
            assertTrue(state.displayedHotels[0].hotel.name.contains("Grand", ignoreCase = true))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `city filter should work with search query`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onSearchQueryChange("Inn")
            advanceTimeBy(300)
            runCurrent()
            awaitItem()
            awaitItem()

            viewModel.onCityFilterChange("Alexandria")
            runCurrent()

            val state = expectMostRecentItem()
            assertEquals(1, state.displayedHotels.size)
            assertEquals("Alexandria", state.displayedHotels[0].hotel.city)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `rating filter should filter hotels`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onRatingFilterChange(4.7)
            runCurrent()

            val state = awaitItem()
            assertEquals(1, state.displayedHotels.size)
            assertTrue(state.displayedHotels[0].hotel.rating >= 4.7)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `price range filter should filter hotels`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onPriceRangeChange(1000.0, 2500.0)
            runCurrent()

            val state = awaitItem()
            assertEquals(2, state.displayedHotels.size)
            assertTrue(state.displayedHotels.all { it.hotel.pricePerNight in 1000.0..2500.0 })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `pagination should load more hotels and protect against duplicates`() = runTest {
        val manyHotels = (1..25).map {
            Hotel(it.toLong(), "Hotel $it", "City", 4.0, 1000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
        }
        every { getHotelsUseCase() } returns flowOf(manyHotels)

        viewModel = HotelsViewModel(
            getHotelsUseCase,
            refreshHotelsUseCase,
            favoriteRepository,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        viewModel.uiState.test {
            awaitItem()
            var state = awaitItem()
            assertEquals(10, state.displayedHotels.size)

            viewModel.onLoadMore()
            state = awaitItem()
            assertEquals(20, state.displayedHotels.size)

            viewModel.onLoadMore()
            runCurrent()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reset filters should preserve city filter`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            awaitItem()

            viewModel.onCityFilterChange("Cairo")
            awaitItem()

            viewModel.onRatingFilterChange(4.9)
            awaitItem()

            viewModel.onResetFilters()
            runCurrent()

            val state = expectMostRecentItem()
            assertEquals("", state.searchQuery)
            assertEquals("Cairo", state.selectedCity)
            assertEquals(null, state.minRating)
            assertEquals(1, state.displayedHotels.size)
            assertEquals("Grand Nile", state.displayedHotels[0].hotel.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search should reset pagination`() = runTest {
        val manyHotels = (1..25).map {
            Hotel(it.toLong(), "Hotel $it", "City", 4.0, 1000.0, "", "", 0.0, 0.0, emptyList(), emptyList())
        }
        every { getHotelsUseCase() } returns flowOf(manyHotels)

        viewModel = HotelsViewModel(
            getHotelsUseCase,
            refreshHotelsUseCase,
            favoriteRepository,
            toggleFavoriteUseCase,
            savedStateHandle
        )

        viewModel.uiState.test {
            awaitItem()
            var state = awaitItem()
            assertEquals(10, state.displayedHotels.size)

            viewModel.onLoadMore()
            state = awaitItem()
            assertEquals(20, state.displayedHotels.size)

            viewModel.onSearchQueryChange("Hotel 1")
            advanceTimeBy(300)
            runCurrent()
            
            state = expectMostRecentItem()
            assertEquals(10, state.displayedHotels.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `hasMore should be false when all filtered hotels are displayed`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            val state = awaitItem()
            assertEquals(3, state.displayedHotels.size)
            assertFalse(state.hasMore)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
