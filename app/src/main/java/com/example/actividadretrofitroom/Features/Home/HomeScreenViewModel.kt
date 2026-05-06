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

/** Representa todos los posibles estados de la pantalla principal. */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val countries: List<CountryListItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
    data object Empty : HomeUiState
}

/** Estado completo de la UI expuesto al Composable. */
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
)


private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 300L

val REGIONS = listOf("Africa", "Americas", "Asia", "Europe", "Oceania", "Antarctic")

// ─────────────────────────────────────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(FlowPreview::class)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val paisRepository: PaisRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    private val _allLoadedCountries = mutableListOf<CountryListItem>()

    /** Flow interno del query para aplicar debounce antes de buscar. */
    private val _searchQueryFlow = MutableStateFlow("")

    // ── Inicialización ────────────────────────────────────────────────────────

    init {
        observeConnectivity()
        observeSearchQuery()
        loadCountries(reset = true)
    }

    // ── API pública ──────────────────────────────────────────────────────────

    /** Llamado cuando el usuario escribe en la barra de búsqueda. */
    fun onSearchQueryChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }

    /** Llamado cuando el usuario selecciona o deselecciona una región. */
    fun onRegionSelected(region: String?) {
        _state.update {
            it.copy(
                selectedRegion = region,
                selectedSubregion = null,
                availableSubregions = emptyList(),
            )
        }
        loadCountries(reset = true)
    }

    /** Llamado cuando el usuario selecciona o deselecciona una subregión. */
    fun onSubregionSelected(subregion: String?) {
        _state.update { it.copy(selectedSubregion = subregion) }
        loadCountries(reset = true)
    }

    /**
     * Carga la siguiente página cuando el usuario llega al final de la lista.
     * No hace nada si ya se está cargando o no hay más datos.
     */
    fun loadNextPage() {
        val current = _state.value
        if (current.isLoadingNextPage || !current.canLoadMore) return
        loadCountries(reset = false)
    }

    /** Refresca la lista desde cero (pull-to-refresh o botón reintentar). */
    fun refresh() {
        loadCountries(reset = true)
    }

    // ── Lógica interna ───────────────────────────────────────────────────────

    /**
     * Observa el Flow de conectividad expuesto por el repositorio.
     * Actualiza [HomeScreenState.isOnline] en tiempo real.
     */
    private fun observeConnectivity() {
        viewModelScope.launch {
//            repository.observeConnectivity().collect { isOnline ->
//                val wasOffline = !_state.value.isOnline
//                _state.update { it.copy(isOnline = isOnline) }
//                // Si recuperamos conexión, refresca datos
//                if (wasOffline && isOnline) {
//                    loadCountries(reset = true)
//                }
//            }
        }
    }

    /**
     * Escucha el Flow de búsqueda con debounce para no llamar a la API
     * en cada pulsación de teclado.
     */
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQueryFlow
                .debounce(SEARCH_DEBOUNCE_MS)
                .distinctUntilChanged()
                .collect { loadCountries(reset = true) }
        }
    }

    /**
     * Ejecuta la carga de países según los filtros activos.
     *
     * @param reset Si es `true`, reinicia la paginación desde la página 1.
     */
    private fun loadCountries(reset: Boolean) {
        viewModelScope.launch {
            val current = _state.value

            if (reset) {
                _allLoadedCountries.clear()
                _state.update {
                    it.copy(
                        uiState = HomeUiState.Loading,
                        currentPage = 1,
                        canLoadMore = true,
                    )
                }
            } else {
                _state.update { it.copy(isLoadingNextPage = true) }
            }

            // Llamada al repositorio
            val result = if (current.selectedRegion != null) {
                paisRepository.getCountriesByRegion(current.selectedRegion)
            } else {
                paisRepository.getAllCountries()
            }

            result.onSuccess { allCountries ->
                // Filtrado local para búsqueda (ya que la API de búsqueda es distinta)
                val filtered = if (current.searchQuery.isNotBlank()) {
                    allCountries.filter {
                        it.name.contains(current.searchQuery, ignoreCase = true) ||
                                it.cca3.contains(current.searchQuery, ignoreCase = true)
                    }
                } else allCountries

                _allLoadedCountries.addAll(filtered)

                _state.update {
                    it.copy(
                        uiState = if (_allLoadedCountries.isEmpty()) HomeUiState.Empty else HomeUiState.Success(_allLoadedCountries.toList()),
                        isLoadingNextPage = false,
                        canLoadMore = false // REST Countries suele devolver todo de golpe
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        uiState = HomeUiState.Error(error.message ?: "Error al cargar países"),
                        isLoadingNextPage = false
                    )
                }
            }
        }
    }
}