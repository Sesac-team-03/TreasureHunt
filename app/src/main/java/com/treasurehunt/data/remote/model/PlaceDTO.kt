package com.treasurehunt.data.remote.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.treasurehunt.data.local.model.PlaceEntity
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDTO(
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val caption: String,
    val remoteId: String? = null,
    val log: String? = null,
    val id: Long = 0
)

fun PlaceDTO.toPlaceEntity(): PlaceEntity {
    val (lat, lng, plan, caption, remoteId, log, id) = this
    return PlaceEntity(lat, lng, plan, caption, remoteId, log, id)
}