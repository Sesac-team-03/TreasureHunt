package com.treasurehunt.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("places")
data class PlaceEntity(
    val log: String,
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0
)