package com.roamly.booking.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roamly.booking.BookingPriceBreakdown
import com.roamly.features.R

@Composable
fun PriceSummarySection(
    breakdown: BookingPriceBreakdown,
    nights: Int,
    rooms: Int,
    pricePerNight: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.price_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            PriceRow(label = "${pricePerNight.toInt()} EGP x $nights nights x $rooms rooms", value = breakdown.basePrice)
            PriceRow(label = stringResource(R.string.vat), value = breakdown.vatAmount)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            PriceRow(
                label = stringResource(R.string.total_price),
                value = breakdown.totalPrice,
                isTotal = true
            )
        }
    }
}