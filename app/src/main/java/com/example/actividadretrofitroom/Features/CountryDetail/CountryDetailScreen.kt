package com.example.actividadretrofitroom.Features.CountryDetail

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.actividadretrofitroom.Domain.CountryDetail
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    onBack: () -> Unit,
    viewModel: CountryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del País") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DetailUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCountryDetail() }) {
                            Text("Reintentar")
                        }
                    }
                }
                is DetailUiState.Success -> {
                    CountryDetailContent(state.country)
                }
            }
        }
    }
}

@Composable
private fun CountryDetailContent(country: CountryDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bandera y Nombres
        item {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "Bandera de ${country.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = country.officialName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Información General
        item {
            DetailSection("Información General") {
                DetailItem("Capital", country.capital)
                DetailItem("Región", country.region)
                DetailItem("Subregión", country.subregion)
                DetailItem("Continentes", country.continents.joinToString(", "))
                DetailItem("Población", formatNumber(country.population))
                DetailItem("Área", "${formatNumber(country.area.toLong())} km²")
            }
        }

        // Idiomas
        if (country.languages.isNotEmpty()) {
            item {
                DetailSection("Idiomas") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        country.languages.forEach { language ->
                            SuggestionChip(onClick = {}, label = { Text(language) })
                        }
                    }
                }
            }
        }

        // Monedas
        if (country.currencies.isNotEmpty()) {
            item {
                DetailSection("Monedas") {
                    country.currencies.forEach { (code, name) ->
                        DetailItem(code, name)
                    }
                }
            }
        }

        // Fronteras
        if (country.borders.isNotEmpty()) {
            item {
                DetailSection("Fronteras") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        country.borders.forEach { border ->
                            AssistChip(
                                onClick = { /* Podrías navegar a este país si quisieras */ },
                                label = { Text(border) }
                            )
                        }
                    }
                }
            }
        }

        // Códigos
        item {
            DetailSection("Identificadores") {
                DetailItem("Código CCA3", country.cca3)
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium)
        Text(text = value, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatNumber(number: Long): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}