package com.treasurehunt.ui.home

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class MapSymbol(
    val lat: Double,
    val lng: Double,
    val caption: String,
    val isPlan: Boolean = false,
    val remoteId: String? = null
) : Parcelable

