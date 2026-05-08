package com.example.actividadretrofitroom.Features.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actividadretrofitroom.Core.NetworkMonitor
import com.example.actividadretrofitroom.Data.Repository.PaisRepository
import com.example.actividadretrofitroom.Domain.CountryListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val countries: List<CountryListItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
}

enum class SortOption(val label: String) {
    NAME("Nombre"), POPULATION("Población"), AREA("Área")
}

data class HomeScreenState(
    val uiState: HomeUiState = HomeUiState.Loading,
    val searchQuery: String = "",
    val selectedRegion: String? = null,
    val selectedSubregion: String? = null,
    val availableSubregions: List<String> = emptyList(),
    val isOnline: Boolean = true,
    val selectedSort: SortOption = SortOption.NAME,
    val selectedLanguage: String? = null,
    val availableLanguages: List<String> = emptyList(),
    val isLoadingNextPage: Boolean = false,
    val canLoadMore: Boolean = false
)

private const val SEARCH_DEBOUNCE_MS = 300L
val REGIONS = listOf("Africa", "Americas", "Asia", "Europe", "Oceania", "Antarctic")

@OptIn(FlowPreview::class)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val paisRepository: PaisRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _allRawCountries = mutableListOf<CountryListItem>()
    private val _searchQueryFlow = MutableStateFlow("")

    init {
        observeConnectivity()
        observeSearchQuery()
        loadCountries(reset = true)
    }

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
        applyFiltersAndSort() // Filtrado instantáneo sobre los datos cargados
    }

    fun onSortOptionSelected(option: SortOption) {
        _state.update { it.copy(selectedSort = option) }
        applyFiltersAndSort()
    }

    fun onLanguageSelected(language: String?) {
        _state.update { it.copy(selectedLanguage = language) }
        applyFiltersAndSort()
    }

    fun refresh() = loadCountries(reset = true)

    private fun observeConnectivity() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                val wasOffline = !_state.value.isOnline
                _state.update { it.copy(isOnline = isOnline) }
                // Recargar si vuelve el internet y hubo un error previo
                if (wasOffline && isOnline && _state.value.uiState is HomeUiState.Error) {
                    refresh()
                }
            }
        }
    }

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
            if (reset) _state.update { it.copy(uiState = HomeUiState.Loading) }

            val current = _state.value
            val result = when {
                current.searchQuery.isNotBlank() -> paisRepository.searchCountries(current.searchQuery.trim())
                current.selectedRegion != null -> paisRepository.getCountriesByRegion(current.selectedRegion)
                else -> paisRepository.getAllCountries()
            }

            result.onSuccess { countries ->
                _allRawCountries.clear()
                _allRawCountries.addAll(countries)
                
                // Extracción dinámica de filtros (Vital para el funcionamiento Offline)
                val subregions = countries.mapNotNull { it.subregion }.distinct().sorted()
                val languages = countries.flatMap { it.languages }.distinct().sorted()
                
                _state.update { it.copy(
                    availableSubregions = subregions,
                    availableLanguages = languages
                ) }
                applyFiltersAndSort()
            }.onFailure { error ->
                _state.update { it.copy(uiState = HomeUiState.Error(error.message ?: "Sin conexión")) }
            }
        }
    }

    private fun applyFiltersAndSort() {
        val current = _state.value
        
        var filtered = _allRawCountries.filter { country ->
            val matchesSubregion = current.selectedSubregion == null || country.subregion == current.selectedSubregion
            val matchesLanguage = current.selectedLanguage == null || country.languages.contains(current.selectedLanguage)
            matchesSubregion && matchesLanguage
        }

        filtered = when (current.selectedSort) {
            SortOption.NAME -> filtered.sortedBy { it.name }
            SortOption.POPULATION -> filtered.sortedByDescending { it.population }
            SortOption.AREA -> filtered.sortedByDescending { it.area }
        }

        _state.update {
            it.copy(uiState = if (filtered.isEmpty()) HomeUiState.Empty else HomeUiState.Success(filtered))
        }
    }
    
    fun loadNextPage() {} // No aplica para esta implementación
}
