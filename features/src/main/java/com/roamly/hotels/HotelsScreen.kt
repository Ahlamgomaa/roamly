package com.roamly.hotels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roamly.features.R
import com.roamly.hotels.components.CachedDataBanner
import com.roamly.hotels.components.HotelCard
import com.roamly.hotels.components.HotelEmptyState
import com.roamly.hotels.components.HotelErrorState
import com.roamly.hotels.components.HotelFilterSheet
import com.roamly.hotels.components.HotelsHeader
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun HotelsRoot(
    onHotelClick: (Long) -> Unit,
    viewModel: HotelsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HotelsScreen(
        state = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onCityFilterChange = viewModel::onCityFilterChange,
        onRatingFilterChange = viewModel::onRatingFilterChange,
        onPriceRangeChange = viewModel::onPriceRangeChange,
        onLoadMore = viewModel::onLoadMore,
        onRefresh = viewModel::refresh,
        onResetFilters = viewModel::onResetFilters,
        onHotelClick = onHotelClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelsScreen(
    state: HotelsUiState,
    onSearchQueryChange: (String) -> Unit,
    onCityFilterChange: (String?) -> Unit,
    onRatingFilterChange: (Double?) -> Unit,
    onPriceRangeChange: (Double?, Double?) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onResetFilters: () -> Unit,
    onHotelClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }
    LaunchedEffect(listState, state.displayedHotels.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex to totalItemsNumber
        }
            .distinctUntilChanged()
            .collect { (lastIndex, total) ->
                if (total > 0 && lastIndex >= total - 3) {
                    onLoadMore()
                }
            }
    }

    Scaffold(
        topBar = {
            HotelsHeader(
                searchQuery = state.searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                availableCities = state.availableCities,
                selectedCity = state.selectedCity,
                onCitySelected = onCityFilterChange,
                onFilterClick = { showFilterSheet = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.displayedHotels.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null && state.displayedHotels.isEmpty() -> {
                    HotelErrorState(
                        message = state.errorMessage,
                        onRetry = onRefresh
                    )
                }
                state.displayedHotels.isEmpty() -> {
                    HotelEmptyState(onReset = onResetFilters)
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.popular_stays),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        if (state.isCached) {
                            item {
                                CachedDataBanner()
                            }
                        }

                        items(
                            items = state.displayedHotels,
                            key = { it.id }
                        ) { hotel ->
                            HotelCard(
                                hotel = hotel,
                                onClick = { onHotelClick(hotel.id) }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        HotelFilterSheet(
            initialMinRating = state.minRating,
            initialMinPrice = state.minPrice,
            initialMaxPrice = state.maxPrice,
            onApply = { rating, min, max ->
                onRatingFilterChange(rating)
                onPriceRangeChange(min, max)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
}
