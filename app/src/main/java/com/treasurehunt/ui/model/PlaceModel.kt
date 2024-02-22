package com.treasurehunt.ui.model

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO

data class PlaceModel(
    val lat: Double,
    val lng: Double,
    val isPlan: Boolean,
    val caption: String,
    val remoteLogId: String? = null
)

fun PlaceModel.asPlaceEntity(localId: Long = 0, remoteId: String? = null) =
    PlaceEntity(lat, lng, isPlan, caption, remoteLogId, localId, remoteId)

fun PlaceModel.asPlaceDTO(localId: Long = 0) =
    PlaceDTO(lat, lng, isPlan, caption, remoteLogId, localId)
