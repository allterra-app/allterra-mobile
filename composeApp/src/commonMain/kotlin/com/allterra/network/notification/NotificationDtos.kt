package com.allterra.network.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val title: String,
    val body: String,
    val read: Boolean,
    val createdAt: String? = null,
)
