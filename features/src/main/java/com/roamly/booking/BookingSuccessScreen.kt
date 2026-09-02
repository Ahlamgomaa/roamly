package com.roamly.booking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roamly.booking.components.SuccessDetailRow
import com.roamly.features.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingSuccessScreen(
    bookingReference: String,
    hotelName: String,
    checkIn: Long,
    checkOut: Long,
    rooms: Int,
    totalPrice: Double,
    onDoneClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.booking_confirmed),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SuccessDetailRow(label = stringResource(R.string.booking_reference), value = bookingReference, isPrimary = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SuccessDetailRow(label = stringResource(R.string.hotel), value = hotelName)
                    SuccessDetailRow(label = stringResource(R.string.check_in), value = formatDate(checkIn * 86400000L))
                    SuccessDetailRow(label = stringResource(R.string.check_out), value = formatDate(checkOut * 86400000L))
                    SuccessDetailRow(label = stringResource(R.string.rooms), value = rooms.toString())
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    SuccessDetailRow(label = stringResource(R.string.total_paid), value = "${totalPrice.toInt()} EGP", isBold = true)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_to_hotels))
            }
        }
    }
}
private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}