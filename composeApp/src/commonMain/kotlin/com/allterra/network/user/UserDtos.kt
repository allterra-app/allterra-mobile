package com.allterra.network.user

import kotlinx.serialization.Serializable

@Serializable
data class UserMeDto(
    val id: String,
    val email: String,
    val username: String? = null,
)
