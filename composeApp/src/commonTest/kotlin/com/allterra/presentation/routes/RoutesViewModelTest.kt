package com.allterra.presentation.routes

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.RouteCreateProgressStage
import com.allterra.domain.repository.RoutesRepository
import com.allterra.presentation.common.model.GeoPoint
import com.allterra.presentation.common.model.RouteUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoutesViewModelTest {

    @Test
    fun openAndCloseCreateRoute_updatesState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = RoutesViewModel(
            routesRepository = FakeRoutesRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.openCreateRoute()
        assertTrue(viewModel.state.value.isCreateOpen)

        viewModel.closeCreateRoute()
        assertFalse(viewModel.state.value.isCreateOpen)
    }

    @Test
    fun saveCreatedRoute_withValidGpxFile_addsRoute() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = RoutesViewModel(
            routesRepository = FakeRoutesRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.openCreateRoute()
        viewModel.onCreateTitleChanged("Morning Ride")
        viewModel.onCreateDescriptionChanged("Road training")
        viewModel.onCreateGpxImported(
            fileName = "morning_ride.gpx",
            contentType = "application/gpx+xml",
            fileBytes = validGpxBytes(),
        )

        viewModel.saveCreatedRoute(validationMessage = "Invalid")
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.items.size)
        assertEquals("Morning Ride", viewModel.state.value.items.first().title)
        assertEquals(3, viewModel.state.value.items.first().pointCount)
        assertFalse(viewModel.state.value.isCreateOpen)
        assertFalse(viewModel.state.value.isSaving)
    }

    @Test
    fun saveCreatedRoute_withoutGpxFile_setsValidationError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = RoutesViewModel(
            routesRepository = FakeRoutesRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.openCreateRoute()
        viewModel.saveCreatedRoute(validationMessage = "Invalid")

        assertEquals(0, viewModel.state.value.items.size)
        assertEquals("Invalid", viewModel.state.value.createError)
    }

    @Test
    fun saveCreatedRoute_withNonGpxExtension_setsValidationError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = RoutesViewModel(
            routesRepository = FakeRoutesRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.openCreateRoute()
        viewModel.onCreateGpxImported(
            fileName = "not-gpx.txt",
            contentType = "text/plain",
            fileBytes = "text".encodeToByteArray(),
        )

        viewModel.saveCreatedRoute(validationMessage = "Invalid")
        assertEquals("Invalid", viewModel.state.value.createError)
    }
}

private class FakeRoutesRepository : RoutesRepository {
    private val items = mutableListOf<RouteUiModel>()

    override suspend fun getMyRoutes(): ApiResult<List<RouteUiModel>> = ApiResult.Success(items.toList())

    override suspend fun createRoute(
        title: String,
        description: String,
        fileName: String,
        contentType: String,
        fileBytes: ByteArray,
        onProgress: (RouteCreateProgressStage) -> Unit,
    ): ApiResult<RouteUiModel> {
        if (!fileName.endsWith(".gpx", ignoreCase = true) || fileBytes.isEmpty()) {
            return ApiResult.ValidationError("Invalid GPX file", emptyMap())
        }

        onProgress(RouteCreateProgressStage.UPLOADING)
        onProgress(RouteCreateProgressStage.PROCESSING)

        val route = RouteUiModel(
            id = "r",
            title = title,
            description = description,
            date = "01.01.2026",
            source = "GPX",
            distanceKm = 12.4,
            durationMinutes = 34,
            pointCount = 3,
            previewPoints = listOf(
                GeoPoint(51.84, 16.57),
                GeoPoint(51.85, 16.58),
                GeoPoint(51.86, 16.59),
            ),
        )
        items.add(0, route)
        return ApiResult.Success(route)
    }

    override suspend fun deleteMyRoute(routeId: String): ApiResult<Unit> {
        items.removeAll { it.id == routeId }
        return ApiResult.Success(Unit)
    }
}

private fun validGpxBytes(): ByteArray = """
    <gpx>
      <trk>
        <name>Test Route</name>
        <trkseg>
          <trkpt lat="51.8402" lon="16.5748"><time>2024-09-03T07:01:16.000Z</time></trkpt>
          <trkpt lat="51.8427" lon="16.5864"><time>2024-09-03T07:19:42.000Z</time></trkpt>
          <trkpt lat="51.8490" lon="16.5932"><time>2024-09-03T07:33:58.000Z</time></trkpt>
        </trkseg>
      </trk>
    </gpx>
""".trimIndent().encodeToByteArray()
