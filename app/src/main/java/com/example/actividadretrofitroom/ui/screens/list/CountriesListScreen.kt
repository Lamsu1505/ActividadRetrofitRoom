package com.example.actividadretrofitroom.ui.screens.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage
import com.example.actividadretrofitroom.domain.model.Country
import kotlinx.coroutines.flow.Flow

private val Regions = listOf("Africa", "Americas", "Antarctic", "Asia", "Europe", "Oceania")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesListScreen(
    state: CountriesListState,
    pagingFlow: Flow<androidx.paging.PagingData<Country>>,
    isOnline: Boolean,
    onRefresh: () -> Unit,
    onApplySearch: (String) -> Unit,
    onApplyRegion: (String?) -> Unit,
    onCountryClick: (String) -> Unit,
) {
    var query by remember(state.query) { mutableStateOf(state.query) }

    val items = pagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("REST Countries") },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text(if (isOnline) "Online" else "Offline") },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Buscar por nombre") },
                    singleLine = true,
                )
                Button(onClick = { onApplySearch(query) }) { Text("Buscar") }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Región:", style = MaterialTheme.typography.labelLarge)
                Regions.forEach { r ->
                    AssistChip(
                        onClick = { onApplyRegion(r) },
                        label = { Text(r) },
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                AssistChip(onClick = { onApplyRegion(null) }, label = { Text("Todas") })
            }

            if (state.errorMessage != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(state.errorMessage, color = MaterialTheme.colorScheme.onErrorContainer)
                        Button(onClick = onRefresh) { Text("Reintentar") }
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxSize()) {
                items(items.itemCount) { idx ->
                    val c = items[idx] ?: return@items
                    CountryRow(country = c, onClick = { onCountryClick(c.cca3) })
                }
            }
        }
    }
}

@Composable
private fun CountryRow(country: Country, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = country.flagPng,
                contentDescription = country.flagAlt ?: "Bandera",
                modifier = Modifier.size(48.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(country.nameCommon, fontWeight = FontWeight.SemiBold)
                Text(
                    listOfNotNull(country.region, country.subregion).joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(country.cca3, style = MaterialTheme.typography.labelLarge)
        }
    }
}

