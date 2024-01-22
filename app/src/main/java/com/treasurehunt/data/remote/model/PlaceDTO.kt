package com.treasurehunt.data.remote.model

import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDTO(
    val log: String,
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val uid: Long = 0
)

fun PlaceDTO.toPlaceEntity(): PlaceEntity {
    val (log, lat, lng, plan, uid) = this
    return PlaceEntity(
        log,
        lat,
        lng,
        plan,
        uid
    )
}