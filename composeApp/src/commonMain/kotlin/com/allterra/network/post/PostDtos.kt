package com.allterra.network.post

import kotlinx.serialization.Serializable

@Serializable
enum class PostAudienceDto {
    PUBLIC,
    FRIENDS,
    CLUB,
}

@Serializable
enum class PostTypeDto {
    CHECK_IN,
    ROUTE,
    NOTE,
    GEAR,
}

@Serializable
enum class ActivityTypeDto {
    HIKE,
    BIKEPACKING,
    ALPINISM,
    TREK,
    CLIMB,
}

@Serializable
data class PostUserRefDto(
    val id: String,
    val email: String? = null,
    val username: String? = null,
)

@Serializable
data class PostRefDto(
    val id: String,
)

@Serializable
data class PostPhotoDto(
    val id: String? = null,
    val url: String,
)

@Serializable
data class PostCreateRequestDto(
    val userId: String? = null,
    val tripId: String? = null,
    val routeId: String? = null,
    val audience: PostAudienceDto? = null,
    val type: PostTypeDto? = null,
    val activity: ActivityTypeDto? = null,
    val title: String,
    val body: String? = null,
)

@Serializable
data class PostPhotoCreateRequestDto(
    val url: String,
    val postId: String,
)

@Serializable
data class PostDto(
    val id: String,
    val user: PostUserRefDto? = null,
    val title: String,
    val body: String? = null,
    val audience: PostAudienceDto? = null,
    val type: PostTypeDto? = null,
    val activity: ActivityTypeDto? = null,
    val tripId: String? = null,
    val routeId: String? = null,
    val photos: List<PostPhotoDto>? = null,
    val likeCount: Int = 0,
    val liked: Boolean = false,
    val createdAt: String? = null,
    val modifiedAt: String? = null,
)

@Serializable
data class FeedPageDto(
    val items: List<PostDto> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalItems: Long = 0,
    val hasNext: Boolean = false,
)
