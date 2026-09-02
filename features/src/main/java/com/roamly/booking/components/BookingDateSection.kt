package com.roamly.booking.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roamly.features.R

@Composable
fun BookingDateSection(
    checkIn: Long?,
    checkOut: Long?,
    onCheckInClick: () -> Unit,
    onCheckOutClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        DateItem(
            label = stringResource(R.string.check_in),
            date = checkIn,
            onClick = onCheckInClick,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        DateItem(
            label = stringResource(R.string.check_out),
            date = checkOut,
            onClick = onCheckOutClick,
            modifier = Modifier.weight(1f)
        )
    }
}