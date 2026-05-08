package com.example.actividadretrofitroom.Features.Home


import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.actividadretrofitroom.Domain.CountryListItem
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla principal
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Pantalla principal de la aplicación.
 * Muestra la lista paginada de países con búsqueda, filtros y estado de conexión.
 *
 * @param onCountryClick Callback que recibe el código `cca3` del país seleccionado.
 * @param viewModel ViewModel inyectado automáticamente por Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCountryClick: (cca3: String) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    // Detecta cuando el usuario llega al penúltimo elemento → carga siguiente página
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val total = listState.layoutInfo.totalItemsCount
            lastVisible >= total - 2 && total > 0
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadNextPage()
    }

    Scaffold(
        topBar = {
            Column {
                CountriesTopAppBar(isOnline = state.isOnline)
                SearchBarComponent(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
                
                // Fila de Filtros (Región y Subregión)
                RegionFilterRow(
                    selectedRegion = state.selectedRegion,
                    onRegionSelected = viewModel::onRegionSelected,
                )
                
                AnimatedVisibility(visible = state.selectedRegion != null && state.availableSubregions.isNotEmpty()) {
                    SubregionFilterRow(
                        subregions = state.availableSubregions,
                        selectedSubregion = state.selectedSubregion,
                        onSubregionSelected = viewModel::onSubregionSelected,
                    )
                }

                // NUEVA Fila de Filtros Adicionales (Orden e Idioma)
                AdditionalFiltersRow(
                    selectedSort = state.selectedSort,
                    onSortSelected = viewModel::onSortOptionSelected,
                    languages = state.availableLanguages,
                    selectedLanguage = state.selectedLanguage,
                    onLanguageSelected = viewModel::onLanguageSelected
                )

                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
        },
        bottomBar = {
            ConnectionStatusBanner(isOnline = state.isOnline)
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (val uiState = state.uiState) {

                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                is HomeUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        onRetry = viewModel::refresh,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is HomeUiState.Empty -> {
                    EmptyState(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                is HomeUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = uiState.countries,
                            key = { it.cca3 },
                        ) { country ->
                            CountryCard(
                                country = country,
                                onClick = { onCountryClick(country.cca3) },
                            )
                        }

                        // Footer de paginación
                        if (state.isLoadingNextPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        strokeWidth = 2.5.dp,
                                    )
                                }
                            }
                        }

                        if (!state.canLoadMore && uiState.countries.isNotEmpty()) {
                            item {
                                Text(
                                    text = "• ${uiState.countries.size} países en total •",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Componentes Adicionales de Filtro
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun AdditionalFiltersRow(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    languages: List<String>,
    selectedLanguage: String?,
    onLanguageSelected: (String?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Selector de Orden
        item {
            SortOptionChip(selectedSort, onSortSelected)
        }

        // Separador visual si hay idiomas
        if (languages.isNotEmpty()) {
            item {
                VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 4.dp))
            }

            item {
                FilterChip(
                    selected = selectedLanguage == null,
                    onClick = { onLanguageSelected(null) },
                    label = { Text("Idiomas: Todos") },
                    leadingIcon = { if (selectedLanguage == null) Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp)) }
                )
            }

            items(languages) { language ->
                FilterChip(
                    selected = selectedLanguage == language,
                    onClick = { onLanguageSelected(if (selectedLanguage == language) null else language) },
                    label = { Text(language) }
                )
            }
        }
    }
}

@Composable
private fun SortOptionChip(
    selectedSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        AssistChip(
            onClick = { expanded = true },
            label = { Text("Ordenar por: ${selectedSort.label}") },
            leadingIcon = { Icon(Icons.Default.Sort, null, modifier = Modifier.size(18.dp)) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSortSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// TopAppBar
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CountriesTopAppBar(isOnline: Boolean) {
    TopAppBar(
        title = {
            Text(
                text = "Países del Mundo",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            )
        },
        actions = {
            Icon(
                imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                contentDescription = if (isOnline) "Conectado" else "Sin conexión",
                tint = if (isOnline) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 16.dp),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// SearchBar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SearchBarComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Buscar país...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar búsqueda",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Filtro de Regiones
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun RegionFilterRow(
    selectedRegion: String?,
    onRegionSelected: (String?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedRegion == null,
                onClick = { onRegionSelected(null) },
                label = { Text("Todas") },
            )
        }
        items(REGIONS) { region ->
            FilterChip(
                selected = selectedRegion == region,
                onClick = {
                    onRegionSelected(if (selectedRegion == region) null else region)
                },
                label = { Text(region) },
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Filtro de Subregiones
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SubregionFilterRow(
    subregions: List<String>,
    selectedSubregion: String?,
    onSubregionSelected: (String?) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            FilterChip(
                selected = selectedSubregion == null,
                onClick = { onSubregionSelected(null) },
                label = { Text("Todas") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
        }
        items(subregions) { subregion ->
            FilterChip(
                selected = selectedSubregion == subregion,
                onClick = {
                    onSubregionSelected(if (selectedSubregion == subregion) null else subregion)
                },
                label = { Text(subregion) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CountryCard
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CountryCard(
    country: CountryListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Bandera
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "Bandera de ${country.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 72.dp, height = 50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            // Información
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = country.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                country.capital?.let {
                    Text(
                        text = "🏛 $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RegionBadge(region = country.region)
                    Text(
                        text = "👥 ${formatPopulation(country.population)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RegionBadge(region: String) {
    val (backgroundColor, textColor) = when (region) {
        "Africa"    -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "Americas"  -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "Asia"      -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "Europe"    -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        else        -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(
            text = region,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = textColor,
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Banner de conexión
// ─────────────────────────────────────────────────────────────────────────────

// ... (dentro de HomeScreen.kt)

// ─────────────────────────────────────────────────────────────────────────────
// Banner de conexión
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ConnectionStatusBanner(isOnline: Boolean) {
    // Definimos los colores según el estado solicitado
    val bannerColor = if (isOnline) Color(0xFF2E7D32) else Color(0xFFC62828)
    val statusText = if (isOnline) "Dispositivo conectado" else "Sin conexión — modo offline"
    val icon = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff

    Surface(
        color = bannerColor,
        contentColor = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Estados vacío y error
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "🌍", style = MaterialTheme.typography.displayMedium)
        Text(
            text = "No se encontró el país",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "Intenta con otro término de búsqueda o cambia los filtros.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "⚠️", style = MaterialTheme.typography.displayMedium)
        Text(
            text = "Algo salió mal",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utilidades
// ─────────────────────────────────────────────────────────────────────────────

/** Formatea un número de población para mostrarlo de forma legible. */
private fun formatPopulation(population: Long): String = when {
    population >= 1_000_000_000 -> "${"%.1f".format(population / 1_000_000_000.0)}B"
    population >= 1_000_000     -> "${"%.1f".format(population / 1_000_000.0)}M"
    population >= 1_000         -> "${"%.0f".format(population / 1_000.0)}K"
    else                        -> population.toString()
}
