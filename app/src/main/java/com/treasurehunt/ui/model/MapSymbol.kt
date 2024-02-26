package com.treasurehunt.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapSymbol(
    val lat: Double,
    val lng: Double,
    val isPlan: Boolean,
    val caption: String,
    val remotePlanId: String? = null
) : Parcelable

fun MapSymbol.toPlace() = PlaceModel(lat, lng, isPlan, caption)

