package com.example.actividadretrofitroom.Features.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actividadretrofitroom.Data.Repository.PaisRepository
import com.example.actividadretrofitroom.Domain.CountryListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─────────────────────────────────────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────────────────────────────────────

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val countries: List<CountryListItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
}

enum class SortOption(val label: String) {
    NAME("Nombre"),
    POPULATION("Población"),
    AREA("Área")
}

data class HomeScreenState(
    val uiState: HomeUiState = HomeUiState.Loading,
    val searchQuery: String = "",
    val selectedRegion: String? = null,
    val selectedSubregion: String? = null,
    val availableSubregions: List<String> = emptyList(),
    val isOnline: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val canLoadMore: Boolean = true,
    val currentPage: Int = 1,
    val selectedSort: SortOption = SortOption.NAME,
    val selectedLanguage: String? = null,
    val availableLanguages: List<String> = emptyList(),
)

private const val SEARCH_DEBOUNCE_MS = 300L
val REGIONS = listOf("Africa", "Americas", "Asia", "Europe", "Oceania", "Antarctic")

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val paisRepository: PaisRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _allRawCountries = mutableListOf<CountryListItem>()
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
        loadCountries(reset = true)
    }

    // ── API Pública ──────────────────────────────────────────────────────────

    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }

    fun onRegionSelected(region: String?) {
        _state.update { it.copy(selectedRegion = region, selectedSubregion = null) }
        loadCountries(reset = true)
    }

    fun onSubregionSelected(subregion: String?) {
        _state.update { it.copy(selectedSubregion = subregion) }
        loadCountries(reset = true)
    }

    fun onSortOptionSelected(option: SortOption) {
        _state.update { it.copy(selectedSort = option) }
        applyFiltersAndSort()
    }

    fun onLanguageSelected(language: String?) {
        _state.update { it.copy(selectedLanguage = language) }
        applyFiltersAndSort()
    }

    fun loadNextPage() { /* REST Countries no requiere paginación tradicional */ }

    fun refresh() = loadCountries(reset = true)

    // ── Lógica Interna ───────────────────────────────────────────────────────

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { loadCountries(reset = true) }
        }
    }

    private fun loadCountries(reset: Boolean) {
        viewModelScope.launch {
            val current = _state.value
            if (reset) {
                _state.update { it.copy(uiState = HomeUiState.Loading) }
            }

            val result = when {
                current.searchQuery.isNotBlank() -> paisRepository.searchCountries(current.searchQuery.trim())
                current.selectedRegion != null -> paisRepository.getCountriesByRegion(current.selectedRegion)
                else -> paisRepository.getAllCountries()
            }

            result.onSuccess { countries ->
                _allRawCountries.clear()
                _allRawCountries.addAll(countries)
                
                // Extraer idiomas únicos
                val languages = countries.flatMap { it.languages }.distinct().sorted()
                
                _state.update { it.copy(availableLanguages = languages) }
                applyFiltersAndSort()
            }.onFailure { error ->
                _state.update { it.copy(uiState = HomeUiState.Error(error.message ?: "Error de red")) }
            }
        }
    }

    private fun applyFiltersAndSort() {
        val current = _state.value
        
        // 1. Filtrar por Subregión e Idioma
        var filtered = _allRawCountries.filter { country ->
            val matchesSubregion = current.selectedSubregion == null || country.subregion == current.selectedSubregion
            val matchesLanguage = current.selectedLanguage == null || country.languages.contains(current.selectedLanguage)
            matchesSubregion && matchesLanguage
        }

        // 2. Ordenar
        filtered = when (current.selectedSort) {
            SortOption.NAME -> filtered.sortedBy { it.name }
            SortOption.POPULATION -> filtered.sortedByDescending { it.population }
            SortOption.AREA -> filtered.sortedByDescending { it.area }
        }

        _state.update {
            it.copy(
                uiState = if (filtered.isEmpty()) HomeUiState.Empty else HomeUiState.Success(filtered)
            )
        }
    }
}
