package com.treasurehunt.data.remote.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.treasurehunt.data.local.model.LogEntity
import kotlinx.serialization.Serializable

@Serializable
data class LogDTO(
    val place: String,
    val images: List<String>,
    val text: String,
    val theme: String,
    val createdDate: Long,
)

fun LogDTO.toLogEntity(remoteId: String): LogEntity {
    val (place, images, text, theme, createdDate) = this
    return LogEntity(place, images, text, theme, createdDate, remoteId)
}