package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.data.local.model.toImageDTO
import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val url: String,
    val localId: Long = 0
)

fun ImageDTO.toImageEntity(remoteId: String): ImageEntity {
    val (url, localId) = this
    return ImageEntity(url, null, remoteId, localId)
}