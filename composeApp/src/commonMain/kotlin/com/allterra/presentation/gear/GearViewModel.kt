package com.allterra.presentation.gear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allterra.config.AppConfig
import com.allterra.core.result.ApiResult
import com.allterra.core.ui.UiState
import com.allterra.domain.repository.GearRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GearUiState(
    val uiState: UiState<List<GearItemUiModel>> = UiState.Loading,
    val items: List<GearItemUiModel> = emptyList(),
    val selectedCategory: String? = null,
    val selectedItemId: String? = null,
    val isEditorOpen: Boolean = false,
    val editorItemId: String? = null,
    val editorError: String? = null,
    val successMessage: String? = null,
)

class GearViewModel(
    private val repository: GearRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GearUiState())
    val state: StateFlow<GearUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(uiState = UiState.Loading) }
            when (val result = repository.getGearItems()) {
                is ApiResult.Success -> {
                    _state.update { it.copy(uiState = UiState.Success(result.data), items = result.data) }
                }
                else -> {
                    _state.update { it.copy(uiState = UiState.Error("Failed to load gear inventory")) }
                }
            }
        }
    }

    fun onCategorySelected(category: String?) {
        _state.update { it.copy(selectedCategory = category) }
    }

    fun openDetail(id: String) {
        _state.update { it.copy(selectedItemId = id) }
    }

    fun closeDetail() {
        _state.update { it.copy(selectedItemId = null) }
    }

    fun openAddForm() {
        _state.update { it.copy(isEditorOpen = true, editorItemId = null, editorError = null) }
    }

    fun openEditForm(id: String) {
        _state.update { it.copy(isEditorOpen = true, editorItemId = id, editorError = null) }
    }

    fun closeEditor() {
        _state.update { it.copy(isEditorOpen = false, editorItemId = null, editorError = null) }
    }

    fun saveGear(draft: GearDraft) {
        println("ALLTERRA_GEAR_DEBUG saveGear baseUrl=${AppConfig.baseUrl}")
        val validationError = validateDraft(draft)
        if (validationError != null) {
            _state.update { it.copy(editorError = validationError) }
            return
        }
        viewModelScope.launch {
            val editingId = state.value.editorItemId
            val result = if (editingId == null) repository.createGearItem(draft) else repository.updateGearItem(editingId, draft)
            when (result) {
                is ApiResult.Success -> {
                    val success = if (editingId == null) "Gear item added" else "Gear item updated"
                    closeEditor()
                    _state.update { it.copy(successMessage = success) }
                    refresh()
                }
                is ApiResult.ValidationError -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.Unauthorized -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.Forbidden -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.NotFound -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.ServerError -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.NetworkError -> _state.update { it.copy(editorError = result.message) }
                is ApiResult.UnknownError -> _state.update { it.copy(editorError = result.message) }
            }
        }
    }

    fun deleteGear(id: String) {
        viewModelScope.launch {
            repository.deleteGearItem(id)
            closeDetail()
            _state.update { it.copy(successMessage = "Gear item deleted") }
            refresh()
        }
    }

    fun consumeSuccessMessage() {
        _state.update { it.copy(successMessage = null) }
    }

    private fun validateDraft(draft: GearDraft): String? {
        if (draft.name.isBlank()) return "Name is required"
        if (draft.category.isBlank()) return "Category is required"
        if (draft.weightKg <= 0.0) return "Weight must be greater than 0"
        return null
    }
}
