package com.example.actividadretrofitroom.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.actividadretrofitroom.core.connectivity.ConnectivityObserver
import com.example.actividadretrofitroom.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CountriesListState(
    val query: String = "",
    val region: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class CountriesListViewModel @Inject constructor(
    private val repo: CountryRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    val isOnline: StateFlow<Boolean> =
        connectivityObserver.isOnline.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val filters = MutableStateFlow("" to null as String?)

    private val _state = MutableStateFlow(CountriesListState())
    val state: StateFlow<CountriesListState> = _state.asStateFlow()

    val pagingFlow =
        filters
            .flatMapLatest { (q, r) -> repo.pagedCountries(q.ifBlank { null }, r) }
            .cachedIn(viewModelScope)

    init {
        refreshAll()
    }

    fun applySearch(query: String) {
        _state.value = _state.value.copy(query = query)
        filters.value = query to _state.value.region
        if (isOnline.value) {
            viewModelScope.launch {
                runCatching { repo.refreshByName(query) }
                    .onFailure { _state.value = _state.value.copy(errorMessage = it.message ?: "Error de red") }
            }
        }
    }

    fun applyRegion(region: String?) {
        _state.value = _state.value.copy(region = region)
        filters.value = _state.value.query to region
        if (!region.isNullOrBlank() && isOnline.value) {
            viewModelScope.launch {
                runCatching { repo.refreshByRegion(region) }
                    .onFailure { _state.value = _state.value.copy(errorMessage = it.message ?: "Error de red") }
            }
        }
    }

    fun refreshAll() {
        if (!isOnline.value) return
        viewModelScope.launch {
            runCatching { repo.refreshAll() }
                .onFailure { _state.value = _state.value.copy(errorMessage = it.message ?: "Error de red") }
        }
    }
}

