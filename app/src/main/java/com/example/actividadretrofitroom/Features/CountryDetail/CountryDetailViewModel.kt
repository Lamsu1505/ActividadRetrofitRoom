package com.example.actividadretrofitroom.Features.CountryDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.actividadretrofitroom.Core.AppRoutes
import com.example.actividadretrofitroom.Data.Repository.PaisRepository
import com.example.actividadretrofitroom.Domain.CountryDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Representa los estados de la pantalla de detalle.
 */
sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val country: CountryDetail) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

@HiltViewModel
class CountryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val paisRepository: PaisRepository
) : ViewModel() {

    // Extraemos los argumentos de la ruta. Usamos el nombre del parámetro definido en AppRoutes.CountryDetail
    private val route = savedStateHandle.toRoute<AppRoutes.CountryDetail>()
    val countryCode: String = route.cca3

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadCountryDetail()
    }

    fun loadCountryDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            paisRepository.getCountryByCode(countryCode)
                .onSuccess { detail ->
                    _uiState.value = DetailUiState.Success(detail)
                }
                .onFailure { error ->
                    _uiState.value = DetailUiState.Error(
                        error.message ?: "No se pudo cargar la información del país"
                    )
                }
        }
    }
}
