package com.treasurehunt.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("places")
data class PlaceEntity(
    val log: String,
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    @ColumnInfo("remote_id")
    val remoteId: String? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
)