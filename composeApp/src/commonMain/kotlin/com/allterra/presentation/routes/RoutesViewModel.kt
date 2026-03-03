package com.allterra.presentation.routes

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.RouteCreateProgressStage
import com.allterra.domain.repository.RoutesRepository
import com.allterra.presentation.common.model.RouteUiModel
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

data class RouteCreateDraft(
    val title: String = "",
    val description: String = "",
    val importedFileName: String? = null,
    val importedFileContentType: String = "application/gpx+xml",
    val importedFileBytes: ByteArray? = null,
)

data class RoutesUiState(
    val items: List<RouteUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isCreateOpen: Boolean = false,
    val createDraft: RouteCreateDraft = RouteCreateDraft(),
    val createProgress: RouteCreateProgressStage? = null,
    val createError: String? = null,
    val deletingIds: Set<String> = emptySet(),
)

class RoutesViewModel(
    private val routesRepository: RoutesRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _state = MutableStateFlow(RoutesUiState())
    val state: StateFlow<RoutesUiState> = _state.asStateFlow()

    init {
        refreshRoutes()
    }

    fun refreshRoutes() {
        scope.launch {
            _state.update { it.copy(isLoading = true, createError = null) }
            when (val result = routesRepository.getMyRoutes()) {
                is ApiResult.Success -> _state.update {
                    it.copy(items = result.data, isLoading = false)
                }

                else -> _state.update {
                    it.copy(isLoading = false, createError = result.message())
                }
            }
        }
    }

    fun openCreateRoute() {
        _state.update {
            it.copy(
                isCreateOpen = true,
                createDraft = RouteCreateDraft(),
                createProgress = null,
                createError = null,
            )
        }
    }

    fun closeCreateRoute() {
        _state.update { it.copy(isCreateOpen = false, createProgress = null, createError = null) }
    }

    fun onCreateTitleChanged(value: String) {
        _state.update { it.copy(createDraft = it.createDraft.copy(title = value), createError = null) }
    }

    fun onCreateDescriptionChanged(value: String) {
        _state.update { it.copy(createDraft = it.createDraft.copy(description = value), createError = null) }
    }

    fun onCreateGpxImported(fileName: String, contentType: String, fileBytes: ByteArray) {
        val suggestedTitle = fileName.substringBeforeLast(".").takeIf { it.isNotBlank() }

        _state.update {
            it.copy(
                createDraft = it.createDraft.copy(
                    importedFileName = fileName,
                    importedFileContentType = contentType,
                    importedFileBytes = fileBytes,
                    title = it.createDraft.title.ifBlank { suggestedTitle.orEmpty() },
                ),
                createError = null,
            )
        }
    }

    fun onCreateGpxImportFailed(message: String) {
        _state.update { it.copy(createError = message) }
    }

    fun saveCreatedRoute(validationMessage: String) {
        val state = _state.value
        val fileName = state.createDraft.importedFileName
        val fileBytes = state.createDraft.importedFileBytes
        if (fileName.isNullOrBlank() || fileBytes == null || fileBytes.isEmpty()) {
            _state.update { it.copy(createError = validationMessage) }
            return
        }

        if (!fileName.lowercase().endsWith(".gpx")) {
            _state.update { it.copy(createError = validationMessage) }
            return
        }

        scope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    createProgress = RouteCreateProgressStage.UPLOADING,
                    createError = null,
                )
            }

            val result = routesRepository.createRoute(
                title = state.createDraft.title,
                description = state.createDraft.description,
                fileName = fileName,
                contentType = state.createDraft.importedFileContentType,
                fileBytes = fileBytes,
                onProgress = { progress ->
                    _state.update { current ->
                        current.copy(createProgress = progress)
                    }
                },
            )

            when (result) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isCreateOpen = false,
                            createDraft = RouteCreateDraft(),
                            createProgress = null,
                            createError = null,
                            items = listOf(result.data) + it.items,
                        )
                    }
                }

                else -> _state.update {
                    it.copy(isSaving = false, createProgress = null, createError = result.message())
                }
            }
        }
    }

    fun deleteRoute(routeId: String) {
        val currentState = _state.value
        if (routeId in currentState.deletingIds) return

        scope.launch {
            _state.update { it.copy(deletingIds = it.deletingIds + routeId, createError = null) }
            when (val result = routesRepository.deleteMyRoute(routeId)) {
                is ApiResult.Success -> _state.update {
                    it.copy(
                        deletingIds = it.deletingIds - routeId,
                        items = it.items.filterNot { item -> item.id == routeId },
                    )
                }

                else -> _state.update {
                    it.copy(
                        deletingIds = it.deletingIds - routeId,
                        createError = result.message(),
                    )
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
