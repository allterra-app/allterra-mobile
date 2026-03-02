package com.allterra.domain.repository

import com.allterra.core.result.ApiResult
import com.allterra.presentation.common.model.ActivityUiModel

interface PostsRepository {
    suspend fun getFeedPosts(): ApiResult<List<ActivityUiModel>>
    suspend fun getMyPosts(): ApiResult<List<ActivityUiModel>>
    suspend fun getMyUserName(): ApiResult<String>
    suspend fun createMyPost(
        title: String,
        description: String,
        localPhotoPaths: List<String>,
        selectedRouteId: String?,
        selectedPoiIds: List<String>,
    ): ApiResult<ActivityUiModel>
}
