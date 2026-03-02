package com.allterra.presentation.pois

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PoisRepository
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.PoiType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PoiCreateDraft(
    val name: String = "",
    val type: PoiType = PoiType.OTHER,
    val photoUris: List<String> = emptyList(),
)

data class PoisUiState(
    val items: List<PoiUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isCreateOpen: Boolean = false,
    val createDraft: PoiCreateDraft = PoiCreateDraft(),
    val createError: String? = null,
)

class PoisViewModel(
    private val poisRepository: PoisRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(PoisUiState())
    val state: StateFlow<PoisUiState> = _state.asStateFlow()

    init {
        refreshPois()
    }

    fun refreshPois() {
        scope.launch {
            _state.update { it.copy(isLoading = true, createError = null) }
            when (val result = poisRepository.getMyPois()) {
                is ApiResult.Success -> _state.update {
                    it.copy(items = result.data, isLoading = false)
                }

                else -> _state.update {
                    it.copy(isLoading = false, createError = result.message())
                }
            }
        }
    }

    fun openCreatePoi() {
        _state.update {
            it.copy(
                isCreateOpen = true,
                createDraft = PoiCreateDraft(),
                createError = null,
            )
        }
    }

    fun closeCreatePoi() {
        _state.update { it.copy(isCreateOpen = false, createError = null) }
    }

    fun onCreateNameChanged(value: String) {
        _state.update { it.copy(createDraft = it.createDraft.copy(name = value), createError = null) }
    }

    fun onCreateTypeChanged(value: PoiType) {
        _state.update { it.copy(createDraft = it.createDraft.copy(type = value), createError = null) }
    }

    fun addCreatePhotos(photoUris: List<String>, maxPhotos: Int, limitMessage: String) {
        if (photoUris.isEmpty()) return
        _state.update { state ->
            val current = state.createDraft.photoUris
            val freeSlots = (maxPhotos - current.size).coerceAtLeast(0)
            if (freeSlots == 0) {
                state.copy(createError = limitMessage)
            } else {
                val next = current + photoUris.take(freeSlots)
                state.copy(
                    createDraft = state.createDraft.copy(photoUris = next),
                    createError = if (next.size >= maxPhotos && photoUris.size > freeSlots) limitMessage else null,
                )
            }
        }
    }

    fun onCreatePhotoReadError(message: String) {
        _state.update { it.copy(createError = message) }
    }

    fun removeCreatePhoto(uri: String) {
        _state.update { state ->
            state.copy(
                createDraft = state.createDraft.copy(photoUris = state.createDraft.photoUris - uri),
                createError = null,
            )
        }
    }

    fun saveCreatedPoi(validationMessage: String) {
        val state = _state.value
        val name = state.createDraft.name.trim()
        if (name.isBlank()) {
            _state.update { it.copy(createError = validationMessage) }
            return
        }

        scope.launch {
            _state.update { it.copy(isSaving = true, createError = null) }
            when (
                val result = poisRepository.createPoi(
                    name = name,
                    type = state.createDraft.type,
                    localPhotoPaths = state.createDraft.photoUris,
                )
            ) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        isSaving = false,
                        items = it.items + result.data,
                        isCreateOpen = false,
                        createDraft = PoiCreateDraft(),
                        createError = null,
                    )
                }

                else -> _state.update {
                    it.copy(isSaving = false, createError = result.message())
                }
            }
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

private fun ApiResult<*>.message(): String {
    return when (this) {
        is ApiResult.Success -> ""
        is ApiResult.ValidationError -> message
        is ApiResult.Unauthorized -> message
        is ApiResult.Forbidden -> message
        is ApiResult.NotFound -> message
        is ApiResult.ServerError -> message
        is ApiResult.NetworkError -> message
        is ApiResult.UnknownError -> message
    }
}
