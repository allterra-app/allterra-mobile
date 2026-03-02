package com.allterra.presentation.feed

import com.allterra.core.result.ApiResult
import com.allterra.domain.repository.PostsRepository
import com.allterra.presentation.common.model.ActivityUiModel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
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

        val updated = viewModel.state.value.activities.first().liked
        if (initial) {
            assertFalse(updated)
        } else {
            assertTrue(updated)
        }
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
    override suspend fun getFeedPosts(): ApiResult<List<ActivityUiModel>> = ApiResult.Success(feed)
    override suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>> = ApiResult.Success(emptyList())
    override suspend fun getMyUserName(): ApiResult<String> = ApiResult.Success("Tester")
    override suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
    ): ApiResult<ActivityUiModel> = ApiResult.UnknownError("Not used")
}

private fun testActivity() = ActivityUiModel(
    id = "a1",
    title = "Test",
    description = "Test description",
    author = "Tester",
    addedAt = "20.02.2026",
    updatedAt = "20.02.2026",
)
