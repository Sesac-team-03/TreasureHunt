package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.remote.model.PlaceDTO

@Entity("places")
data class PlaceEntity(
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val caption: String,
    val log: String? = null,
    @ColumnInfo("remote_id")
    val remoteId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)

fun PlaceEntity.toPlaceDTO(): PlaceDTO {
    val (lat, lng, plan, caption, log, _, localId) = this
    return PlaceDTO(lat, lng, plan, caption, log, localId)
}