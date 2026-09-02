package com.roamly.hotels.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roamly.features.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelFilterSheet(
    initialMinRating: Double?,
    initialMinPrice: Double?,
    initialMaxPrice: Double?,
    onApply: (Double?, Double?, Double?) -> Unit,
    onDismiss: () -> Unit
) {
    var tempRating by remember { mutableStateOf(initialMinRating) }
    var tempMinPrice by remember { mutableStateOf(initialMinPrice) }
    var tempMaxPrice by remember { mutableStateOf(initialMaxPrice) }

    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.filters),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.min_rating),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null, 4.0, 4.5, 4.8).forEach { rating ->
                    FilterChip(
                        selected = tempRating == rating,
                        onClick = { tempRating = rating },
                        label = {
                            Text(
                                when (rating) {
                                    null -> stringResource(R.string.any)
                                    4.0 -> stringResource(R.string.rating_4_0)
                                    4.5 -> stringResource(R.string.rating_4_5)
                                    4.8 -> stringResource(R.string.rating_4_8)
                                    else -> rating.toString()
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.price_range),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = tempMinPrice?.toInt()?.toString() ?: "",
                    onValueChange = { tempMinPrice = it.toDoubleOrNull() },
                    label = { Text(stringResource(R.string.min_price)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tempMaxPrice?.toInt()?.toString() ?: "",
                    onValueChange = { tempMaxPrice = it.toDoubleOrNull() },
                    label = { Text(stringResource(R.string.max_price)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = {
                        tempRating = null
                        tempMinPrice = null
                        tempMaxPrice = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.reset))
                }
                Button(
                    onClick = { onApply(tempRating, tempMinPrice, tempMaxPrice) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}