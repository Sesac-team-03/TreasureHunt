package com.treasurehunt.ui.model

import android.os.Parcelable
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapPlaceModel(
    val title: String,
    val link: String? = null,
    val category: String? = null,
    val description: String? = null,
    val telephone: String? = null,
    val address: String? = null,
    val roadAddress: String? = null,
    val mapx: String? = null,
    val mapy: String? = null,
    val position: LatLng? = null,
    val distance: String? = null
) : Parcelable