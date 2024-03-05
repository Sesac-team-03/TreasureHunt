package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.ui.model.LogModel
import kotlinx.serialization.Serializable

@Serializable
data class LogDTO(
    val remotePlaceId: String,
    val text: String,
    val theme: String,
    val createdDate: Long,
    val remoteImageIds: Map<String, Boolean>,
    val localId: Long = 0
)

fun LogDTO.toLogEntity(remoteId: String) = LogEntity(
    remotePlaceId,
    text,
    theme,
    createdDate,
    remoteImageIds.keys.toList(),
    localId,
    remoteId
)

fun LogDTO.toLogModel(imageUrls: List<String>, localId: Long? = null, remoteId: String? = null) =
    LogModel(
        remotePlaceId,
        text,
        theme,
        createdDate,
        remoteImageIds.keys.toList(),
        imageUrls,
        localId,
        remoteId
    )
