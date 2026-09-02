package com.roamly.booking


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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.roamly.booking.components.BookingDatePickerDialog
import com.roamly.booking.components.BookingDateSection
import com.roamly.booking.components.PriceSummarySection
import com.roamly.booking.components.RoomSelector
import com.roamly.features.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingRoot(
    hotelId: Long,
    onBackClick: () -> Unit,
    onBookingConfirmed: (String, String, Long, Long, Int, Double) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    LaunchedEffect(hotelId) {
        viewModel.setHotelId(hotelId)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.bookingReference != null && state.hotel != null && state.checkIn != null && state.checkOut != null && state.priceBreakdown != null) {
        LaunchedEffect(state.bookingReference) {
            onBookingConfirmed(
                state.bookingReference!!,
                state.hotel!!.name,
                state.checkIn!!,
                state.checkOut!!,
                state.rooms,
                state.priceBreakdown!!.totalPrice
            )
        }
    }

    BookingScreen(
        state = state,
        onBackClick = onBackClick,
        onCheckInSelected = viewModel::onCheckInSelected,
        onCheckOutSelected = viewModel::onCheckOutSelected,
        onRoomsChanged = viewModel::onRoomsChanged,
        onConfirmClick = viewModel::confirmBooking
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    state: BookingUiState,
    onBackClick: () -> Unit,
    onCheckInSelected: (Long) -> Unit,
    onCheckOutSelected: (Long) -> Unit,
    onRoomsChanged: (Int) -> Unit,
    onConfirmClick: () -> Unit
) {
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.booking)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirmClick,
                enabled = state.isConfirmEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.confirm_booking))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            state.hotel?.let { hotel ->
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = hotel.city,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(24.dp))

                BookingDateSection(
                    checkIn = state.checkIn,
                    checkOut = state.checkOut,
                    onCheckInClick = { showCheckInPicker = true },
                    onCheckOutClick = { showCheckOutPicker = true }
                )

                if (state.dateValidation != BookingDateValidation.Valid) {
                    Text(
                        text = when (state.dateValidation) {
                            BookingDateValidation.CheckInInPast -> stringResource(R.string.checkin_in_past)
                            BookingDateValidation.CheckOutBeforeCheckIn -> stringResource(R.string.checkout_before_checkin)
                            BookingDateValidation.CheckOutIsCheckIn -> stringResource(R.string.checkout_is_checkin)
                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                RoomSelector(
                    rooms = state.rooms,
                    onRoomsChanged = onRoomsChanged
                )

                Spacer(modifier = Modifier.height(24.dp))

                state.priceBreakdown?.let { breakdown ->
                    PriceSummarySection(breakdown, state.nights, state.rooms, hotel.pricePerNight)
                }
            }
        }
    }

    if (showCheckInPicker) {
        BookingDatePickerDialog(
            onDateSelected = {
                onCheckInSelected(it / 86400000L)
                showCheckInPicker = false
            },
            onDismiss = { showCheckInPicker = false }
        )
    }

    if (showCheckOutPicker) {
        BookingDatePickerDialog(
            onDateSelected = {
                onCheckOutSelected(it / 86400000L)
                showCheckOutPicker = false
            },
            onDismiss = { showCheckOutPicker = false }
        )
    }
}


