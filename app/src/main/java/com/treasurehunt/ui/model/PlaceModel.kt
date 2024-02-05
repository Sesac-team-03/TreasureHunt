package com.treasurehunt.ui.model

import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.PlaceDTO

data class PlaceModel(
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val caption: String,
    val log: String? = null
)

fun PlaceModel.asPlaceEntity(remoteId: String? = null, localId: Long = 0): PlaceEntity {
    val (lat, lng, plan, caption, log) = this
    return PlaceEntity(lat, lng, plan, caption, log, remoteId, localId)
}

fun PlaceModel.asPlaceDTO(localId: Long = 0): PlaceDTO {
    val (lat, lng, plan, caption, log) = this
    return PlaceDTO(lat, lng, plan, caption, log, localId)
}