package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.ui.model.LogModel
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class LogDTO @OptIn(ExperimentalSerializationApi::class) constructor(
    val remotePlaceId: String,
    @EncodeDefault val text: String = "",
    val theme: Int,
    val createdDate: Long,
    @EncodeDefault val remoteImageIds: Map<String, Boolean> = emptyMap(),
    val localId: Long = 0
)

fun LogDTO.toLogEntity(remoteId: String) = LogEntity(
    remotePlaceId,
    text,
    theme,
    createdDate,
    remoteImageIds.filterValues { it }.keys.toList(),
    localId,
    remoteId
)

fun LogDTO.toLogModel(imageUrls: List<String>, localId: Long? = null, remoteId: String? = null) =
    LogModel(
        remotePlaceId,
        text,
        theme,
        createdDate,
        remoteImageIds.filterValues { it }.keys.toList(),
        imageUrls,
        localId,
        remoteId
    )
