package com.allterra.presentation.packing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.repository.PackingRepository
import kotlinx.coroutines.launch

class PackingViewModel(
    private val packingRepository: PackingRepository,
    private val tripId: String
) : ViewModel() {

    var state by mutableStateOf<UiState<List<PackingItemUiModel>>>(UiState.Loading)
        private set

    init {
        loadPackingList()
    }

    fun loadPackingList() {
        state = UiState.Loading
        viewModelScope.launch {
            val result = packingRepository.getPackingList(tripId)
            state = when (result) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.ValidationError -> UiState.Error(result.message)
                is ApiResult.Unauthorized -> UiState.Error(result.message)
                is ApiResult.Forbidden -> UiState.Error(result.message)
                is ApiResult.NotFound -> UiState.Error(result.message)
                is ApiResult.ServerError -> UiState.Error(result.message)
                is ApiResult.NetworkError -> UiState.Error(result.message)
                is ApiResult.UnknownError -> UiState.Error(result.message)
            }
        }
    }

    fun togglePackedStatus(gearId: String, currentPacked: Boolean) {
        val currentState = state
        if (currentState is UiState.Success) {
            val updatedList = currentState.data.map {
                if (it.gearId == gearId) it.copy(packed = !currentPacked) else it
            }
            state = UiState.Success(updatedList)

            viewModelScope.launch {
                val result = packingRepository.updatePackingStatus(tripId, gearId, !currentPacked)
                if (result !is ApiResult.Success) {
                    state = currentState
                }
            }
        }
    }

    fun getProgress(): Pair<Int, Int> {
        val currentState = state
        if (currentState is UiState.Success) {
            val packedCount = currentState.data.count { it.packed }
            return packedCount to currentState.data.size
        }
        return 0 to 0
    }

    fun getTotalWeight(): Double {
        val currentState = state
        if (currentState is UiState.Success) {
            return currentState.data.sumOf { it.weightKg }
        }
        return 0.0
    }
}
