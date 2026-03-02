package com.allterra.network.post

import kotlinx.serialization.Serializable

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
    val photos: List<PostPhotoDto>? = null,
    val createdAt: String? = null,
    val modifiedAt: String? = null,
)
