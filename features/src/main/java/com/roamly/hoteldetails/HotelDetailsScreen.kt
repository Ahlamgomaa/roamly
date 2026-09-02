package com.roamly.hoteldetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roamly.features.R
import com.roamly.hoteldetails.components.BookHotelButton
import com.roamly.hoteldetails.components.HotelAmenities
import com.roamly.hoteldetails.components.HotelImageGallery
import com.roamly.hoteldetails.components.HotelInfoSection
import com.roamly.hoteldetails.components.HotelLocationSection
import com.roamly.hoteldetails.components.HotelRating
import com.roamly.hotels.components.HotelErrorState

@Composable
fun HotelDetailsRoot(
    hotelId: Long,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit,
    viewModel: HotelDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(hotelId) {
        viewModel.setHotelId(hotelId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HotelDetailsScreen(
        state = uiState,
        onBackClick = onBackClick,
        onBookClick = onBookClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(
    state: HotelDetailsUiState,
    onBackClick: () -> Unit,
    onBookClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.hotel_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        bottomBar = {
            state.hotel?.let { hotel ->
                BookHotelButton(
                    pricePerNight = hotel.pricePerNight,
                    onBookClick = onBookClick
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null -> {
                    HotelErrorState(
                        message = state.errorMessage,
                        onRetry = { /* Retry logic if needed */ }
                    )
                }
                state.isNotFound -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.hotel_not_found),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        androidx.compose.material3.Button(onClick = onBackClick) {
                            Text(stringResource(R.string.back))
                        }
                    }
                }
                state.hotel != null -> {
                    val hotel = state.hotel
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        HotelImageGallery(
                            images = hotel.images,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                        )
                        Column(modifier = Modifier.padding(16.dp)) {
                            HotelRating(rating = hotel.rating)
                            Spacer(modifier = Modifier.height(8.dp))
                            HotelInfoSection(
                                name = hotel.name,
                                city = hotel.city,
                                description = hotel.description
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = stringResource(R.string.amenities),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HotelAmenities(amenities = hotel.amenities)
                            Spacer(modifier = Modifier.height(24.dp))
                            HotelLocationSection(address = hotel.address)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
