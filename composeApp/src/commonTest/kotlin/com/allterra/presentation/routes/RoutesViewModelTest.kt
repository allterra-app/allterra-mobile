package com.allterra.presentation.routes

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.RoutesRepository
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.routes.RouteGpxMetrics
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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
    fun saveCreatedRoute_withValidGpx_addsRoute() = runTest {
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
            content = validGpx(),
        )
        assertNotNull(viewModel.state.value.createMetrics)
        assertEquals("morning_ride.gpx", viewModel.state.value.createDraft.importedFileName)

        viewModel.saveCreatedRoute(validationMessage = "Invalid")
        advanceUntilIdle()
        assertEquals(1, viewModel.state.value.items.size)
        assertEquals("Morning Ride", viewModel.state.value.items.first().title)
        assertEquals(3, viewModel.state.value.items.first().pointCount)
        assertFalse(viewModel.state.value.isCreateOpen)
    }

    @Test
    fun saveCreatedRoute_withoutPoints_setsValidationError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = RoutesViewModel(
            routesRepository = FakeRoutesRepository(),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreateRoute()
        viewModel.onCreateGpxImported(
            fileName = "empty.gpx",
            content = "<gpx><trk><name>Empty</name></trk></gpx>",
        )

        viewModel.saveCreatedRoute(validationMessage = "Invalid")
        assertEquals(0, viewModel.state.value.items.size)
        assertEquals("Invalid", viewModel.state.value.createError)
    }
}

private class FakeRoutesRepository : RoutesRepository {
    private val items = mutableListOf<RouteUiModel>()

    override suspend fun getMyRoutes(): ApiResult<List<RouteUiModel>> = ApiResult.Success(items.toList())

    override suspend fun createRoute(
        title: String,
        description: String,
        gpxContent: String,
        metrics: RouteGpxMetrics,
    ): ApiResult<RouteUiModel> {
        val route = RouteUiModel(
            id = "r${items.size + 1}",
            title = title,
            description = description,
            date = "01.01.2026",
            source = "GPX",
            distanceKm = metrics.distanceKm,
            durationMinutes = metrics.durationMinutes,
            pointCount = metrics.pointCount,
            previewPoints = metrics.points,
        )
        items.add(0, route)
        return ApiResult.Success(route)
    }
}

private fun validGpx() = """
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
""".trimIndent()
