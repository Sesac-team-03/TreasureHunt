package com.treasurehunt.ui.model

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.remote.model.LogDTO

data class LogModel(
    val place: String,
    val images: List<String>,
    val text: String,
    val theme: String,
    val createdDate: Long
)

fun LogModel.asLogEntity(remoteId: String? = null, localId: Long = 0): LogEntity {
    val (place, images, text, theme, createdDate) = this
    return LogEntity(place, images, text, theme, createdDate, remoteId, localId)
}

fun LogModel.asLogDTO(localId: Long = 0): LogDTO {
    val (place, images, text, theme, createdDate) = this
    return LogDTO(place, images.associateWith { true }, text, theme, createdDate, localId)
}