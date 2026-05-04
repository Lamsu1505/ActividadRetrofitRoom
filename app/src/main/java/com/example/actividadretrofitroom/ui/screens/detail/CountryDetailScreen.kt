package com.example.actividadretrofitroom.ui.screens.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    state: androidx.compose.runtime.State<CountryDetailState>,
    isOnline: androidx.compose.runtime.State<Boolean>,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    val s = state.value
    val online = isOnline.value
    val country = s.country

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(country?.nameCommon ?: "Detalle") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } },
                actions = { AssistChip(onClick = {}, label = { Text(if (online) "Online" else "Offline") }) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (country == null) {
                Text("No hay datos del país (aún).", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onRetry) { Text("Reintentar") }
                return@Column
            }

            Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AsyncImage(
                        model = country.flagPng,
                        contentDescription = country.flagAlt ?: "Bandera",
                        modifier = Modifier.size(72.dp),
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(country.nameCommon, fontWeight = FontWeight.SemiBold)
                        Text(country.nameOfficial ?: "", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(country.cca3, style = MaterialTheme.typography.labelLarge)
                }
            }

            InfoRow("Región", listOfNotNull(country.region, country.subregion).joinToString(" • "))
            InfoRow("Capital", country.capitals.joinToString(", ").ifBlank { "-" })
            InfoRow("Población", country.population?.toString() ?: "-")
            InfoRow("Área", country.area?.toString() ?: "-")
            InfoRow("Idiomas", country.languages.values.joinToString(", ").ifBlank { "-" })
            InfoRow("Monedas", country.currencies.values.joinToString(", ").ifBlank { "-" })
            InfoRow("Zonas horarias", country.timezones.joinToString(", ").ifBlank { "-" })
            InfoRow("Lat/Lng", country.latLng?.let { "${it.first}, ${it.second}" } ?: "-")

            Spacer(modifier = Modifier.size(12.dp))
            Button(onClick = onRetry, enabled = online) { Text("Actualizar detalle") }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

