package com.allterra.presentation.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.repository.WalletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WalletState(
    val uiState: UiState<List<WalletItem>> = UiState.Loading,
    val items: List<WalletItem> = emptyList()
)

class WalletViewModel(
    private val repository: WalletRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WalletState())
    val state: StateFlow<WalletState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading) }
            when (val result = repository.getDocuments()) {
                is ApiResult.Success -> {
                    _state.update { it.copy(uiState = UiState.Success(result.data), items = result.data) }
                }
                else -> {
                    _state.update { it.copy(uiState = UiState.Error("Failed to load documents")) }
                }
            }
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            repository.deleteDocument(id)
            refresh()
        }
    }
}
