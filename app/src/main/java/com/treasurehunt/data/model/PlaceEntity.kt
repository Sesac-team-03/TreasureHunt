package com.treasurehunt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("places")
data class PlaceEntity(
    val lat: Double,
    val lng: Double,
    val plan: Boolean,
    val caption: String,
    val log: String? = null,
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0
)