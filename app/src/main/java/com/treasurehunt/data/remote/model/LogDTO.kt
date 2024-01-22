package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.LogEntity
import kotlinx.serialization.Serializable

@Serializable
data class LogDTO(
    val createdDate: String,
    val images: List<String>,
    val place: String,
    val text: String,
    val theme: Long
)

fun LogDTO.toLogEntity(): LogEntity {
    val (createdDate, images, place, text, theme) = this
    return LogEntity(
        createdDate,
        images,
        place,
        text,
        theme
    )
}