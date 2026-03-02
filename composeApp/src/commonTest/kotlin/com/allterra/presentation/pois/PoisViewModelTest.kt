package com.allterra.presentation.pois

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PoisRepository
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.PoiType
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PoisViewModelTest {

    @Test
    fun openAndCloseCreatePoi_updatesState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = PoisViewModel(
            poisRepository = FakePoisRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.openCreatePoi()
        assertTrue(viewModel.state.value.isCreateOpen)

        viewModel.closeCreatePoi()
        assertFalse(viewModel.state.value.isCreateOpen)
    }

    @Test
    fun saveCreatedPoi_withValidName_addsPoi() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = PoisViewModel(
            poisRepository = FakePoisRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreatePoi()
        viewModel.onCreateNameChanged("Cafe")
        viewModel.onCreateTypeChanged(PoiType.SHOP)
        viewModel.addCreatePhotos(
            photoUris = listOf("a.jpg", "b.jpg"),
            maxPhotos = 5,
            limitMessage = "Limit",
        )

        viewModel.saveCreatedPoi(validationMessage = "Invalid")
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.items.size)
        assertEquals("Cafe", viewModel.state.value.items.first().title)
        assertEquals(PoiType.SHOP, viewModel.state.value.items.first().type)
        assertEquals(2, viewModel.state.value.items.first().photoUris.size)
        assertFalse(viewModel.state.value.isCreateOpen)
    }

    @Test
    fun addCreatePhotos_respectsLimit() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = PoisViewModel(
            poisRepository = FakePoisRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreatePoi()
        viewModel.addCreatePhotos(
            photoUris = listOf("1", "2", "3", "4", "5", "6"),
            maxPhotos = 5,
            limitMessage = "Limit",
        )

        assertEquals(5, viewModel.state.value.createDraft.photoUris.size)
        assertEquals("Limit", viewModel.state.value.createError)
    }

    @Test
    fun saveCreatedPoi_withoutName_setsValidationError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = PoisViewModel(
            poisRepository = FakePoisRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreatePoi()

        viewModel.saveCreatedPoi(validationMessage = "Invalid")

        assertEquals("Invalid", viewModel.state.value.createError)
        assertTrue(viewModel.state.value.items.isEmpty())
    }
}

private class FakePoisRepository : PoisRepository {
    private val items = mutableListOf<PoiUiModel>()

    override suspend fun getMyPois(): ApiResult<List<PoiUiModel>> = ApiResult.Success(items.toList())

    override suspend fun createPoi(
        name: String,
        type: PoiType,
        localPhotoPaths: List<String>,
    ): ApiResult<PoiUiModel> {
        val item = PoiUiModel(
            id = "poi_${items.size + 1}",
            title = name,
            type = type,
            photoUris = localPhotoPaths,
            addedAt = "20.02.2026",
            updatedAt = "20.02.2026",
        )
        items.add(item)
        return ApiResult.Success(item)
    }
}
