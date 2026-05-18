package com.allterra.presentation.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TripsUiState(
    val uiState: UiState<List<TripUiModel>> = UiState.Loading,
    val items: List<TripUiModel> = emptyList()
)

class TripViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TripsUiState())
    val state: StateFlow<TripsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading) }
            when (val result = repository.getMyTrips()) {
                is ApiResult.Success -> {
                    _state.update { it.copy(uiState = UiState.Success(result.data), items = result.data) }
                }
                else -> {
                    _state.update { it.copy(uiState = UiState.Error("Failed to load trips")) }
                }
            }
        }
    }

    fun deleteTrip(id: String) {
        viewModelScope.launch {
            repository.deleteTrip(id)
            refresh()
        }
    }
}
