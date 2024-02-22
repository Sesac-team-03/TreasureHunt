package com.treasurehunt.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.treasurehunt.data.remote.model.PlaceDTO

@Entity("places")
data class PlaceEntity(
    val lat: Double,
    val lng: Double,
    val isPlan: Boolean,
    val caption: String,
    val remoteLogId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0,
    @ColumnInfo("remote_id")
    val remoteId: String? = null
)

fun PlaceEntity.toPlaceDTO() = PlaceDTO(lat, lng, isPlan, caption, remoteLogId, localId)