package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.ui.model.MapSymbol
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDTO(
    val lat: Double,
    val lng: Double,
    val isPlan: Boolean,
    val caption: String,
    val remoteLogId: String? = null,
    val localId: Long = 0
)

fun PlaceDTO.toPlaceEntity(remoteId: String) = PlaceEntity(
    lat,
    lng,
    isPlan,
    caption,
    remoteLogId,
    localId,
    remoteId
)

fun PlaceDTO.toMapSymbol() = MapSymbol(
    lat,
    lng,
    isPlan,
    caption
)