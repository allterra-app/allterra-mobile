package com.allterra.presentation.routes

import androidx.lifecycle.ViewModel
import com.allterra.core.result.ApiResult
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
    val gpxContent: String = "",
    val importedFileName: String? = null,
)

data class RoutesUiState(
    val items: List<RouteUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isCreateOpen: Boolean = false,
    val createDraft: RouteCreateDraft = RouteCreateDraft(),
    val createMetrics: RouteGpxMetrics? = null,
    val createError: String? = null,
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
                createMetrics = null,
                createError = null,
            )
        }
    }

    fun closeCreateRoute() {
        _state.update { it.copy(isCreateOpen = false, createError = null) }
    }

    fun onCreateTitleChanged(value: String) {
        _state.update { it.copy(createDraft = it.createDraft.copy(title = value), createError = null) }
    }

    fun onCreateDescriptionChanged(value: String) {
        _state.update { it.copy(createDraft = it.createDraft.copy(description = value), createError = null) }
    }

    fun onCreateGpxImported(fileName: String, content: String) {
        val metrics = analyzeGpx(content)
        val suggestedTitle = metrics.routeName
            ?: fileName.substringBeforeLast(".").takeIf { it.isNotBlank() }

        _state.update {
            it.copy(
                createDraft = it.createDraft.copy(
                    gpxContent = content,
                    importedFileName = fileName,
                    title = it.createDraft.title.ifBlank { suggestedTitle.orEmpty() },
                ),
                createMetrics = metrics,
                createError = null,
            )
        }
    }

    fun onCreateGpxImportFailed(message: String) {
        _state.update { it.copy(createError = message) }
    }

    fun saveCreatedRoute(validationMessage: String) {
        val state = _state.value
        val gpx = state.createDraft.gpxContent.trim()
        if (gpx.isBlank()) {
            _state.update { it.copy(createError = validationMessage) }
            return
        }

        val metrics = state.createMetrics ?: analyzeGpx(gpx)
        if (metrics.pointCount < 2) {
            _state.update { it.copy(createError = validationMessage) }
            return
        }

        scope.launch {
            _state.update { it.copy(isSaving = true, createError = null) }
            val result = routesRepository.createRoute(
                title = state.createDraft.title.ifBlank { metrics.routeName ?: "Route" },
                description = state.createDraft.description,
                gpxContent = gpx,
                metrics = metrics,
            )
            when (result) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            isCreateOpen = false,
                            createDraft = RouteCreateDraft(),
                            createMetrics = null,
                            createError = null,
                            items = listOf(result.data) + it.items,
                        )
                    }
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
