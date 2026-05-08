package com.example.actividadretrofitroom.Features.CountryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
                title = { Text("Detalle", fontWeight = FontWeight.Bold) },
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
                    // Estado de error personalizado y profesional
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WifiOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "¡Ups! Problema de conexión",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message, // Muestra el mensaje del repositorio
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { viewModel.loadCountryDetail() },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text("Reintentar conexión")
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountryDetailContent(country: CountryDetail) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bandera y Cabecera
        item {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "Bandera de ${country.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = country.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = country.officialName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        // Secciones de Información
        item {
            DetailSection("Geografía e Información") {
                DetailItem("Capital", country.capital)
                DetailItem("Región", country.region)
                DetailItem("Subregión", country.subregion)
                DetailItem("Continentes", country.continents.joinToString(", "))
                DetailItem("Población", formatNumber(country.population))
                DetailItem("Área", "${formatNumber(country.area.toLong())} km²")
            }
        }

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

        if (country.currencies.isNotEmpty()) {
            item {
                DetailSection("Monedas") {
                    country.currencies.forEach { (code, name) ->
                        DetailItem(code, name)
                    }
                }
            }
        }

        if (country.borders.isNotEmpty()) {
            item {
                DetailSection("Países Fronterizos") {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        country.borders.forEach { border ->
                            AssistChip(onClick = { }, label = { Text(border) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp)
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatNumber(number: Long): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}
