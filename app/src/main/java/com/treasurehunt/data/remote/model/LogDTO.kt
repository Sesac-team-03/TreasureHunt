package com.treasurehunt.data.remote.model

import com.treasurehunt.data.ImageRepository
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.ui.model.LogModel
import kotlinx.serialization.Serializable

@Serializable
data class LogDTO(
    val place: String,
    val images: Map<String,Boolean>,
    val text: String,
    val theme: String,
    val createdDate: Long,
    val localId: Long = 0
)

fun LogDTO.toLogEntity(remoteId: String): LogEntity {
    val (place, images, text, theme, createdDate, localId) = this
    return LogEntity(place, images.keys.toList(), text, theme, createdDate, remoteId, localId)
}

suspend fun LogDTO.toLogModel(imageRepo: ImageRepository): LogModel {
    val imageUrls = images.keys.map { imageId -> imageRepo.getImage(imageId).url }
    return LogModel(place, images.keys.toList(), imageUrls, text, theme, createdDate)
}