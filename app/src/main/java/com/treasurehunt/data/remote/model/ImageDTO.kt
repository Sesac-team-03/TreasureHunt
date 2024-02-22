package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.ImageEntity
import com.treasurehunt.ui.model.ImageModel
import kotlinx.serialization.Serializable

@Serializable
data class ImageDTO(
    val url: String,
    val localId: Long = 0
)

fun ImageDTO.toImageEntity(remoteId: String): ImageEntity =
    ImageEntity(url, null, localId, remoteId)

fun ImageDTO.toImageModel(): ImageModel =
    ImageModel(url)