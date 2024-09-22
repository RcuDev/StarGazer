package com.rcudev.posts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InfoResponse(
    val version: String,
    @SerialName("news_sites")
    val newsSites: List<String>
)
