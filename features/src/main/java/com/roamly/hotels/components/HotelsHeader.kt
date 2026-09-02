package com.roamly.hotels.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.roamly.features.R

@Composable
fun HotelsHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    availableCities: List<String>,
    selectedCity: String?,
    onCitySelected: (String?) -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Roamly",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.find_your_perfect_stay),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        HotelSearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onFilterClick = onFilterClick
        )
        Spacer(modifier = Modifier.height(8.dp))
        CityFilterRow(
            cities = availableCities,
            selectedCity = selectedCity,
            onCitySelected = onCitySelected
        )
    }
}
