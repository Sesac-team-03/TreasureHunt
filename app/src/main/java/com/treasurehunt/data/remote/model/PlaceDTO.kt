package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.PlaceEntity
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDTO(
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val caption: String,
    val log: String? = null,
    val localId: Long = 0
)

fun PlaceDTO.toPlaceEntity(remoteId: String): PlaceEntity {
    val (lat, lng, plan, caption, log, localId) = this
    return PlaceEntity(lat, lng, plan, caption, log, remoteId, localId)
}