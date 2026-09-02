package com.roamly.hotels.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.roamly.features.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityFilterRow(
    cities: List<String>,
    selectedCity: String?,
    onCitySelected: (String?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            FilterChip(
                selected = selectedCity == null,
                onClick = { onCitySelected(null) },
                label = { Text(stringResource(R.string.all_cities)) }
            )
        }
        items(cities) { city ->
            FilterChip(
                selected = selectedCity == city,
                onClick = { onCitySelected(city) },
                label = { Text(city) }
            )
        }
    }
}

