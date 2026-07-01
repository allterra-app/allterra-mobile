package com.allterra.presentation.profile

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.FeedPageResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityUiModel
import com.allterra.presentation.common.model.PoiUiModel
import com.allterra.presentation.common.model.RouteUiModel
import com.allterra.presentation.trips.TripStatus
import com.allterra.presentation.trips.TripUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProfileViewModelTest {

    @Test
    fun setLayout_updatesState() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProfileViewModel(
            postsRepository = FakePostsRepository(myPosts = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()

        viewModel.setLayout(ProfileFeedLayout.GRID)

        assertEquals(ProfileFeedLayout.GRID, viewModel.state.value.feedLayout)
    }

    @Test
    fun toggleLike_updatesActivity() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProfileViewModel(
            postsRepository = FakePostsRepository(myPosts = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        val id = viewModel.state.value.activities.first().id

        viewModel.toggleLike(id)

        assertTrue(viewModel.state.value.activities.first().liked)
    }

    @Test
    fun saveCreatedPost_withValidDraft_insertsAtTop() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProfileViewModel(
            postsRepository = FakePostsRepository(myPosts = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreatePost()
        viewModel.onPostTitleChanged("My hike")
        viewModel.onPostDescriptionChanged("Great weather")
        viewModel.onPostTripSelected("trip-1")
        viewModel.onPostRouteSelected("route-1")
        viewModel.togglePostPoi("poi-1")
        viewModel.addCreatePhotos(
            photoUris = listOf("a.jpg", "b.jpg"),
            maxPhotos = 10,
            limitMessage = "Limit",
        )

        val saved = viewModel.saveCreatedPost(
            validationMessage = "Invalid",
            availableTrips = listOf(testTrip()),
            availableRoutes = listOf(testRoute()),
            availablePois = listOf(testPoi()),
        )
        advanceUntilIdle()

        assertTrue(saved)
        assertEquals(2, viewModel.state.value.activities.size)
        assertTrue(viewModel.state.value.activities.first().id.startsWith("post_"))
        assertEquals("trip-1", viewModel.state.value.activities.first().tripId)
        assertEquals("route-1", viewModel.state.value.activities.first().routeId)
        assertEquals(listOf("poi-1"), viewModel.state.value.activities.first().poiIds)
        assertEquals(2, viewModel.state.value.activities.first().photoUris.size)
        assertFalse(viewModel.state.value.isCreatePostOpen)
    }

    @Test
    fun saveCreatedPost_withoutTitle_setsValidationError() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = ProfileViewModel(
            postsRepository = FakePostsRepository(myPosts = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        viewModel.openCreatePost()
        viewModel.onPostTripSelected("trip-1")
        viewModel.onPostRouteSelected("route-1")

        val saved = viewModel.saveCreatedPost(
            validationMessage = "Invalid",
            availableTrips = listOf(testTrip()),
            availableRoutes = emptyList(),
            availablePois = emptyList(),
        )

        assertFalse(saved)
        assertEquals("Invalid", viewModel.state.value.postCreateError)
    }
}

private class FakePostsRepository(
    private val myPosts: List<ActivityUiModel> = emptyList(),
) : PostsRepository {
    private val feedPosts = mutableListOf<ActivityUiModel>().apply { addAll(myPosts) }

    override suspend fun getFeedPosts(page: Int, size: Int): ApiResult<FeedPageResult> = ApiResult.Success(
        FeedPageResult(
            items = feedPosts.toList(),
            page = page,
            size = size,
            totalItems = feedPosts.size.toLong(),
            hasNext = false,
        )
    )

    override suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>> = ApiResult.Success(feedPosts.toList())

    override suspend fun getMyUserName(): ApiResult<String> = ApiResult.Success("Tester")

    override suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        audience: com.allterra.presentation.common.model.PostAudienceUi,
        selectedTripId: String?,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
        type: com.allterra.presentation.common.model.PostTypeUi?,
        activity: com.allterra.presentation.common.model.ActivityTypeUi?,
    ): ApiResult<ActivityUiModel> {
        val created = ActivityUiModel(
            id = "post_",
            title = title,
            description = description,
            author = "Tester",
            addedAt = "20.02.2026",
            updatedAt = "20.02.2026",
            tripId = selectedTripId,
            routeId = selectedRouteId,
            poiIds = selectedPoiIds,
            photoUris = localPhotoPaths,
        )
        feedPosts.add(0, created)
        return ApiResult.Success(created)
    }

    override suspend fun deleteMyPost(postId: String): ApiResult<Unit> {
        feedPosts.removeAll { it.id == postId }
        return ApiResult.Success(Unit)
    }
    override suspend fun getMySavedPostIds(): ApiResult<Set<String>> = ApiResult.Success(emptySet())
    override suspend fun savePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun unsavePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun getMyNotifications(): ApiResult<List<com.allterra.presentation.common.model.NotificationUiModel>> =
        ApiResult.Success(emptyList())
    override suspend fun markNotificationRead(notificationId: String): ApiResult<com.allterra.presentation.common.model.NotificationUiModel> =
        ApiResult.UnknownError("Not used")
    override suspend fun likePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun unlikePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
}

private fun testActivity() = ActivityUiModel(
    id = "a1",
    title = "Test",
    description = "Test description",
    author = "Tester",
    addedAt = "20.02.2026",
    updatedAt = "20.02.2026",
)

private fun testRoute() = RouteUiModel(
    id = "route-1",
    title = "Route",
    date = "20.02.2026",
    source = "GPX",
)

private fun testTrip() = TripUiModel(
    id = "trip-1",
    title = "Trip",
    region = "Tatras",
    status = TripStatus.Planned,
    dates = "20.02.2026 - 22.02.2026",
)

private fun testPoi() = PoiUiModel(
    id = "poi-1",
    title = "POI",
    addedAt = "20.02.2026",
    updatedAt = "20.02.2026",
)
