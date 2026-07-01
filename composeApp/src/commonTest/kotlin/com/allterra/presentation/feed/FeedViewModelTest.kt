package com.allterra.presentation.feed

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.FeedPageResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FeedViewModelTest {

    @Test
    fun toggleLike_switchesFlag() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = FeedViewModel(
            postsRepository = FakePostsRepository(feed = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        val id = viewModel.state.value.activities.first().id
        val initial = viewModel.state.value.activities.first().liked

        viewModel.toggleLike(id)
        advanceUntilIdle()

        val updated = viewModel.state.value.activities.first().liked
        if (initial) {
            assertFalse(updated, "Expected liked=false after unlike")
        } else {
            assertTrue(updated, "Expected liked=true after like")
        }
    }

    @Test
    fun toggleLike_updatesCount() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = FeedViewModel(
            postsRepository = FakePostsRepository(feed = listOf(testActivity(likeCount = 5))),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        val id = viewModel.state.value.activities.first().id
        val initialCount = viewModel.state.value.activities.first().likeCount

        viewModel.toggleLike(id)
        advanceUntilIdle()

        val updatedCount = viewModel.state.value.activities.first().likeCount
        assertEquals(initialCount + 1, updatedCount, "Like count should increment by 1")
    }

    @Test
    fun toggleBookmark_switchesFlag() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewModel = FeedViewModel(
            postsRepository = FakePostsRepository(feed = listOf(testActivity())),
            dispatcher = dispatcher,
        )
        advanceUntilIdle()
        val id = viewModel.state.value.activities.first().id
        val initial = viewModel.state.value.activities.first().bookmarked

        viewModel.toggleBookmark(id)

        val updated = viewModel.state.value.activities.first().bookmarked
        if (initial) {
            assertFalse(updated)
        } else {
            assertTrue(updated)
        }
    }
}

private class FakePostsRepository(
    private val feed: List<ActivityUiModel> = emptyList(),
) : PostsRepository {
    override suspend fun getFeedPosts(page: Int, size: Int): ApiResult<FeedPageResult> = ApiResult.Success(
        FeedPageResult(
            items = feed,
            page = page,
            size = size,
            totalItems = feed.size.toLong(),
            hasNext = false,
        )
    )
    override suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>> = ApiResult.Success(emptyList())
    override suspend fun getMyUserName(): ApiResult<String> = ApiResult.Success("Tester")
    override suspend fun getMySavedPostIds(): ApiResult<Set<String>> = ApiResult.Success(emptySet())
    override suspend fun savePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun unsavePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun getMyNotifications(): ApiResult<List<com.allterra.presentation.common.model.NotificationUiModel>> =
        ApiResult.Success(emptyList())
    override suspend fun markNotificationRead(notificationId: String): ApiResult<com.allterra.presentation.common.model.NotificationUiModel> =
        ApiResult.UnknownError("Not used")
    override suspend fun likePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
    override suspend fun unlikePost(postId: String): ApiResult<Unit> = ApiResult.Success(Unit)
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
    ): ApiResult<ActivityUiModel> = ApiResult.UnknownError("Not used")

    override suspend fun deleteMyPost(postId: String): ApiResult<Unit> = ApiResult.UnknownError("Not used")
}

private fun testActivity(likeCount: Int = 0) = ActivityUiModel(
    id = "a1",
    title = "Test",
    description = "Test description",
    author = "Tester",
    addedAt = "20.02.2026",
    updatedAt = "20.02.2026",
    likeCount = likeCount,
)
