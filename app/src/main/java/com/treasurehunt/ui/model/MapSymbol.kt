package com.treasurehunt.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapSymbol(
    val lat: Double,
    val lng: Double,
    val isPlan: Boolean = false,
    val caption: String,
    val remotePlanId: String? = null
) : Parcelable

fun MapSymbol.toPlace(): PlaceModel {
    val (lat, lng, isPlan, caption) = this
    return PlaceModel(
        lat,
        lng,
        false,
        caption
    )
}
