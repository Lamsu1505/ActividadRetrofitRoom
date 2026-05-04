package com.example.actividadretrofitroom.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.actividadretrofitroom.core.connectivity.ConnectivityObserver
import com.example.actividadretrofitroom.domain.model.Country
import com.example.actividadretrofitroom.domain.repository.CountryRepository
import com.example.actividadretrofitroom.ui.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CountryDetailState(
    val country: Country? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: CountryRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {
    private val cca3: String = checkNotNull(savedStateHandle[Route.Detail.ARG])

    val isOnline: StateFlow<Boolean> =
        connectivityObserver.isOnline.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val state: StateFlow<CountryDetailState> =
        repo.observeCountry(cca3)
            .map { CountryDetailState(country = it, errorMessage = null) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CountryDetailState())

    init {
        refresh()
    }

    fun refresh() {
        if (!isOnline.value) return
        viewModelScope.launch {
            runCatching { repo.refreshDetail(cca3) }
                .onFailure { /* UI already has cached DB; keep silent */ }
        }
    }
}

